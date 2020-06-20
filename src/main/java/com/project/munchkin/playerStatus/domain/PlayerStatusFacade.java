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
    PlayerStatusRepository playerStatusRepository;

    @Autowired
    PlayerRaceRepository playerRaceRepository;

    @Autowired
    PlayerClassRepository playerClassRepository;

    @Autowired
    RoomFacade roomFacade;

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


}
