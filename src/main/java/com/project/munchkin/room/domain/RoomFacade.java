package com.project.munchkin.room.domain;

import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.playerStatus.domain.PlayerStatusFacade;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import com.project.munchkin.playerStatus.model.PlayerStatus;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.room.dto.RoomDto;
import com.project.munchkin.room.dto.RoomRequest;
import com.project.munchkin.room.dto.RoomResponse;
import com.project.munchkin.room.dto.RoomUpdateRequest;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.room.model.Room;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.model.User;
import com.project.munchkin.user.repository.UserRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@Builder
public class RoomFacade {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    PlayerStatusRepository playerStatusRepository;

    @Autowired
    PlayerStatusFacade playerStatusFacade;

    public ResponseEntity<?> addRoom(RoomRequest roomRequest, UserPrincipal currentUser) {
        if (playerStatusFacade.playerIsInAnyRoom(currentUser.getId())) {
            return new ResponseEntity<>("Can't create new room while being in one", HttpStatus.BAD_REQUEST);
        }else {
            Room room = Room.builder()
                    .roomName(roomRequest.getRoomName())
                    .slots(roomRequest.getSlots())
                    .usersInRoom(0L)
                    .isComplete(false)
                    .user(getUser(currentUser.getId()))
                    .roomPassword(roomRequest.getRoomPassword())
                    .build();

            roomRepository.save(room);
            playerStatusFacade.joinRoom(room.getId(), room.getRoomPassword(), currentUser);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/byRoomId/{roomId}")
                    .buildAndExpand(room.getId()).toUri();

            ResponseEntity<ApiResponse> responseEntity = ResponseEntity.created(location)
                    .body(new ApiResponse(true, "Room Created Successfully"));

            return responseEntity;
        }
    }

    public RoomResponse getRoomByRoomId(Long roomId) {
        RoomDto roomDto = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId)).dto();
        return mapRoomDtoToRoomResponse(roomDto);
    }

    public Page<RoomResponse> getAllRooms(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Room> rooms = roomRepository.findAll(pageable);
        return rooms.map(Room::response);
    }

    public RoomResponse editRoom(RoomUpdateRequest roomUpdateRequest) {
        RoomDto roomDto = roomRepository.findById(roomUpdateRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomUpdateRequest.getId())).dto();

        if(!roomUpdateRequest.getRoomName().isEmpty()){
            roomDto.setRoomName(roomUpdateRequest.getRoomName());
        }
        if(roomDto.getUsersInRoom() < roomUpdateRequest.getSlots()){
            roomDto.setSlots(roomUpdateRequest.getSlots());
        }
        if(!roomUpdateRequest.getRoomPassword().isEmpty()){
            roomDto.setRoomPassword(roomUpdateRequest.getRoomPassword());
        }

        roomRepository.save(Room.fromDto(roomDto));

        return mapRoomDtoToRoomResponse(roomDto);
    }

    public ResponseEntity deleteRoomById(Long roomId) {
        List<PlayerStatus> allPlayersStatuses = playerStatusRepository.findAllPlayerStatusByRoomId(roomId);
        if (allPlayersStatuses.isEmpty()){
            roomRepository.deleteById(roomId);
            return ResponseEntity.ok("Room was deleted");
        }

        roomRepository.deleteById(roomId);
        playerStatusRepository.deleteAll(allPlayersStatuses);

        return ResponseEntity.ok("Room and all player statuses in it were deleted");
    }

    public List<PlayerStatusResponse> getGameSummary(Long roomId) {
        List<PlayerStatusResponse> allPlayersStatusesInRoom = playerStatusFacade.getAllPlayersStatusesInRoom(roomId);

        allPlayersStatusesInRoom.sort((p1, p2) -> {
            if(p1.getPlayerLevel() == p2.getPlayerLevel()){
                return 0;
            }
            return p1.getPlayerLevel() >= p2.getPlayerLevel() ? -1 : 1;
        });

        return allPlayersStatusesInRoom;
    }

    private RoomResponse mapRoomDtoToRoomResponse(RoomDto roomDto) {
        return RoomResponse.builder()
                .id(roomDto.getId())
                .roomName(roomDto.getRoomName())
                .slots(roomDto.getSlots())
                .usersInRoom(roomDto.getUsersInRoom())
                .isComplete(roomDto.isComplete())
                .creatorId(roomDto.getUser().getId())
                .roomPassword(roomDto.getRoomPassword())
                .build();
    }

    private User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));
    }
}
