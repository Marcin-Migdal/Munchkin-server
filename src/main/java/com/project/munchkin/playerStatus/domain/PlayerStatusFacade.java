package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.playerStatus.dto.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRaceDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import com.project.munchkin.playerStatus.exception.UserNotInRoomException;
import com.project.munchkin.playerStatus.model.PlayerClass;
import com.project.munchkin.playerStatus.model.PlayerRace;
import com.project.munchkin.playerStatus.model.PlayerStatus;
import com.project.munchkin.playerStatus.repository.PlayerClassRepository;
import com.project.munchkin.playerStatus.repository.PlayerRaceRepository;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.room.dto.RoomDto;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.room.model.Room;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.dto.UserDto;
import com.project.munchkin.user.repository.UserRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Builder
public class PlayerStatusFacade {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

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
        List<PlayerStatusResponse> allPlayersStatusesInRoom = playerStatusRepository.findAllPlayerStatusByRoomId(roomId)
                .stream()
                .map(item -> toPlayerStatusResponse(item))
                .collect(Collectors.toList());

        if(allPlayersStatusesInRoom.isEmpty()){
            throw new ResourceNotFoundException("Players Statuses","roomId", roomId);
        }

        return allPlayersStatusesInRoom;
    }

    public ResponseEntity joinRoom(Long roomId, String roomPassword, UserPrincipal currentUser) {

        if(playerIsInAnyRoom(currentUser.getId())){
            return new ResponseEntity<>("You are already in this or another room, to join please leave room you are already in", HttpStatus.BAD_REQUEST);
        }if(roomRepository.isRoomFull(roomId)){
            return new ResponseEntity<>("Room is full", HttpStatus.BAD_REQUEST);
        }

        RoomDto roomDto = getRoomDto(roomId);
        if (roomDto.getRoomPassword().equals(roomPassword)) {
            try {
                PlayerStatusDto playerStatusDto = getPlayerStatusEntityByRoomId(roomId, currentUser.getId()).dto();
                playerStatusDto.setPlayerInRoom(true);
                updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, true);

                return ResponseEntity.ok("User with id: "+ currentUser.getId() +" joined room with id: " + roomId);

            } catch (ResourceNotFoundException e) {
                PlayerStatusDto playerStatusDto = createDefaultPlayerStatus(roomId, currentUser).dto();
                updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, true);

                return ResponseEntity.ok("Player status was created and user joined room with id: " + roomId);
            }
        } else {
            return new ResponseEntity<>("Wrong password user could not enter the room", HttpStatus.BAD_REQUEST);
        }
    }

    public boolean playerIsInAnyRoom(Long userId) {
        return playerStatusRepository.playerIsInAnyRoom(userId);
    }

    public void exitRoom(Long roomId, Long userId) {
        RoomDto roomDto = getRoomDto(roomId);
        PlayerStatusDto playerStatusDto = playerStatusRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Player Status", "roomId: " + roomId + " and userId: ", userId)).dto();

        updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, false);
    }

    public ResponseEntity setPlayerLevel(Long playerStatusId, Long levelValue) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, playerStatusDto.getUserId(), playerStatusDto.getRoomId());

        if(playerStatusDto.getPlayerLevel() + levelValue < 1){
            return new ResponseEntity<>("Player level can't be lower then 1", HttpStatus.BAD_REQUEST);
        }

        if (playerStatusDto.getPlayerLevel() + levelValue > 9){
            playerStatusDto.setPlayerLevel(playerStatusDto.getPlayerLevel() + levelValue);
            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

            RoomDto roomDto = getRoomDto(playerStatusDto.getRoomId());
            roomDto.setComplete(true);
            roomRepository.save(Room.fromDto(roomDto));
            return ResponseEntity.ok("Player achieved level 10, game is over");
        }

        playerStatusDto.setPlayerLevel(playerStatusDto.getPlayerLevel() + levelValue);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        return ResponseEntity.ok("Player level set successfully");
    }

    public ResponseEntity setPlayerBonus(Long playerStatusId, Long bonusValue) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, playerStatusDto.getUserId(), playerStatusDto.getRoomId());

        if(playerStatusDto.getPlayerBonus() + bonusValue < 0){
            return new ResponseEntity<>("Player bonus can't be lower then 0", HttpStatus.BAD_REQUEST);
        }

        playerStatusDto.setPlayerBonus(playerStatusDto.getPlayerBonus() + bonusValue);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
        return ResponseEntity.ok("Player bonus set successfully");
    }

    public ResponseEntity changeGender(Long playerStatusId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, playerStatusDto.getUserId(), playerStatusDto.getRoomId());

        String newGender = playerStatusDto.getGender().equals("men")  ? "women" : "men";
        playerStatusDto.setGender(newGender);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        return ResponseEntity.ok("Player gender was changed successfully to: " + newGender);
    }

    public ResponseEntity changeFirstRace(Long playerStatusId, Long raceId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, playerStatusDto.getUserId(), playerStatusDto.getRoomId());

        playerRaceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race", "RaceId", raceId)).dto();

        playerStatusDto.setRaceId(raceId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        return ResponseEntity.ok("Player race was changed successfully");
    }

    public ResponseEntity changeSecondRace(Long playerStatusId, Long raceId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, playerStatusDto.getUserId(), playerStatusDto.getRoomId());

        playerRaceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race", "RaceId", raceId)).dto();

        if(!playerStatusDto.isTwoRaces()){
            return new ResponseEntity<>("You can't have second race", HttpStatus.BAD_REQUEST);
        }

        playerStatusDto.setSecondRaceId(raceId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        return ResponseEntity.ok("Second race was changed successfully");
    }

    public ResponseEntity toggleTwoRaces(Long playerStatusId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, playerStatusDto.getUserId(), playerStatusDto.getRoomId());

        playerStatusDto.setTwoRaces(!playerStatusDto.isTwoRaces());
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        if(!playerStatusDto.twoRaces){
            return ResponseEntity.ok("Player is able to have one race");
        }

        return ResponseEntity.ok("Player is able to have second race");
    }

    public ResponseEntity changeClass(Long playerStatusId, Long classId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, playerStatusDto.getUserId(), playerStatusDto.getRoomId());

        playerClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "ClassId", classId)).dto();

        playerStatusDto.setClassId(classId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        return ResponseEntity.ok("Class was changed successfully");
    }

    public ResponseEntity changeSecondClass(Long playerStatusId, Long classId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, playerStatusDto.getUserId(), playerStatusDto.getRoomId());

        playerClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "ClassId", classId)).dto();

        if(playerStatusDto.isTwoClasses()){
            playerStatusDto.setSecondClassId(classId);
            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

            return ResponseEntity.ok("Second class was changed successfully");
        }
        return new ResponseEntity<>("You can't have second class", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity toggleTwoClasses(Long playerStatusId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, playerStatusDto.getUserId(), playerStatusDto.getRoomId());

        playerStatusDto.setTwoClasses(!playerStatusDto.isTwoClasses());
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        if(!playerStatusDto.twoClasses){
            return ResponseEntity.ok("Player is able to have one class");
        }

        return ResponseEntity.ok("Player is able to have second class");
    }

    public void deletePlayerStatus(Long playerStatusId) {
        PlayerStatus playerStatus = getPlayerStatusEntityById(playerStatusId);
        RoomDto roomDto = getRoomDto(playerStatus.getRoomId());
        roomDto.setUsersInRoom(roomDto.getUsersInRoom() - 1L);
        usersInRoomUpdate(roomDto);
        playerStatusRepository.delete(playerStatus);
    }

    public ResponseEntity deletePlayersStatuses(Long roomId) {
        List<PlayerStatus> allPlayersStatuses = playerStatusRepository.findAllPlayerStatusByRoomId(roomId);

        if (allPlayersStatuses.isEmpty()){
            return new ResponseEntity<>("No room by id:" + roomId + "was wound to delete players", HttpStatus.NOT_FOUND);
        }

        RoomDto roomDto = getRoomDto(roomId);
        roomDto.setUsersInRoom(0L);
        usersInRoomUpdate(roomDto);
        playerStatusRepository.deleteAll(allPlayersStatuses);

        return ResponseEntity.ok("All statuses in the room were deleted");
    }

    private PlayerStatus getPlayerStatusEntityByRoomId(Long roomId, Long userId) {
        return playerStatusRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Player Status", "roomId: "+ roomId + " and userId", userId));
    }

    private PlayerStatus getPlayerStatusEntityById(Long playerStatusId) {
        return playerStatusRepository.findById(playerStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("Player Status","playerStatusId", playerStatusId));
    }

    private void updatePlayerInRoomAndUserInRoom(RoomDto roomDto, PlayerStatusDto playerStatusDto, boolean joiningOrLeaving) {
        if(joiningOrLeaving){
            roomDto.setUsersInRoom(roomDto.getUsersInRoom() + 1L);

            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
            usersInRoomUpdate(roomDto);
        }else{
            playerStatusDto.setPlayerInRoom(false);
            roomDto.setUsersInRoom(roomDto.getUsersInRoom() - 1L);

            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
            usersInRoomUpdate(roomDto);
        }
    }

    private PlayerStatusResponse toPlayerStatusResponse(PlayerStatus playerStatus) {
        return PlayerStatusResponse.builder()
                .playerStatusId(playerStatus.getId())
                .userId(playerStatus.getUserId())
                .userName(getUserDto(playerStatus.getUserId()).getUsername())
                .playerClassDto(getPlayerClass(playerStatus.getClassId()))
                .twoClasses(playerStatus.isTwoClasses())
                .secondPlayerClassDto(getPlayerClass(playerStatus.getSecondClassId()))
                .playerRaceDto(getPlayerRace(playerStatus.getRaceId()))
                .twoClasses(playerStatus.isTwoClasses())
                .secondPlayerRaceDto(getPlayerRace(playerStatus.getSecondRaceId()))
                .playerLevel(playerStatus.getPlayerLevel())
                .playerBonus(playerStatus.getPlayerBonus())
                .gender(playerStatus.getGender())
                .build();
    }

    private PlayerStatus createDefaultPlayerStatus(Long roomId, UserPrincipal currentUser) {
        return PlayerStatus.builder()
                .roomId(roomId)
                .userId(currentUser.getId())
                .classId(0L)
                .twoClasses(false)
                .secondClassId(0L)
                .raceId(0L)
                .twoRaces(false)
                .secondRaceId(0L)
                .playerLevel(1L)
                .playerBonus(0L)
                .playerInRoom(true)
                .gender(getUserDto(currentUser.getId()).getGender())
                .build();
    }

    private UserDto getUserDto(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId)).dto();
    }

    private RoomDto getRoomDto(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId)).dto();
    }

    private void usersInRoomUpdate(RoomDto roomDto) {
        Room room = Room.fromDto(roomDto);
        roomRepository.save(room);
    }

    private void playerInRoom(boolean playerInRoom, Long userId, Long roomId){
        if(!playerInRoom){
            throw new UserNotInRoomException(userId, roomId);
        }
    }
}
