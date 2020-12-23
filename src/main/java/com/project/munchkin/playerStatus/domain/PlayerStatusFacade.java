package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.playerStatus.dto.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRaceDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import com.project.munchkin.playerStatus.exception.RoomIsFullException;
import com.project.munchkin.playerStatus.exception.UserAlreadyInRoomException;
import com.project.munchkin.playerStatus.exception.WrongValueException;
import com.project.munchkin.playerStatus.model.PlayerClass;
import com.project.munchkin.playerStatus.model.PlayerRace;
import com.project.munchkin.playerStatus.model.PlayerStatus;
import com.project.munchkin.playerStatus.repository.PlayerClassRepository;
import com.project.munchkin.playerStatus.repository.PlayerRaceRepository;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.room.dto.RoomDto;
import com.project.munchkin.room.exception.NotAuthorizedException;
import com.project.munchkin.room.exception.ResourceNotFoundException;
import com.project.munchkin.room.model.Room;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.dto.UserDto;
import com.project.munchkin.user.model.User;
import com.project.munchkin.user.repository.UserRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


    public PlayerRaceDto getRace(Long raceId) {
        return playerRaceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race", "Race Id", raceId, HttpStatus.NOT_FOUND)).dto();
    }

    public List<PlayerRaceDto> getAllRaces() {
        List<PlayerRaceDto> playerRaceDtoList = playerRaceRepository.findAll()
                .stream()
                .map(PlayerRace::dto)
                .collect(Collectors.toList());
        if(playerRaceDtoList.isEmpty()){
            throw new ResourceNotFoundException("No race found", HttpStatus.NOT_FOUND);
        }
        return playerRaceDtoList;
    }

    public PlayerClassDto getClass(Long classId) {
        return playerClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "Class Id", classId, HttpStatus.NOT_FOUND)).dto();
    }

    public List<PlayerClassDto> getAllClasses() {
        List<PlayerClassDto> playerClassDtoList = playerClassRepository.findAll()
                .stream()
                .map(PlayerClass::dto)
                .collect(Collectors.toList());
        if(playerClassDtoList.isEmpty()){
            throw new ResourceNotFoundException("No class found", HttpStatus.NOT_FOUND);
        }
        return playerClassDtoList;
    }

    public void joinRoom(Long roomId, String roomPassword, Long userId) {
        if(playerStatusRepository.playerIsInAnyRoom(userId)){
            throw new UserAlreadyInRoomException(userId, HttpStatus.BAD_REQUEST);
        }

        RoomDto roomDto = getRoomDto(roomId);

        if(roomDto.getSlots() <= getAllPlayerStatus(roomId).size() || roomRepository.isRoomFull(roomId)){
            throw new RoomIsFullException(HttpStatus.BAD_REQUEST);
        }

        if (roomDto.getRoomPassword().equals(roomPassword)) {
            try {
                PlayerStatusDto playerStatusDto = getPlayerStatusEntityByRoomId(roomId, userId).dto();
                updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, true);
            } catch (ResourceNotFoundException e) {
                PlayerStatusDto playerStatusDto = createDefaultPlayerStatus(roomId, userId).dto();
                updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, true);
            }
        } else {
            throw new NotAuthorizedException("join this room, wrong password",HttpStatus.UNAUTHORIZED);
        }
    }

    public void exitRoom(Long roomId, Long userId) {
        RoomDto roomDto = getRoomDto(roomId);
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityByRoomId(roomId, userId).dto();

        playerInRoom(playerStatusDto.playerInRoom, "leave a room that you are not in");

        updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, false);
    }

    public PlayerStatusResponse getPlayerStatusResponseByRoomId(Long roomId , Long userId) {
        PlayerStatus playerStatus = getPlayerStatusEntityByRoomId(roomId, userId);
        return toPlayerStatusResponse(playerStatus);
    }

    public PlayerStatusResponse getPlayerStatusResponseById(Long playerStatusId) {
        PlayerStatus playerStatus = getPlayerStatusEntityById(playerStatusId);
        return toPlayerStatusResponse(playerStatus);
    }

    public List<PlayerStatusResponse> getAllPlayersStatusResponse(Long roomId) {
        List<PlayerStatus> allPlayerStatus = getAllPlayerStatus(roomId);
        if(allPlayerStatus.isEmpty()){
            throw new ResourceNotFoundException("Players Statuses","roomId", roomId, HttpStatus.NOT_FOUND);
        }else{
            return allPlayerStatus
                    .stream()
                    .map(this::toPlayerStatusResponse)
                    .collect(Collectors.toList());
        }
    }

    public void setPlayerLevel(Long playerStatusId, Long levelValue) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, "change player level in this room because you are not in it");

        if(playerStatusDto.getPlayerLevel() + levelValue < 1){
            throw new WrongValueException("Player level can't be lower then 1", HttpStatus.BAD_REQUEST);
        }

        if (playerStatusDto.getPlayerLevel() + levelValue > 9){
            RoomDto roomDto = getRoomDto(playerStatusDto.getRoomId());
            roomDto.setComplete(true);
            roomRepository.save(Room.fromDto(roomDto));
        }

        playerStatusDto.setPlayerLevel(playerStatusDto.getPlayerLevel() + levelValue);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public void setPlayerBonus(Long playerStatusId, Long bonusValue) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, "change player bonus in this room because you are not in it");

        if(playerStatusDto.getPlayerBonus() + bonusValue < 0){
            throw new WrongValueException("Player bonus can't be lower then 0", HttpStatus.BAD_REQUEST);
        }

        playerStatusDto.setPlayerBonus(playerStatusDto.getPlayerBonus() + bonusValue);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public String changeGender(Long playerStatusId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, "change gender in this room because you are not in it");

        String newGender = playerStatusDto.getGender().equals("male")  ? "female" : "male";
        playerStatusDto.setGender(newGender);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        return newGender;
    }

    public void setFirstRace(Long playerStatusId, Long raceId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, "change first race in this room because you are not in it");

        raceExist(raceId);

        playerStatusDto.setRaceId(raceId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public boolean toggleTwoRaces(Long playerStatusId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, "toggle between having one or two races in this room because you are not in it");

        playerStatusDto.setTwoRaces(!playerStatusDto.isTwoRaces());
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        return playerStatusDto.isTwoRaces();
    }

    public void setSecondRace(Long playerStatusId, Long raceId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, "change second race in this room because you are not in it");

        raceExist(raceId);

        if(!playerStatusDto.isTwoRaces()){
            throw new NotAuthorizedException("change second race because you can't have two races", HttpStatus.BAD_REQUEST);
        }

        playerStatusDto.setSecondRaceId(raceId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public void setFirstClass(Long playerStatusId, Long classId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, "change first class in this room because you are not in it");

        classExist(classId);

        playerStatusDto.setClassId(classId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public boolean toggleTwoClasses(Long playerStatusId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, "toggle between having one or two classes in this room because you are not in it");

        playerStatusDto.setTwoClasses(!playerStatusDto.isTwoClasses());
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));

        return playerStatusDto.isTwoClasses();
    }

    public void setSecondClass(Long playerStatusId, Long classId) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(playerStatusId).dto();
        playerInRoom(playerStatusDto.playerInRoom, "change second class in this room because you are not in it");

        classExist(classId);

        if(!playerStatusDto.isTwoClasses()){
            throw new NotAuthorizedException("change second class because you can't have two classes", HttpStatus.BAD_REQUEST);
        }

        playerStatusDto.setSecondClassId(classId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    public List<PlayerStatusResponse> getGameSummary(Long roomId) {
        List<PlayerStatus> allPlayerStatus = playerStatusRepository.findAllSortedPlayerStatusByRoomId(roomId);
        if(allPlayerStatus.isEmpty()){
            throw new ResourceNotFoundException("Players Statuses","roomId", roomId, HttpStatus.NOT_FOUND);
        }else{
            return allPlayerStatus
                    .stream()
                    .map(this::toPlayerStatusResponse)
                    .collect(Collectors.toList());
        }
    }

    public void deletePlayerStatus(Long playerStatusId, Long userId) {
        if(!playerStatusRepository.existsById(playerStatusId)){
            throw new ResourceNotFoundException("Player Status","playerStatusId", playerStatusId, HttpStatus.NOT_FOUND);
        }

        RoomDto roomDto = getRoomDto(playerStatusId);

        if(!roomDto.getUser().getId().equals(userId)){
            throw new NotAuthorizedException("delete this player status because you are not creator of the room that player status belong" , HttpStatus.UNAUTHORIZED);
        }

        roomDto.setUsersInRoom(roomDto.getUsersInRoom() - 1L);
        usersInRoomUpdate(roomDto);
        playerStatusRepository.deleteById(playerStatusId);
    }

    public void deletePlayersStatuses(Long roomId, Long creatorId) {
        RoomDto roomDto = getRoomDto(roomId);
        if(!roomDto.getUser().getId().equals(creatorId)){
            throw new NotAuthorizedException("delete all player statuses in this room because you are not creator of this room" , HttpStatus.UNAUTHORIZED);
        }

        List<PlayerStatus> allPlayersStatuses = getAllPlayerStatus(roomId);

        if (allPlayersStatuses.isEmpty()){
            throw new ResourceNotFoundException("Players Statuses","roomId", roomId, HttpStatus.NOT_FOUND);
        }

        playerStatusRepository.deleteAll(allPlayersStatuses);
        roomDto.setUsersInRoom(0L);
        usersInRoomUpdate(roomDto);
    }

    private void raceExist(Long raceId) {
        if(!playerRaceRepository.existsById(raceId)){
            throw new ResourceNotFoundException("Race", "RaceId", raceId, HttpStatus.NOT_FOUND);
        }
    }

    private void classExist(Long classId) {
        if(!playerClassRepository.existsById(classId)){
            throw new ResourceNotFoundException("Class", "ClassId", classId, HttpStatus.NOT_FOUND);
        }
    }

    private List<PlayerStatus> getAllPlayerStatus(Long roomId) {
        return playerStatusRepository.findAllPlayerStatusByRoomId(roomId);
    }

    private PlayerStatus getPlayerStatusEntityByRoomId(Long roomId, Long userId) {
        return playerStatusRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Player Status", "roomId: "+ roomId + " and userId", userId, HttpStatus.NOT_FOUND));
    }

    private PlayerStatus getPlayerStatusEntityById(Long playerStatusId) {
        return playerStatusRepository.findById(playerStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("Player Status","playerStatusId", playerStatusId, HttpStatus.NOT_FOUND));
    }

    private void updatePlayerInRoomAndUserInRoom(RoomDto roomDto, PlayerStatusDto playerStatusDto, boolean joiningOrLeaving) {
        if(joiningOrLeaving){
            playerStatusDto.setPlayerInRoom(true);
            roomDto.setUsersInRoom(roomDto.getUsersInRoom() + 1L);
        }else{
            playerStatusDto.setPlayerInRoom(false);
            roomDto.setUsersInRoom(roomDto.getUsersInRoom() - 1L);
        }

        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
        usersInRoomUpdate(roomDto);
    }

    private PlayerStatusResponse toPlayerStatusResponse(PlayerStatus playerStatus) {
        return PlayerStatusResponse.builder()
                .id(playerStatus.getId())
                .user(playerStatus.getUser().response())
                .playerClassDto(getClass(playerStatus.getClassId()))
                .secondPlayerClassDto(getClass(playerStatus.getSecondClassId()))
                .twoClasses(playerStatus.isTwoClasses())
                .playerRaceDto(getRace(playerStatus.getRaceId()))
                .secondPlayerRaceDto(getRace(playerStatus.getSecondRaceId()))
                .twoClasses(playerStatus.isTwoClasses())
                .playerLevel(playerStatus.getPlayerLevel())
                .playerBonus(playerStatus.getPlayerBonus())
                .playerInRoom(playerStatus.isPlayerInRoom())
                .gender(playerStatus.getGender())
                .build();
    }

    private PlayerStatus createDefaultPlayerStatus(Long roomId, Long userId) {
        return PlayerStatus.builder()
                .roomId(roomId)
                .user(User.fromDto(getUserDto(userId)))
                .classId(0L)
                .twoClasses(false)
                .secondClassId(0L)
                .raceId(0L)
                .twoRaces(false)
                .secondRaceId(0L)
                .playerLevel(1L)
                .playerBonus(0L)
                .playerInRoom(true)
                .gender(getUserDto(userId).getGender())
                .build();
    }

    private UserDto getUserDto(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId, HttpStatus.NOT_FOUND)).dto();
    }

    private RoomDto getRoomDto(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId, HttpStatus.NOT_FOUND)).dto();
    }

    private void usersInRoomUpdate(RoomDto roomDto) {
        Room room = Room.fromDto(roomDto);
        roomRepository.save(room);
    }

    private void playerInRoom(boolean playerInRoom, String message){
        if(!playerInRoom){
            throw new NotAuthorizedException(message, HttpStatus.UNAUTHORIZED);
        }
    }
}
