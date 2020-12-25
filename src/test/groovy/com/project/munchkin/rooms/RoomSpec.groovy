package groovy.com.project.munchkin.rooms

import com.project.munchkin.base.security.JwtTokenProvider
import com.project.munchkin.room.domain.RoomFacade
import com.project.munchkin.room.dto.RoomUpdateRequest
import com.project.munchkin.room.repository.RoomRepository
import com.project.munchkin.user.dto.UserResponse
import groovy.com.project.munchkin.utils.MunchkinTestUtils
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class RoomSpec extends Specification {
    AuthenticationManager authenticationManager = Stub(AuthenticationManager)
    PasswordEncoder passwordEncoder = Stub(PasswordEncoder)
    JwtTokenProvider tokenProvider = Stub(JwtTokenProvider)

    private RoomRepository roomRepository = new RoomInMemoryRepository()
    MunchkinTestUtils munchkinTestUtils = new MunchkinTestUtils(roomRepository, authenticationManager, tokenProvider, passwordEncoder)

    RoomFacade roomFacade = munchkinTestUtils.getRoomFacade()

    def "user can create room"() {
        given: "there is user "
        UserResponse userResponse = munchkinTestUtils.registerUser("Morti", "morti1234",
                "morti1234@gmail.com", "morti4321", "male")
        when: "user is creating room"
        def roomResponse = munchkinTestUtils.createRoom("First Testing Room", 4L, "SecretPassword123", userResponse.getId())
        then: "room is created successfully"
        roomRepository.existsById(roomResponse.getId())
    }


    def "user can get a pageable rooms"() {
        given: "there is user and rooms"
        UserResponse userResponse = munchkinTestUtils.registerUser("Ghost", "Ghost1234",
                "Ghost1234@gmail.com", "Ghost4321", "male")

        munchkinTestUtils.createRoom("Second Testing Room", 5L, "Secret123", userResponse.getId())
        munchkinTestUtils.createRoom("SecondSecond Testing Room", 5L, "Secret123", userResponse.getId())
        when: "user is trying to get rooms"
        def pageableRooms = roomFacade.getPageableRooms(0, 2)
        then: "user got pageable rooms"
        println pageableRooms.getContent().get(0).getRoomName()
        !pageableRooms.isEmpty()
    }

    def "user is able to edit his room"() {
        given: "there is user and room"
        UserResponse userResponse = munchkinTestUtils.registerUser("John", "John1234",
                "John1234@gmail.com", "John4321", "male")

        def roomResponse = munchkinTestUtils.createRoom("Third Testing Room", 8L, "SecretWord", userResponse.getId())
        when: "user is editing his room "
        def updateRequest = RoomUpdateRequest.builder()
                .id(roomResponse.getId())
                .roomName("Third Edited Testing Room")
                .slots(4L)
                .roomPassword("EditedSecretWord")
                .build()

        def editedRoom = roomFacade.editRoom(updateRequest, userResponse.getId())
        then: "room is successfully edited"
        editedRoom.getRoomName() == updateRequest.getRoomName()&&
                editedRoom.getSlots() == updateRequest.getSlots()
    }

    def "user is able to delete room"() {
        given: "there is user and room"
        def userResponse =  munchkinTestUtils.registerUser("Alice", "Alice1234",
                "Alice1234@gmail.com", "Alice4321", "female")

        def roomResponse = munchkinTestUtils.createRoom("Forth Testing Room", 8L, "SecretWord", userResponse.getId())
        when: "user is deleting his room "
        roomFacade.deleteRoom(roomResponse.getId(), userResponse.getId())
        then: "room is deleted successfully"
        !roomRepository.existsById(roomResponse.getId())
    }

    def "user is able to search rooms "() {
        given: "there is user and a room"
        def userResponse = munchkinTestUtils.registerUser("Frank", "Frank1234",
                "Frank1234@gmail.com", "Frank1234", "male")
        munchkinTestUtils.createRoom("Sixth Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        when: "user search for room"
        def pageableRoomResponse = roomFacade.getPageableSearchedRoom("S", 0, 1)
        then: "user gets room"
        !pageableRoomResponse.isEmpty()
    }
}
