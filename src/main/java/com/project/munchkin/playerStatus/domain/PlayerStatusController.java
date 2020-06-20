package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.playerStatus.dto.PlayerClass.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRace.PlayerRaceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/playerStatus")
public class PlayerStatusController {

    @Autowired
    PlayerStatusFacade playerStatusFacade;

    @GetMapping("/race/byId/{raceId}")
    public PlayerRaceDto getPlayerRace(@PathVariable Long raceId){
        return playerStatusFacade.getPlayerRace(raceId);
    }

    @GetMapping("/race/getAll")
    public List<PlayerRaceDto> getAllPlayerRaces(){
        return playerStatusFacade.getAllPlayerRaces();
    }

    @GetMapping("/class/byId/{classId}")
    public PlayerClassDto getPlayerClass(@PathVariable Long classId){
        return playerStatusFacade.getPlayerClass(classId);
    }

    @GetMapping("/class/getAll")
    public List<PlayerClassDto> getAllPlayerClasses(){
        return playerStatusFacade.getAllPlayerClasses();
    }


}
