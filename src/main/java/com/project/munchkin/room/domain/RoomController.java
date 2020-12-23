package com.project.munchkin.room.domain;

import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.room.dto.RoomRequest;
import com.project.munchkin.room.dto.RoomResponse;
import com.project.munchkin.room.dto.RoomUpdateRequest;
import com.project.munchkin.room.exception.NotAuthorizedException;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.room.exception.RoomNameAlreadyExistsException;
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

    @PostMapping("/addRoom")
    public ResponseEntity<?> addRoom(@Valid @RequestBody RoomRequest roomRequest, @CurrentUser UserPrincipal currentUser) {
        try {
            RoomResponse roomResponse = roomFacade.addRoom(roomRequest, currentUser.getId());
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/byRoomId/{roomId}")
                    .buildAndExpand(roomResponse.getId()).toUri();
            return ResponseEntity.created(location).body(new ApiResponse <RoomResponse>(true, "Room created successfully", roomResponse));
        }catch (RoomNameAlreadyExistsException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/getRoom/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable Long roomId) {
        try {
            RoomResponse roomResponse = roomFacade.getRoom(roomId);
            return ResponseEntity.ok().body(new ApiResponse <RoomResponse>(true, "Room with id: " + roomId + " was found successfully", roomResponse));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/getAll/{page}/{pageSize}")
    public ResponseEntity<?> getPageableRooms(@PathVariable int page, @PathVariable int pageSize) {
        try{
            Page<RoomResponse> pageableRooms = roomFacade.getPageableRooms(page, pageSize);
            return ResponseEntity.ok().body(new ApiResponse <Page<RoomResponse>>(true, pageSize + " rooms in page: " + page + " was returned successfully", pageableRooms));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/search/{searchValue}/{page}/{pageSize}")
    public ResponseEntity<?> searchPageableRoom(@PathVariable String searchValue, @PathVariable int page, @PathVariable int pageSize) {
        try{
            Page<RoomResponse> pageableRooms = roomFacade.getPageableSearchedRoom(searchValue, page, pageSize);
            return ResponseEntity.ok().body(new ApiResponse <Page<RoomResponse>>(true, pageSize + " rooms in page: " + page + " was found successfully", pageableRooms));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PutMapping("/editRoom")
    public ResponseEntity<?> editRoom(@Valid @RequestBody RoomUpdateRequest roomUpdateRequest, @CurrentUser UserPrincipal currentUser) {
        try{
            RoomResponse roomResponse = roomFacade.editRoom(roomUpdateRequest, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse <RoomResponse>(true, "Room was edited successfully", roomResponse));
        }catch (ResourceNotFoundException | RoomNameAlreadyExistsException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @DeleteMapping("/deleteById/{roomId}")
    public ResponseEntity<?> deleteRoom (@PathVariable Long roomId, @CurrentUser UserPrincipal currentUser){
        try{
            roomFacade.deleteRoom(roomId, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse <>(true, "Room was deleted successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }
}