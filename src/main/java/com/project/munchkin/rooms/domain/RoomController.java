package com.project.munchkin.rooms.domain;

import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.rooms.dto.RoomDto;
import com.project.munchkin.rooms.dto.RoomRequest;
import com.project.munchkin.rooms.dto.RoomResponse;
import com.project.munchkin.rooms.dto.RoomUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    RoomFacade roomFacade;

    @PostMapping
    public ResponseEntity<?> addRoom(@Valid @RequestBody RoomRequest roomRequest, @CurrentUser UserPrincipal currentUser) {
        RoomDto roomDto = roomFacade.addRoom(roomRequest, currentUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/byRoomId/{roomId}")
                .buildAndExpand(roomDto.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Room Created Successfully"));
    }

    @GetMapping("/byId/{roomId}")
    public RoomResponse getRoom(@PathVariable Long roomId){
        return roomFacade.getRoomByRoomId(roomId);
    }

    @GetMapping("/getAllPageable/{page}/{pageSize}")
    public Page<RoomResponse> getRoomFacade(@PathVariable int page, @PathVariable int pageSize){
        return roomFacade.getAllRooms(page, pageSize);
    }

    @DeleteMapping("/byId/{roomId}")
    public ResponseEntity<?> deleteById(@PathVariable Long roomId){
        roomFacade.deleteById(roomId);
        return ResponseEntity.ok("Room deleted");
    }

    @PutMapping
    public RoomResponse editRoom(@Valid @RequestBody RoomUpdateRequest roomUpdateRequest){
        return roomFacade.editRoom(roomUpdateRequest);
    }
}