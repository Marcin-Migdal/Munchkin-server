package groovy.com.project.munchkin.utils

import com.project.munchkin.base.security.JwtTokenProvider
import com.project.munchkin.playerStatus.domain.PlayerStatusFacade
import com.project.munchkin.playerStatus.domain.PlayerStatusFacadeCreator
import com.project.munchkin.playerStatus.dto.EditRequest.BonusLevelEditRequest
import com.project.munchkin.playerStatus.model.PlayerClass
import com.project.munchkin.playerStatus.model.PlayerRace
import com.project.munchkin.playerStatus.repository.PlayerClassRepository
import com.project.munchkin.playerStatus.repository.PlayerRaceRepository
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository
import com.project.munchkin.room.domain.RoomFacade
import com.project.munchkin.room.domain.RoomFacadeCreator
import com.project.munchkin.room.dto.RoomRequest
import com.project.munchkin.room.dto.RoomResponse
import com.project.munchkin.room.repository.RoomRepository
import com.project.munchkin.user.domain.UserFacade
import com.project.munchkin.user.domain.UserFacadeCreator
import com.project.munchkin.user.dto.UserResponse
import com.project.munchkin.user.dto.authRequests.SignUpRequest
import com.project.munchkin.user.repository.UserRepository
import groovy.com.project.munchkin.playerStatus.PlayerClassInMemoryRepository
import groovy.com.project.munchkin.playerStatus.PlayerRaceInMemoryRepository
import groovy.com.project.munchkin.playerStatus.PlayerStatusInMemoryRepository
import groovy.com.project.munchkin.rooms.RoomInMemoryRepository
import groovy.com.project.munchkin.users.UserInMemoryRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder

class MunchkinTestUtils {
    UserRepository userRepository = new UserInMemoryRepository()
    RoomRepository roomRepository
    PlayerStatusRepository playerStatusRepository
    PlayerClassRepository playerClassRepository
    PlayerRaceRepository playerRaceRepository

    UserFacade userFacade
    RoomFacade roomFacade
    PlayerStatusFacade playerStatusFacade

    MunchkinTestUtils(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userFacade = UserFacadeCreator.createUserFacade(this.userRepository, authenticationManager, passwordEncoder, tokenProvider)
    }

    MunchkinTestUtils(RoomRepository roomRepository,
                        AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {

        this.playerStatusRepository = new PlayerStatusInMemoryRepository()
        this.userFacade = UserFacadeCreator.createUserFacade(this.userRepository, authenticationManager, passwordEncoder, tokenProvider)
        this.roomFacade = RoomFacadeCreator.createRoomFacade(this.userRepository, roomRepository, this.playerStatusRepository)
    }

    MunchkinTestUtils(PlayerStatusRepository playerStatusRepository,
                        AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {

        this.roomRepository = new RoomInMemoryRepository()
        this.playerClassRepository = new PlayerClassInMemoryRepository();
        this.playerRaceRepository = new PlayerRaceInMemoryRepository();
        createRacesAndClasses()
        this.userFacade = UserFacadeCreator.createUserFacade(this.userRepository, authenticationManager, passwordEncoder, tokenProvider)
        this.roomFacade = RoomFacadeCreator.createRoomFacade(this.userRepository, roomRepository, playerStatusRepository)
        this.playerStatusFacade= PlayerStatusFacadeCreator.createPlayerStatusFacade(this.userRepository, this.roomRepository, playerStatusRepository,
                                                                                        this.playerRaceRepository, this.playerClassRepository);
    }

    UserResponse registerUser(String inGameName, String username, String email, String userPassword, String gender) {
        userFacade.registerUser(SignUpRequest.builder()
                .inGameName(inGameName)
                .username(username)
                .email(email)
                .userPassword(userPassword)
                .gender(gender)
                .build())
    }

    RoomResponse createRoom(String roomName, Long slots, String roomPassword, Long userId) {
        def roomRequest = RoomRequest.builder()
                .roomName(roomName)
                .slots(slots)
                .roomPassword(roomPassword)
                .build()

        return roomFacade.addRoom(roomRequest, userId)
    }

    BonusLevelEditRequest createBonusLevelEditRequest(Long playerStatusId,Long level) {
        return BonusLevelEditRequest.builder()
                .playerStatusId(playerStatusId)
                .levelValue(level)
                .bonusValue(5)
                .build()
    }

    void createRacesAndClasses(){
        def playerRaceHuman = PlayerRace.builder()
                .id(0L)
                .raceName("Human")
                .raceIcon("none")
                .raceDescription("Nothing interesting")
                .build()

        def playerRaceElf = PlayerRace.builder()
                .id(1L)
                .raceName("Elf")
                .raceIcon("none")
                .raceDescription("Have long ears")
                .build()

        def playerRaceDwarf = PlayerRace.builder()
                .id(2L)
                .raceName("Dwarf")
                .raceIcon("none")
                .raceDescription("Is short")
                .build()

        def playerClassDefault = PlayerClass.builder()
                .id(0L)
                .className("Default")
                .classIcon("none")
                .classDescription("Just normal being")
                .build()

        def playerClassWizard = PlayerClass.builder()
                .id(1L)
                .className("Wizard")
                .classIcon("none")
                .classDescription("Can use magic")
                .build()

        def playerClassBard = PlayerClass.builder()
                .id(2L)
                .className("Bard")
                .classIcon("none")
                .classDescription("Can sing")
                .build()

        playerRaceRepository.save(playerRaceHuman)
        playerRaceRepository.save(playerRaceElf)
        playerRaceRepository.save(playerRaceDwarf)

        playerClassRepository.save(playerClassDefault)
        playerClassRepository.save(playerClassWizard)
        playerClassRepository.save(playerClassBard)
    }
}
