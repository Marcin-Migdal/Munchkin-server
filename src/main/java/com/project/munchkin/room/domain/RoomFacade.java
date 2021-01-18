package com.project.munchkin.room.domain;

import com.project.munchkin.playerStatus.model.PlayerStatus;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.room.dto.*;
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
        if(roomRepository.existsByRoomName(roomRequest.getRoomName())) {
            throw new RoomNameAlreadyExistsException("Room by name: " + roomRequest.getRoomName() + " already exists", HttpStatus.BAD_REQUEST);
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
        Page<Room> rooms = roomRepository.findAllComplete(PageRequest.of(page, pageSize));
        return rooms.map(Room::response);
    }

    public Page<RoomResponse> getPageableSearchedRoom(String searchValue, int page, int pageSize) {
        Page<Room> roomPage = roomRepository.searchPageableRoom(searchValue, PageRequest.of(page, pageSize, Sort.by("roomName")));
        if(roomPage.isEmpty()) {
            throw new ResourceNotFoundException("room", "room name", searchValue, HttpStatus.NOT_FOUND);
        }
        return roomPage.map(Room::response);
    }

    public RoomResponse editRoom(RoomUpdateRequest roomUpdateRequest, Long userId) {
        RoomDto roomDto = roomRepository.findById(roomUpdateRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomUpdateRequest.getId(), HttpStatus.NOT_FOUND)).dto();

        if(roomRepository.existsByRoomName(roomUpdateRequest.getRoomName())) {
            throw new RoomNameAlreadyExistsException("Room by name: " + roomUpdateRequest.getRoomName() + " already exists", HttpStatus.BAD_REQUEST);
        }

        isAuthorized(userId, roomDto.getUser().getId(), "edit this room");

        if(!roomUpdateRequest.getRoomName().isEmpty()){
            roomDto.setRoomName(roomUpdateRequest.getRoomName());
        }if(roomDto.getUsersInRoom() <= roomUpdateRequest.getSlots()){
            roomDto.setSlots(roomUpdateRequest.getSlots());
        }if(!roomUpdateRequest.getRoomPassword().isEmpty()){
            roomDto.setRoomPassword(roomUpdateRequest.getRoomPassword());
        }

        Room save = roomRepository.save(Room.fromDto(roomDto));
        return save.response();
    }

    public void deleteRoom(Long roomId, Long userId) {
        RoomResponse roomResponse = getRoom(roomId);
        isAuthorized(userId, roomResponse.getCreatorId(), "delete this room");
        if (roomResponse.getUsersInRoom() > 0L) {
            List<PlayerStatus> allPlayersStatuses = playerStatusRepository.findAllPlayerStatusesByRoomId(roomId);
            playerStatusRepository.deleteAll(allPlayersStatuses);
        }
        roomRepository.deleteById(roomId);
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
