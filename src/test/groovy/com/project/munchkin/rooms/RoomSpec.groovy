package groovy.com.project.munchkin.rooms


import com.project.munchkin.rooms.domain.RoomFacade
import com.project.munchkin.rooms.domain.RoomFacadeCreator
import com.project.munchkin.rooms.repository.RoomRepository
import com.project.munchkin.users.domain.UserFacade
import spock.lang.Specification

class RoomSpec extends Specification {
    UserFacade userFacade = Mock(UserFacade.class)
    RoomRepository roomRepository = new RoomInMemoryRespository()

    RoomFacade roomFacade = RoomFacadeCreator.createRoomFacade(userFacade, roomRepository)

}
