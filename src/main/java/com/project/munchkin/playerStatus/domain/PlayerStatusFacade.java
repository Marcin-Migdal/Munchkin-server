package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.playerStatus.dto.PlayerClass.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRace.PlayerRaceDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import com.project.munchkin.playerStatus.model.PlayerClass;
import com.project.munchkin.playerStatus.model.PlayerRace;
import com.project.munchkin.playerStatus.model.PlayerStatus;
import com.project.munchkin.playerStatus.repository.PlayerClassRepository;
import com.project.munchkin.playerStatus.repository.PlayerRaceRepository;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.room.domain.RoomFacade;
import com.project.munchkin.room.dto.RoomDto;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.user.domain.UserFacade;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Builder
public class PlayerStatusFacade {

    @Autowired
    UserFacade userFacade;

    @Autowired
    RoomFacade roomFacade;

    @Autowired
    PlayerStatusRepository playerStatusRepository;

    @Autowired
    PlayerRaceRepository playerRaceRepository;

    @Autowired
    PlayerClassRepository playerClassRepository;


    public PlayerRaceDto getPlayerRace(Long raceId) {
        return playerRaceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race", "Race Id", raceId)).dto();
    }

    public List<PlayerRaceDto> getAllPlayerRaces() {
        List<PlayerRaceDto> playerRaceDtos = playerRaceRepository.findAll()
                .stream()
                .map(PlayerRace::dto)
                .collect(Collectors.toList());
        return playerRaceDtos;
    }

    public PlayerClassDto getPlayerClass(Long classId) {
        return playerClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "Class Id", classId)).dto();
    }

    public List<PlayerClassDto> getAllPlayerClasses() {
        List<PlayerClassDto> playerClassDtos = playerClassRepository.findAll()
                .stream()
                .map(PlayerClass::dto)
                .collect(Collectors.toList());
        return playerClassDtos;
    }

    public PlayerStatusResponse getPlayerStatus(Long roomId , UserPrincipal currentUser) {
        PlayerStatus playerStatusEntity = getPlayerStatusEntity(roomId, currentUser.getId());
        return toPlayerStatusResponse(playerStatusEntity);
    }

    public ResponseEntity joinRoom(Long roomId, String roomPassword, UserPrincipal currentUser) {

        if(!playerStatusRepository.playerIsInAnyRoom(currentUser.getId())){}
        else{
            return ResponseEntity.ok("You already in another room, to join this room please leave room you are already in");
        }

        if(roomFacade.roomFull(roomId)){
            return ResponseEntity.ok("Room is full");
        }

        RoomDto roomDto = roomFacade.getRoomDto(roomId);
        if (roomDto.getRoomPassword().equals(roomPassword)) {
            try {
                PlayerStatus playerStatus = getPlayerStatusEntity(roomId, currentUser.getId());
                playerStatus.setPlayerInRoom(true);
                updatePlayerInRoomAndUserInRoom(roomDto, playerStatus, true);

                return ResponseEntity.ok("User joined room with id: " + roomId);

            } catch (ResourceNotFoundException e) {
                PlayerStatus playerStatus = createDefaultPlayerStatus(roomId, currentUser);
                updatePlayerInRoomAndUserInRoom(roomDto, playerStatus, true);

                return ResponseEntity.ok("player status was created and user joined room with id: " + roomId);
            }
        } else {
            return ResponseEntity.ok("Wrong password user could not enter the room");
        }
    }

    public ResponseEntity exitRoom(Long roomId, UserPrincipal currentUser) {
        RoomDto roomDto = roomFacade.getRoomDto(roomId);
        PlayerStatus playerStatus = getPlayerStatusEntity(roomId, currentUser.getId());
        playerStatus.setPlayerInRoom(false);

        updatePlayerInRoomAndUserInRoom(roomDto ,playerStatus, false);

        return ResponseEntity.ok("player leaved room: " + roomId);
    }

    private PlayerStatus getPlayerStatusEntity(Long roomId, Long userId) {
        return playerStatusRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Player Status", "roomId and userId", roomId));
    }

    private void updatePlayerInRoomAndUserInRoom(RoomDto roomDto, PlayerStatus playerStatus, boolean joingOrLeaving) {
        if(joingOrLeaving){
            playerStatusRepository.save(playerStatus);
            roomDto.setUsersInRoom(roomDto.getUsersInRoom() + 1L);
            roomFacade.usersInRoomUpdate(roomDto);
        }else{
            playerStatusRepository.save(playerStatus);
            roomDto.setUsersInRoom(roomDto.getUsersInRoom() - 1L);
            roomFacade.usersInRoomUpdate(roomDto);
        }
    }

    private PlayerStatusResponse toPlayerStatusResponse(PlayerStatus playerStatus) {
        return PlayerStatusResponse.builder()
                .userId(playerStatus.getUserId())
                .userName(userFacade.getUser(playerStatus.getUserId()).getUsername())
                .playerClassDto(getPlayerClass(playerStatus.getClassId()))
                .playerRaceDto(getPlayerRace(playerStatus.getRaceId()))
                .playerLevel(playerStatus.getPlayerLevel())
                .playerBonus(playerStatus.getPlayerBonus())
                .gender(playerStatus.getGender())
                .build();
    }

    private PlayerStatus createDefaultPlayerStatus(Long roomId, UserPrincipal currentUser) {
        PlayerStatus playerStatus;
        playerStatus = PlayerStatus.builder()
                .roomId(roomId)
                .userId(currentUser.getId())
                .classId(0L)
                .raceId(0L)
                .playerLevel(0L)
                .playerBonus(0L)
                .playerInRoom(true)
                .gender(userFacade.getUser(currentUser.getId()).getGender())
                .build();
        return playerStatus;
    }

    public ResponseEntity setPlayerLevel(Long roomId, Long upOrDown, UserPrincipal currentUser) {
        PlayerStatus playerStatus = getPlayerStatusEntity(roomId, currentUser.getId());

        if(playerStatus.getPlayerLevel() + upOrDown < 0){
            return ResponseEntity.ok("Player level can't be lower then 0");
        }

        playerStatus.setPlayerLevel(playerStatus.getPlayerLevel() + upOrDown);
        playerStatusRepository.save(playerStatus);

        if (playerStatus.getPlayerLevel() + upOrDown > 9){
            //dorobić zakończenie gry
            return ResponseEntity.ok("Player achieved level 10, game is over");
        }

        return  ResponseEntity.ok("player level set successfully");
    }

    public ResponseEntity setPlayerBonus(Long roomId, Long upOrDown, UserPrincipal currentUser) {
        PlayerStatus playerStatus = getPlayerStatusEntity(roomId, currentUser.getId());

        if(playerStatus.getPlayerBonus() + upOrDown < 0){
            return ResponseEntity.ok("Player bonus can't be lower then 0");
        }

        playerStatus.setPlayerBonus(playerStatus.getPlayerBonus() + upOrDown);
        playerStatusRepository.save(playerStatus);
        return  ResponseEntity.ok("player bonus set successfully");
    }

    public ResponseEntity changeGender(Long roomId, UserPrincipal currentUser) {
        PlayerStatus playerStatus = getPlayerStatusEntity(roomId, currentUser.getId());
        String newGender = playerStatus.getGender().equals("men")  ? "women" : "men";
        playerStatus.setGender(newGender);
        playerStatusRepository.save(playerStatus);
        return ResponseEntity.ok("Gender was changed successfully to: " + newGender);
    }
}
