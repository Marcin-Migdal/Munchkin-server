package groovy.com.project.munchkin.rooms

import com.project.munchkin.base.security.JwtTokenProvider
import com.project.munchkin.playerStatus.domain.PlayerStatusFacade
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository
import com.project.munchkin.room.exception.ResourceNotFoundException
import groovy.com.project.munchkin.playerStatus.PlayerStatusInMemoryRepository
import groovy.com.project.munchkin.utils.MunchkinTestUtils
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class PlayerStatusSpec extends Specification {
    private PlayerStatusRepository playerStatusRepository = new PlayerStatusInMemoryRepository();

    AuthenticationManager authenticationManager = Stub(AuthenticationManager)
    PasswordEncoder passwordEncoder = Stub(PasswordEncoder)
    JwtTokenProvider tokenProvider = Stub(JwtTokenProvider)

    MunchkinTestUtils munchkinTestUtils = new MunchkinTestUtils(playerStatusRepository, authenticationManager, tokenProvider, passwordEncoder)

    private PlayerStatusFacade playerStatusFacade = munchkinTestUtils.getPlayerStatusFacade();

    def "user is able to get race"() {
        when: "user tries to get race"
        def playerRaceDto = playerStatusFacade.getRace(0)
        then: "user get race successfully"
        playerRaceDto
    }

    def "user is able to get all races"() {
        when: "user tries to get all races"
        def allRaces = playerStatusFacade.getAllRaces()
        then: "user get all races successfully"
        !allRaces.isEmpty()
    }

    def "user is able to get class"() {
        when: "user tries to get class"
        def playerclassDto = playerStatusFacade.getClass(0)
        then: "user get class successfully"
        playerclassDto
    }

    def "user is able to get all classes"() {
        when: "user tries to get all classes"
        def allClasses = playerStatusFacade.getAllClasses()
        then: "user get all classes successfully"
        !allClasses.isEmpty()
    }

    def "user is able to join room"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Morti", "morti1234",
                "morti1234@gmail.com", "morti4321", "male")
        def roomResponse = munchkinTestUtils.createRoom("First Testing Room", 4L, "SecretPassword123", userResponse.getId())
        when: "user is joining room"
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        then: "user successfully joined a room"
        def room = munchkinTestUtils.roomFacade.getRoom(roomResponse.getId())
        def playerStatusResponse = playerStatusFacade.getPlayerStatusResponseByRoomId(room.getId(), userResponse.getId())

        room.getUsersInRoom() == 1 && playerStatusResponse.isPlayerInRoom()
    }

    def "user is able to leave room"() {
        given: "there is user in a room"
            def userResponse = munchkinTestUtils.registerUser("Ghost", "Ghost1234",
                    "Ghost1234@gmail.com", "Ghost4321", "male")
            def roomResponse = munchkinTestUtils.createRoom("Second Testing Room", 4L, "SecretPassword123", userResponse.getId())
            playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        when: "user tries to leave a room"
            playerStatusFacade.exitRoom(roomResponse.getId(), userResponse.getId())
        then: "user leaved the room"
            def room = munchkinTestUtils.roomFacade.getRoom(roomResponse.getId())
            def playerStatusResponse = playerStatusFacade.getPlayerStatusResponseByRoomId(room.getId(), userResponse.getId())

            room.getUsersInRoom() == 0 && !playerStatusResponse.isPlayerInRoom()
    }

    def "user is able to get his own player status"() {
        given: "there is user in a room"
            def userResponse = munchkinTestUtils.registerUser("John", "John1234",
                    "John1234@gmail.com", "John4321", "male")
            def roomResponse = munchkinTestUtils.createRoom("Third Testing Room", 8L, "SecretWord", userResponse.getId())
            playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        when: "user tries to get his own player status"
        def playerStatusResponse = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        then: "user successfully gets his playerStatus"
        playerStatusResponse
    }

    def "user is able to get player status"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Alice", "Alice1234",
                "Alice1234@gmail.com", "Alice4321", "female")
        def roomResponse = munchkinTestUtils.createRoom("Forth Testing Room", 8L, "SecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponseByRoomId = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        when: "user tries to get player status"
        def playerStatusResponse = playerStatusFacade.getPlayerStatusResponseById(playerStatusResponseByRoomId.getId())
        then: "user successfully gets player status"
        playerStatusResponse
    }

    def "user is able to get all player statuses in room"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Frank", "Frank1234",
                "Frank1234@gmail.com", "Frank1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Fifth Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        when: "user tries to get all player statuses in one room"
        def allPlayersStatusResponse = playerStatusFacade.getAllPlayersStatusesResponse(roomResponse.getId())
        then: "user successfully gets all player statuses in one room"
        !allPlayersStatusResponse.isEmpty()
    }

    def "user can set player level"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Paul", "Paul1234",
                "Paul1234@gmail.com", "Paul1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Sixth Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponseBefore = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        when: "user tries to set player level "
        playerStatusFacade.setPlayerLevel(playerStatusResponseBefore.getId(), 1)
        then: "user successfully sets player level"
        def playerStatusResponseAfter = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        playerStatusResponseAfter.getPlayerLevel() > playerStatusResponseBefore.getPlayerLevel()
    }

    def "user can set player bonus"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Louis", "Louis1234",
                "Louis1234@gmail.com", "Louis1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Seventh Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponseBefore = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        when: "user tries to set player bonus "
        playerStatusFacade.setPlayerBonus(playerStatusResponseBefore.getId(), 1)
        then: "user successfully sets player bonus"
        def playerStatusResponseAfter = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        playerStatusResponseAfter.getPlayerBonus() > playerStatusResponseBefore.getPlayerBonus()
    }

    def "user can change player gender"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Teddy", "Teddy1234",
                "Teddy1234@gmail.com", "Teddy1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Eighth Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponseBefore = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        when: "user tries to change player gender "
        playerStatusFacade.changeGender(playerStatusResponseBefore.getId())
        then: "user successfully changed player gender"
        def playerStatusResponseAfter = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        playerStatusResponseAfter.getGender() != playerStatusResponseBefore.getGender()
    }

    def "user can change player race"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Sam", "Sam1234",
                "Sam1234@gmail.com", "Sam1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Ninth Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponseBefore = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        when: "user tries to change player race"
        playerStatusFacade.setFirstRace(playerStatusResponseBefore.getId(), 1)
        then: "user successfully changed player race"
        def playerStatusResponseAfter = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        playerStatusResponseAfter.getPlayerRaceDto().getId() == 1
    }

    def "user can allow for player to have two races"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Harry", "Harry1234",
                "Harry1234@gmail.com", "Harry1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Tenth Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponse = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        when: "user tries to allow player to have two races"
        def isTwoRaces = playerStatusFacade.toggleTwoRaces(playerStatusResponse.getId())
        then: "player can have two races"
        isTwoRaces==true
    }

    def "user can change second player race"() {
        given: "there is user in a room and player can have second race"
        def userResponse = munchkinTestUtils.registerUser("Rick", "Rick1234",
                "Rick1234@gmail.com", "Rick1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Eleventh Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponseBefore = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        playerStatusFacade.toggleTwoRaces(playerStatusResponseBefore.getId())
        when: "user tries to change second player race "
        playerStatusFacade.setSecondRace(playerStatusResponseBefore.getId(), 1)
        then: "user successfully changed second player race"
        def playerStatusResponseAfter = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        playerStatusResponseAfter.getSecondPlayerRaceDto().getId() == 1
    }

    ///////////////////

    def "user can change player class"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Mark", "Mark1234",
                "Mark1234@gmail.com", "Mark1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Twelve Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponseBefore = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        when: "user tries to change player class"
        playerStatusFacade.setFirstClass(playerStatusResponseBefore.getId(), 1)
        then: "user successfully changed player class"
        def playerStatusResponseAfter = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        playerStatusResponseAfter.getPlayerClassDto().getId() == 1
    }

    def "user can allow for player to have two classes"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Richard", "Richard1234",
                "Richard1234@gmail.com", "Richard1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Thirteenth Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponse = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        when: "user tries to allow player to have two classes"
        def isTwoClasses = playerStatusFacade.toggleTwoClasses(playerStatusResponse.getId())
        then: "player can have two classes"
        isTwoClasses
    }

    def "user can change player second class"() {
        given: "there is user in a room and player can have second class"
        def userResponse = munchkinTestUtils.registerUser("Tony", "Tony1234",
                "Tony1234@gmail.com", "Tony1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Fourteenth Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponseBefore = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        playerStatusFacade.toggleTwoClasses(playerStatusResponseBefore.getId())
        when: "user tries to change second player class "
        playerStatusFacade.setSecondClass(playerStatusResponseBefore.getId(), 1)
        then: "user successfully changed second player class"
        def playerStatusResponseAfter = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        playerStatusResponseAfter.getSecondPlayerClassDto().getId() == 1
    }

    def "user can delete player status"() {
        given: "there is user in a room"
        def userResponse = munchkinTestUtils.registerUser("Jack", "Jack1234",
                "Jack1234@gmail.com", "Jack1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Fifteenth Testing Room", 3L, "WordButItsASecretWord", userResponse.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse.getId())
        def playerStatusResponse = playerStatusFacade.getPlayerStatusResponseByRoomId(roomResponse.getId(), userResponse.getId())
        when: "user tries to delete player status"
        playerStatusFacade.deletePlayerStatus(playerStatusResponse.getId(), userResponse.getId())
        then: "user successfully deleted player status"
        !playerStatusRepository.existsById(playerStatusResponse.getId())
    }

    def "user can delete all player statuses in the room"() {
        given: "there are users in a room"
        def userResponse1 = munchkinTestUtils.registerUser("Oliver", "Oliver1234",
                "Oliver1234@gmail.com", "Oliver1234", "male")
        def userResponse2 = munchkinTestUtils.registerUser("William", "William1234",
                "William1234@gmail.com", "William1234", "male")
        def roomResponse = munchkinTestUtils.createRoom("Sixteenth Testing Room", 3L, "WordButItsASecretWord", userResponse1.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse1.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse2.getId())
        when: "user tries to delete all player statuses in the room"
        playerStatusFacade.deletePlayersStatuses(roomResponse.getId(), userResponse1.getId())
        playerStatusFacade.getAllPlayersStatusesResponse(roomResponse.getId())
        then: "user successfully deleted all player statuses"
        thrown ResourceNotFoundException
    }

    def "user is able to get game summary"() {
        given: "there is user and room with few users in it"
        def userResponse1 = munchkinTestUtils.registerUser("Peter1", "Peter1",
                "Peter1@gmail.com", "Peter43", "male")
        def userResponse2 = munchkinTestUtils.registerUser("Peter2", "Peter2",
                "Peter2@gmail.com", "Peter21", "male")
        def userResponse3 = munchkinTestUtils.registerUser("Peter3", "Peter3",
                "Peter3@gmail.com", "Peter41", "female")

        def roomResponse = munchkinTestUtils.createRoom("Seventh Testing Room", 3L, "WordButItsASecretWord", userResponse1.getId())

        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse1.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse2.getId())
        playerStatusFacade.joinRoom(roomResponse.getId(), roomResponse.getRoomPassword(), userResponse3.getId())

        when: "user tries to get game summary"
        def gameSummary = playerStatusFacade.getGameSummary(roomResponse.getId())
        then: "user gets game summary successfully"
        !gameSummary.isEmpty()
    }
}
