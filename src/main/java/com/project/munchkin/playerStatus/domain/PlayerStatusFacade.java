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
import com.project.munchkin.room.dto.RoomDto;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.room.model.Room;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.dto.UserDto;
import com.project.munchkin.user.repository.UserRepository;
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

        if(!playerStatusRepository.playerIsInAnyRoom(currentUser.getId())){}
        else{
            return ResponseEntity.ok("You already in another room, to join this room please leave room you are already in");
        }

        if(roomRepository.isRoomFull(roomId)){
            return ResponseEntity.ok("Room is full");
        }

        RoomDto roomDto = getRoomDto(roomId);
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
        RoomDto roomDto = getRoomDto(roomId);
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerStatusDto.setPlayerInRoom(false);

        updatePlayerInRoomAndUserInRoom(roomDto ,playerStatusDto, false);
    }

    public ResponseEntity setPlayerLevel(Long playerStatusId, Long upOrDown) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        if(playerStatusDto.getPlayerLevel() + upOrDown < 0){
            return ResponseEntity.ok("Player level can't be lower then 0");
        }

        if (playerStatusDto.getPlayerLevel() + upOrDown > 9){
            playerStatusDto.setPlayerLevel(playerStatusDto.getPlayerLevel() + upOrDown);
            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

            RoomDto roomDto = getRoomDto(playerStatusDto.getRoomId());
            roomDto.setComplete(true);
            roomRepository.save(Room.fromDto(roomDto));
            return ResponseEntity.ok("Player achieved level 10, game is over");
        }

        playerStatusDto.setPlayerLevel(playerStatusDto.getPlayerLevel() + upOrDown);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

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

    public void toggleTwoRaces(Long playerStatusId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        playerStatusDto.setTwoRaces(!playerStatusDto.isTwoRaces());
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public ResponseEntity<?> changeSecondRace(Long playerStatusId, Long raceId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        if(playerStatusDto.isTwoRaces()){
            playerStatusDto.setSecondRaceId(raceId);
            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

            return ResponseEntity.ok("Second race was changed successfully");
        }
        return ResponseEntity.ok("You can't have second race");
    }

    public void changeClass(Long playerStatusId, Long classId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        playerStatusDto.setClassId(classId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public void toggleTwoClasses(Long playerStatusId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        playerStatusDto.setTwoClasses(!playerStatusDto.isTwoClasses());
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public ResponseEntity<?> changeSecondClass(Long playerStatusId, Long classId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();

        if(playerStatusDto.isTwoClasses()){
            playerStatusDto.setSecondClassId(classId);
            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

            return ResponseEntity.ok("Second class was changed successfully");
        }
        return ResponseEntity.ok("You can't have second class");
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
            return ResponseEntity.ok("No player statuses was found");
        }

        RoomDto roomDto = getRoomDto(roomId);
        roomDto.setUsersInRoom(0L);
        usersInRoomUpdate(roomDto);
        playerStatusRepository.deleteAll(allPlayersStatuses);

        return ResponseEntity.ok("All statuses in the room were deleted");
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
            usersInRoomUpdate(roomDto);
        }else{
            playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
            roomDto.setUsersInRoom(roomDto.getUsersInRoom() - 1L);
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
        PlayerStatus playerStatus;
        playerStatus = PlayerStatus.builder()
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
        return playerStatus;
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
}
