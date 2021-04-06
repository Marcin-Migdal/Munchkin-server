package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.playerStatus.dto.EditRequest.BonusLevelEditRequest;
import com.project.munchkin.playerStatus.dto.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRaceDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import com.project.munchkin.playerStatus.dto.RaceAndClassResponse;
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
import com.project.munchkin.user.model.User;
import com.project.munchkin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.Builder;

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

    @Bean
    public CommandLineRunner insertClassesAndRacesToDatabase(PlayerClassRepository playerClassRepository, PlayerRaceRepository playerRaceRepository) {
        if(playerClassRepository.existsById(1L) || playerClassRepository.existsById(1L)){
            return null;
        }else{
            return (args) -> {
                playerRaceRepository.save(createPlayerRace("Człowiek","Nie masz żadnych mocy, jesteś człowiekiem"));
                playerRaceRepository.save(createPlayerRace("Elf","Zyskujesz 1 poziom za każdego Potwora pokonanego w walkce w której pomogłeś, +1 do ucieczki"));
                playerRaceRepository.save(createPlayerRace("Krasnolud","Możesz nieść dowolną ilość Dużych Przedmiotów, Możesz mieć 6 kart na ręce"));
                playerRaceRepository.save(createPlayerRace("Ork","Ork, który jest celem Klątwy, może ją zignorować, tracąc w zamian 1 Poziom, chyba że jest na 1 Poziomie, Jeśli Ork walczy sam i pokona Potwora różnicą siły bojowej więszą niż 10, zyskuje 1 dodatkowy Poziom"));
                playerRaceRepository.save(createPlayerRace("Niziołek","Raz w trakcie swojej tury możesz sprzedać jeden Przedmiot za podwójną cene (pozostałe przedmiotysą warte tyle , co zwykle), Jeśli nie uda ci się pierwszy rzut na Ucieczkę, możesz odrzucić kartę i spróbować raz jeszcze"));
                playerRaceRepository.save(createPlayerRace("Gnom","Jeśli walczy sam, możesz zagrać jednego Potwora z ręki jako Iluzję; inne karty mogą wpływać na tego iluzorycznego Potwora normalnie, Dodaj jego siłę bojową do swojej na czas tej jednej walki, potem Potwór znika, Dostajesz +1 za Przedmiot niejednorazowego użytku zaczynający się na G lub N. Potwory Traktują cię jak Niziołka; są zbyt głupie, by dostrzec różnice, Wyjątek: żaden Potwór z \"Nosem\", i \"Nochalem\" w nazwie nie będzie cię gonić. Jeżeli nie możesz ich pokonać to automatycznie im Uciekasz"));

                playerClassRepository.save(createPlayerClass("Brak","Nie masz żadnej klasy"));
                playerClassRepository.save(createPlayerClass("Czarodziej","Czar Lot: Możesz odrzucić do 3 kart przy próbie Ucieczki. Każda z nich daje ci +1 do rzutu kostką. Czar Zauroczenie: Odrzuć całą rękę (conajmniej 3 karty), żeby zauroczyć jednego Potwora, zamiast z nim walczyć. Za tak pokonannego Potwora nie zyskujesz Poziomu, ale zdobywasz jego Skarb, Jeśli w walce uczestniczy więcej Potworów, z pozostałymi walczysz normalnie"));
                playerClassRepository.save(createPlayerClass("Wojownik","Szał Bojowy: Możesz odrzucić do 3 kart w trakcie walki, Każda z nich daje ci bonus +1. Wygrywasz, jeśli walka kończyłaby się remisem"));
                playerClassRepository.save(createPlayerClass("Kapłan","Wskrzenie: Raz w trakcie tury, Zamiast dobrać kartę odkrytą, możesz wziąć kartę z wierzchu odpowiedniego stosu kart odrzuconych. Gdy to zrobisz, musisz odrzucić jedną kartę z ręki za każdą dobraną tak kartę. Odpędzenie Nieumarłych: Możesz odrzucić do 3 kart w trakcie walki z Nieumarłym. Każda z nich daje ci bonus +1"));
                playerClassRepository.save(createPlayerClass("Złodziej","Możesz odrzucić kartę, aby wbić nóż w plecy innego gracza (ma -2 na czas trawania walki). Ten sam gracz nie możesz paść twoją ofjarądwukrotnie w jednej walce, ale jeśli walczących jest dwóch, to możesz zdradzić każdego z nich. Kradzież: Możęsz odrzucić kartę z ręki by spróbować ukraść mały przemiot innemu graczowi. Rzuć kostką. Wyniki 5 i 6 to sukces. Udaje ci się ukraść tą rzecz. Porażka (wyniki 1, 2, 3, 4) oznacza, że zostałeś przyłapany. Tracisz 1 poziom."));
                playerClassRepository.save(createPlayerClass("Łowca","Poskramiacz Potworów: W swojej walce możesz poskromić jednego Potwora zamiast z nim walczyć. Zostaje twoim nowym Rumakiem, Aby go zwerbować musisz odrzucić swojego dotychczasowego Rumaka (jeśli takiego miałeś) i odrzucić liczbe kart równą liczbie Skarbów broninych przez poskromionego Potwora. Ta liczba to także twój nowy bonus z Rumaka. Nie możesz handlować Rumakiem, oddać go ani sprzedać za Poziom. Wsparcie Strzeleckie: Gdy pomagasz komuś w walce, masz dodatkowy bonus +2"));
                playerClassRepository.save(createPlayerClass("Bard","Oczarowanie: Podczas walki możesz odrzucić kartę i wybrać rywala. Każdy z Was rzuca kością, jeśli wyrzucisz wynik równy lub wyższy niż on, musi ci pomóc i nie może prosić o nagrodę. Jeśli przegrasz, możesz odrzucić kolejną kartę i spróbować Oczarować innego rywala. Możesz próbować dopóki masz karty. Każdego gracza możesz próbować Oczarować tylko raz. Nie możesz zdobyć zwycięskiego poziomu korzystając z tej umiejętności. Bardowe Szczęście: Jeśli wygrasz walkę w swojej turze, sam lub z pomocą, dobierz jeden dodatkowy Skarb. Przejrzyj wszystkie dobrane przed chwilą Skarby i odrzuć jeden, wybrany przez siebie"));
            };
        }
    }

    private PlayerClass createPlayerClass(String name, String description) {
        return PlayerClass.builder()
                .className(name)
                .classDescription(description)
                .build();
    }

    private PlayerRace createPlayerRace(String name, String description) {
        return PlayerRace.builder()
                .raceName(name)
                .raceDescription(description)
                .build();
    }

    public RaceAndClassResponse getAllRacesAndClasses() {
        List<PlayerRaceDto> playerRaceDtoList = playerRaceRepository.findAll()
                .stream()
                .map(PlayerRace::dto)
                .collect(Collectors.toList());
        if(playerRaceDtoList.isEmpty()){
            throw new ResourceNotFoundException("No race found", HttpStatus.NOT_FOUND);
        }

        List<PlayerClassDto> playerClassDtoList = playerClassRepository.findAll()
                .stream()
                .map(PlayerClass::dto)
                .collect(Collectors.toList());
        if(playerClassDtoList.isEmpty()){
            throw new ResourceNotFoundException("No class found", HttpStatus.NOT_FOUND);
        }

        return RaceAndClassResponse.builder()
                .playerRaces(playerRaceDtoList)
                .playerClasses(playerClassDtoList)
                .build();
    }

    public void joinRoom(Long roomId, String roomPassword, Long userId) {
        User user = getUser(userId);

        RoomDto roomDto = getRoomDto(roomId);
        if (!roomDto.getRoomPassword().equals(roomPassword)) {
            throw new NotAuthorizedException("join this room, wrong password", HttpStatus.UNAUTHORIZED);
        }

        if(playerStatusRepository.playerIsInAnyRoom(user)){
            throw new UserAlreadyInRoomException(userId, HttpStatus.BAD_REQUEST);
        }

        List<PlayerStatus> allPlayerStatus = getAllPlayerStatusInRoom(roomId);
        allPlayerStatus.removeIf(playerStatus -> playerStatus.getUser().getId().equals(userId));

        if(roomDto.getSlots() <= allPlayerStatus.size() || roomRepository.isRoomFull(roomId)){
            throw new RoomIsFullException(HttpStatus.BAD_REQUEST);
        }

        try {
            PlayerStatusDto playerStatusDto = getPlayerStatusEntityByRoomId(roomId, user).dto();
            updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, true);
        } catch (ResourceNotFoundException e) {
            PlayerStatusDto playerStatusDto = createDefaultPlayerStatus(roomId, user).dto();
            updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, true);
        }
    }

    public void exitRoom(Long roomId, Long userId) {
        RoomDto roomDto = getRoomDto(roomId);
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityByRoomId(roomId, getUser(userId)).dto();

        playerInRoom(playerStatusDto.playerInRoom, "leave a room that you are not in");

        updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, false);
    }

    public void exitRoomOnLogIn(Long userId) {
        User user = getUser(userId);
        Long roomId = roomRepository.findRoomIdWithPlayerIn(user)
                .orElseThrow(() -> new ResourceNotFoundException("RoomId", "UserId", userId, HttpStatus.NOT_FOUND));

        RoomDto roomDto = getRoomDto(roomId);
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityByRoomId(roomId, user).dto();

        updatePlayerInRoomAndUserInRoom(roomDto, playerStatusDto, false);
    }

    public PlayerStatusResponse getPlayerStatusResponseByRoomId(Long roomId , Long userId) {
        PlayerStatus playerStatus = getPlayerStatusEntityByRoomId(roomId, getUser(userId));
        return toPlayerStatusResponse(playerStatus);
    }

    public PlayerStatusResponse getPlayerStatusResponseById(Long playerStatusId) {
        PlayerStatus playerStatus = getPlayerStatusEntityById(playerStatusId);
        return toPlayerStatusResponse(playerStatus);
    }

    public List<PlayerStatusResponse> getAllPlayersStatusesResponse(Long roomId) {
        List<PlayerStatus> allPlayerStatus = getAllPlayerStatusInRoom(roomId);
        if(allPlayerStatus.isEmpty()){
            throw new ResourceNotFoundException("Players Statuses","roomId", roomId, HttpStatus.NOT_FOUND);
        }else{
            return allPlayerStatus
                    .stream()
                    .map(this::toPlayerStatusResponse)
                    .collect(Collectors.toList());
        }
    }

    public List<PlayerStatusResponse> getPlayersStatusesResponseInRoom(Long roomId) {
        List<PlayerStatus> allPlayerStatus = playerStatusRepository.findAllPlayerStatusesInRoom(roomId);
        if(allPlayerStatus.isEmpty()){
            throw new ResourceNotFoundException("Players Statuses","roomId", roomId, HttpStatus.NOT_FOUND);
        }else{
            return allPlayerStatus
                    .stream()
                    .map(this::toPlayerStatusResponse)
                    .collect(Collectors.toList());
        }
    }

    public void setPlayerStatus(BonusLevelEditRequest bonusLevelEditRequest) {
        PlayerStatusDto playerStatusDto = getPlayerStatusEntityById(bonusLevelEditRequest.getPlayerStatusId()).dto();
        playerInRoom(playerStatusDto.playerInRoom, "save player status in this room because you are not in it");

        if(bonusLevelEditRequest.getLevelValue() < 1){
            throw new WrongValueException("Player level can't be lower then 1", HttpStatus.BAD_REQUEST);
        }

        if(bonusLevelEditRequest.getBonusValue() < 0){
            throw new WrongValueException("Player bonus can't be lower then 0", HttpStatus.BAD_REQUEST);
        }

        if (bonusLevelEditRequest.getLevelValue() > 9){
            CloseRoom(playerStatusDto);
        }

        playerStatusDto.setPlayerLevel(bonusLevelEditRequest.getLevelValue());
        playerStatusDto.setPlayerBonus(bonusLevelEditRequest.getBonusValue());
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
    }

    private void CloseRoom(PlayerStatusDto playerStatusDto) {
        RoomDto roomDto = getRoomDto(playerStatusDto.getRoomId());
        roomDto.setComplete(true);
        roomDto.setUsersInRoom(1L);
        roomRepository.save(Room.fromDto(roomDto));
        playerStatusRepository.allPLayersLeaveRoom(playerStatusDto.getRoomId());
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

        if(!playerStatusDto.isTwoRaces() && raceId != 1L){
            playerStatusDto.setTwoRaces(true);
        } if(raceId == 1L){
            playerStatusDto.setTwoRaces(false);
            playerStatusDto.setSecondRaceId(1L);
        }

        playerStatusDto.setRaceId(raceId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
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

        if(!playerStatusDto.isTwoClasses() && classId != 1L){
            playerStatusDto.setTwoClasses(true);
        } if(classId == 1L){
            playerStatusDto.setTwoClasses(false);
            playerStatusDto.setSecondClassId(1L);
        }

        playerStatusDto.setClassId(classId);
        playerStatusRepository.save(PlayerStatus.fromDto(playerStatusDto));
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

        List<PlayerStatus> allPlayersStatuses = getAllPlayerStatusInRoom(roomId);

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

    private List<PlayerStatus> getAllPlayerStatusInRoom(Long roomId) {
        return playerStatusRepository.findAllPlayerStatusesByRoomId(roomId);
    }

    private PlayerStatus getPlayerStatusEntityByRoomId(Long roomId, User user) {
        return playerStatusRepository.findByRoomIdAndUserId(roomId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Player Status", "roomId: "+ roomId + " and userId", user.getId(), HttpStatus.NOT_FOUND));
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
                .twoRaces(playerStatus.isTwoRaces())
                .playerLevel(playerStatus.getPlayerLevel())
                .playerBonus(playerStatus.getPlayerBonus())
                .playerInRoom(playerStatus.isPlayerInRoom())
                .gender(playerStatus.getGender())
                .build();
    }

    private PlayerRaceDto getRace(Long raceId) {
        return playerRaceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race", "Race Id", raceId, HttpStatus.NOT_FOUND)).dto();
    }

    private PlayerClassDto getClass(Long classId) {
        return playerClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "Class Id", classId, HttpStatus.NOT_FOUND)).dto();
    }

    private PlayerStatus createDefaultPlayerStatus(Long roomId, User user) {
        return PlayerStatus.builder()
                .roomId(roomId)
                .user(user)
                .classId(1L)
                .secondClassId(1L)
                .twoClasses(false)
                .raceId(1L)
                .secondRaceId(1L)
                .twoRaces(false)
                .playerLevel(1L)
                .playerBonus(0L)
                .playerInRoom(true)
                .gender(user.getGender())
                .build();
    }

    private User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId, HttpStatus.NOT_FOUND));
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
