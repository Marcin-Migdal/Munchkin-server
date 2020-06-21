package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.playerStatus.dto.PlayerClass.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRace.PlayerRaceDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/status/byId/{roomId}")
    public PlayerStatusResponse getPlayerStatus(@PathVariable Long roomId, @CurrentUser UserPrincipal currentUser){
        return playerStatusFacade.getPlayerStatus(roomId, currentUser);
    }

    @GetMapping("join/{roomId}/{roomPassword}")
    public ResponseEntity<?> joinRoom (@PathVariable Long roomId, @PathVariable String roomPassword, @CurrentUser UserPrincipal currentUser){
        ResponseEntity responseEntity = playerStatusFacade.joinRoom(roomId, roomPassword, currentUser);
        return responseEntity;
    }

    @GetMapping("exit/{roomId}")
    public ResponseEntity<?> exitRoom (@PathVariable Long roomId, @CurrentUser UserPrincipal currentUser){
        ResponseEntity responseEntity = playerStatusFacade.exitRoom(roomId, currentUser);
        return responseEntity;
    }

    @GetMapping("/setLevel/{roomId}/{upOrDown}")
    public ResponseEntity<?> setPlayerLevel (@PathVariable Long roomId, @PathVariable Long upOrDown, @CurrentUser UserPrincipal currentUser){
        ResponseEntity responseEntity = playerStatusFacade.setPlayerLevel(roomId, upOrDown, currentUser);
        return responseEntity;
    }

    @GetMapping("/setBonus/{roomId}/{upOrDown}")
    public ResponseEntity<?> setPlayerBonus (@PathVariable Long roomId, @PathVariable Long upOrDown, @CurrentUser UserPrincipal currentUser){
        ResponseEntity responseEntity = playerStatusFacade.setPlayerBonus(roomId, upOrDown, currentUser);
        return responseEntity;
    }

    @GetMapping("/changeGender/{roomId}")
    public ResponseEntity<?> changeGender (@PathVariable Long roomId, @CurrentUser UserPrincipal currentUser){
        ResponseEntity responseEntity = playerStatusFacade.changeGender(roomId, currentUser);
        return responseEntity;
    }
}
