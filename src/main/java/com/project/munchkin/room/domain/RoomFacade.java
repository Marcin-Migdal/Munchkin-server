package com.project.munchkin.room.domain;

import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.room.dto.RoomDto;
import com.project.munchkin.room.dto.RoomRequest;
import com.project.munchkin.room.dto.RoomResponse;
import com.project.munchkin.room.dto.RoomUpdateRequest;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.room.model.Room;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.domain.UserFacade;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@Builder
public class RoomFacade {

    @Autowired
    UserFacade userFacade;

    @Autowired
    RoomRepository roomRepository;

    public RoomDto addRoom(RoomRequest roomRequest, UserPrincipal currentUser) {
        Room room = Room.builder()
                .roomName(roomRequest.getRoomName())
                .slots(roomRequest.getSlots())
                .usersInRoom(1L)
                .isComplete(false)
                .user(userFacade.getUser(currentUser.getId()))
                .roomPassword(roomRequest.getRoomPassword())
                .build();

        roomRepository.save(room);
        return room.dto();
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

    public RoomResponse editRoom(@Valid RoomUpdateRequest roomUpdateRequest) {
        RoomDto roomDto = roomRepository.findById(roomUpdateRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomUpdateRequest.getId())).dto();

        roomDto.setRoomName(roomUpdateRequest.getRoomName());
        roomDto.setSlots(roomUpdateRequest.getSlots());
        roomDto.setRoomPassword(roomUpdateRequest.getRoomPassword());

        roomRepository.save(Room.fromDto(roomDto));

        return mapRoomDtoToRoomResponse(roomDto);
    }

    public void deleteById(Long roomId) {
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

    public Room getRoomEntity(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));
    }
}
