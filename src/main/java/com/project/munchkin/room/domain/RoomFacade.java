package com.project.munchkin.room.domain;

import com.project.munchkin.playerStatus.model.PlayerStatus;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.room.dto.RoomDto;
import com.project.munchkin.room.dto.RoomRequest;
import com.project.munchkin.room.dto.RoomResponse;
import com.project.munchkin.room.dto.RoomUpdateRequest;
import com.project.munchkin.room.exception.NotAuthorizedException;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.room.exception.RoomNameAlreadyExistsException;
import com.project.munchkin.room.model.Room;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.model.User;
import com.project.munchkin.user.repository.UserRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
public class RoomFacade {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlayerStatusRepository playerStatusRepository;

    public RoomResponse addRoom(RoomRequest roomRequest, Long userId) {
        if (roomRepository.existsByRoomName(roomRequest.getRoomName())) {
            throw new RoomNameAlreadyExistsException("RoomName " + roomRequest.getRoomName() + " is already taken!", HttpStatus.BAD_REQUEST);
        }

        Room room = Room.builder()
                .roomName(roomRequest.getRoomName())
                .slots(roomRequest.getSlots())
                .usersInRoom(0L)
                .isComplete(false)
                .user(getUser(userId))
                .roomPassword(roomRequest.getRoomPassword())
                .build();

        roomRepository.save(room);
        return room.response();
    }

    public RoomResponse getRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId, HttpStatus.NOT_FOUND));
        return room.response();
    }

    public Page<RoomResponse> getPageableRooms(int page, int pageSize) {
        Page<Room> rooms = roomRepository.findAllInComplete(PageRequest.of(page, pageSize));
        if(rooms.isEmpty()){
            throw new ResourceNotFoundException("Rooms", "page", page, HttpStatus.NOT_FOUND);
        }
        return rooms.map(Room::response);
    }

    public Page<RoomResponse> searchPageableRoom(String searchValue, int page, int pageSize) {
        Page<Room> roomPage = roomRepository.searchPageableRoom(searchValue, PageRequest.of(page, pageSize, Sort.by("roomName")));
        if(roomPage.isEmpty()) {
            throw new ResourceNotFoundException("room", "room name", searchValue, HttpStatus.NOT_FOUND);
        }
        return roomPage.map(Room::response);
    }

    public RoomResponse editRoom(RoomUpdateRequest roomUpdateRequest, Long userId) {
        RoomDto roomDto = roomRepository.findById(roomUpdateRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomUpdateRequest.getId(), HttpStatus.NOT_FOUND)).dto();

        isAuthorized(userId, roomDto.getUser().getId(), "edit this room");

        if(!roomUpdateRequest.getRoomName().isEmpty()){
            roomDto.setRoomName(roomUpdateRequest.getRoomName());
        }if(roomDto.getUsersInRoom() <= roomUpdateRequest.getSlots()){
            roomDto.setSlots(roomUpdateRequest.getSlots());
        }if(!roomUpdateRequest.getRoomPassword().isEmpty()){
            roomDto.setRoomPassword(roomUpdateRequest.getRoomPassword());
        }

        roomRepository.save(Room.fromDto(roomDto));
        return mapRoomDtoToRoomResponse(roomDto);
    }

    public void deleteRoom(Long roomId, Long userId) {
        RoomResponse roomResponse = getRoom(roomId);
        isAuthorized(userId, roomResponse.getCreatorId(), "delete this room");
        if (roomResponse.getUsersInRoom() > 0L) {
            List<PlayerStatus> allPlayersStatuses = playerStatusRepository.findAllPlayerStatusByRoomId(roomId);
            playerStatusRepository.deleteAll(allPlayersStatuses);
        }
        roomRepository.deleteById(roomId);
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
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId, HttpStatus.NOT_FOUND));
    }

    private void isAuthorized(Long userId, Long creatorId, String message) {
        if(!userId.equals(creatorId)){
            throw new NotAuthorizedException(message, HttpStatus.UNAUTHORIZED);
        }
    }
}
