package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.playerStatus.dto.PlayerClass.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRace.PlayerRaceDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusDto;
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

    public PlayerStatusResponse getPlayerStatusByRoomId(Long roomId , UserPrincipal currentUser) {
        PlayerStatus playerStatusEntity = getPlayerStatusEntityByRoomId(roomId, currentUser.getId());
        return toPlayerStatusResponse(playerStatusEntity);
    }

    public PlayerStatusResponse getPlayerStatusById(Long playerStatusId) {
        PlayerStatus playerStatus = getPlayerStatusEntityById(playerStatusId);
        return toPlayerStatusResponse(playerStatus);
    }

    public List<PlayerStatusResponse> getAllPlayersStatusesInRoom(Long roomId) {
        List<PlayerStatusResponse> allPlayersStatuses = playerStatusRepository.findAllPlayerStatusByRoomId(roomId)
                .stream()
                .map(item -> toPlayerStatusResponse(item))
                .collect(Collectors.toList());

        if(allPlayersStatuses.isEmpty()){
            throw new ResourceNotFoundException("Players Statuses","roomId", roomId);
        }

        return allPlayersStatuses;
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
                PlayerStatusDto playerStatusDto = getPlayerStatusEntityByRoomId(roomId, currentUser.getId()).dto();
                playerStatusDto.setPlayerInRoom(true);
                updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, true);

                return ResponseEntity.ok("User joined room with id: " + roomId);

            } catch (ResourceNotFoundException e) {
                PlayerStatusDto playerStatusDto = createDefaultPlayerStatus(roomId, currentUser).dto();
                updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, true);

                return ResponseEntity.ok("player status was created and user joined room with id: " + roomId);
            }
        } else {
            return ResponseEntity.ok("Wrong password user could not enter the room");
        }
    }

    public void exitRoom(Long roomId, Long playerStatusId) {
        RoomDto roomDto = roomFacade.getRoomDto(roomId);
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerStatusDto.setPlayerInRoom(false);

        updatePlayerInRoomAndUserInRoom(roomDto ,playerStatusDto, false);
    }

    public ResponseEntity setPlayerLevel(Long playerStatusId, Long upOrDown) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        if(playerStatusDto.getPlayerLevel() + upOrDown < 0){
            return ResponseEntity.ok("Player level can't be lower then 0");
        }

        playerStatusDto.setPlayerLevel(playerStatusDto.getPlayerLevel() + upOrDown);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        if (playerStatusDto.getPlayerLevel() + upOrDown > 9){
            roomFacade.roomIsCompleted(playerStatusDto.getRoomId());
            return ResponseEntity.ok("Player achieved level 10, game is over");
        }

        return  ResponseEntity.ok("player level set successfully");
    }

    public ResponseEntity setPlayerBonus(Long playerStatusId, Long upOrDown) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        if(playerStatusDto.getPlayerBonus() + upOrDown < 0){
            return ResponseEntity.ok("Player bonus can't be lower then 0");
        }

        playerStatusDto.setPlayerBonus(playerStatusDto.getPlayerBonus() + upOrDown);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
        return  ResponseEntity.ok("player bonus set successfully");
    }

    public void changeGender(Long playerStatusId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        String newGender = playerStatusDto.getGender().equals("men")  ? "women" : "men";
        playerStatusDto.setGender(newGender);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public void changeRace(Long playerStatusId, Long raceId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        playerStatusDto.setRaceId(raceId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public void changeClass(Long playerStatusId, Long classId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        playerStatusDto.setClassId(classId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public void deletePlayerStatus(Long playerStatusId) {
        PlayerStatus playerStatus = getPlayerStatusEntityById(playerStatusId);
        RoomDto roomDto = roomFacade.getRoomDto(playerStatus.getRoomId());
        roomDto.setUsersInRoom(roomDto.getUsersInRoom() - 1L);
        roomFacade.usersInRoomUpdate(roomDto);
        playerStatusRepository.delete(playerStatus);
    }

    public ResponseEntity deletePlayersStatuses(Long roomId) {
        List<PlayerStatus> allPlayersStatuses = playerStatusRepository.findAllPlayerStatusByRoomId(roomId);

        if (allPlayersStatuses.isEmpty()){
            return ResponseEntity.ok("No player statuses was found");
        }

        RoomDto roomDto = roomFacade.getRoomDto(roomId);
        roomDto.setUsersInRoom(0L);
        roomFacade.usersInRoomUpdate(roomDto);
        playerStatusRepository.deleteAll(allPlayersStatuses);

        return ResponseEntity.ok("All statuses in the room were deleted");
    }


    public void deleteRoom(Long roomId) {
        roomFacade.deleteById(roomId);
        deletePlayersStatuses(roomId);
    }

    private PlayerStatus getPlayerStatusEntityByRoomId(Long roomId, Long userId) {
        return playerStatusRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Player Status", "roomId and userId", roomId));
    }

    private PlayerStatus getPlayerStatusEntityById(Long playerStatusId) {
        return playerStatusRepository.findById(playerStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("Player Status","playerStatusId", playerStatusId));
    }

    private void updatePlayerInRoomAndUserInRoom(RoomDto roomDto, PlayerStatusDto playerStatusDto, boolean joiningOrLeaving) {
        if(joiningOrLeaving){
            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
            roomDto.setUsersInRoom(roomDto.getUsersInRoom() + 1L);
            roomFacade.usersInRoomUpdate(roomDto);
        }else{
            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
            roomDto.setUsersInRoom(roomDto.getUsersInRoom() - 1L);
            roomFacade.usersInRoomUpdate(roomDto);
        }
    }

    private PlayerStatusResponse toPlayerStatusResponse(PlayerStatus playerStatus) {
        return PlayerStatusResponse.builder()
                .playerStatusId(playerStatus.getId())
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
}
