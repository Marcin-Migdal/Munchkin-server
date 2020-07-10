package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.playerStatus.dto.PlayerClass.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRace.PlayerRaceDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/status/byRoomId/{roomId}")
    public PlayerStatusResponse getPlayerStatusByRoomId(@PathVariable Long roomId, @CurrentUser UserPrincipal currentUser){
        return playerStatusFacade.getPlayerStatusByRoomId(roomId, currentUser);
    }

    @GetMapping("/status/byPlayerStatusId/{playerStatusId}")
    public PlayerStatusResponse getPlayerStatusById(@PathVariable Long playerStatusId){
        return playerStatusFacade.getPlayerStatusById(playerStatusId);
    }

    @GetMapping("/status/allPlayersStatuses/{roomId}")
    public List<PlayerStatusResponse> getAllPlayersStatuses(@PathVariable Long roomId){
        return playerStatusFacade.getAllPlayersStatusesInRoom(roomId);
    }

    @GetMapping("join/{roomId}/{roomPassword}")
    public ResponseEntity<?> joinRoom (@PathVariable Long roomId, @PathVariable String roomPassword, @CurrentUser UserPrincipal currentUser){
        ResponseEntity responseEntity = playerStatusFacade.joinRoom(roomId, roomPassword, currentUser);
        return responseEntity;
    }

    @GetMapping("exit/{roomId}/{playerStatusId}")
    public ResponseEntity<?> exitRoom (@PathVariable Long roomId, @PathVariable Long playerStatusId){
        playerStatusFacade.exitRoom(roomId, playerStatusId);
        return ResponseEntity.ok("Player leaved room");
    }

    @GetMapping("/setLevel/{playerStatusId}/{upOrDown}")
    public ResponseEntity<?> setPlayerLevel (@PathVariable Long playerStatusId, @PathVariable Long upOrDown){
        ResponseEntity responseEntity = playerStatusFacade.setPlayerLevel(playerStatusId, upOrDown);
        return responseEntity;
    }

    @GetMapping("/setBonus/{playerStatusId}/{upOrDown}")
    public ResponseEntity<?> setPlayerBonus (@PathVariable Long playerStatusId, @PathVariable Long upOrDown){
        ResponseEntity responseEntity = playerStatusFacade.setPlayerBonus(playerStatusId, upOrDown);
        return responseEntity;
    }

    @GetMapping("/changeGender/{playerStatusId}")
    public ResponseEntity<?> changeGender (@PathVariable Long playerStatusId){
        playerStatusFacade.changeGender(playerStatusId);
        return ResponseEntity.ok("Gender was changed successfully");
    }

    @GetMapping("/changeRace/{playerStatusId}/{raceId}")
    public ResponseEntity<?> changeRace (@PathVariable Long playerStatusId, @PathVariable Long raceId){
        playerStatusFacade.changeRace(playerStatusId, raceId);
        return ResponseEntity.ok("Race was changed successfully");
    }

    @GetMapping("/toggleTwoRaces/{playerStatusId}")
    public void toggleTwoRaces(@PathVariable Long playerStatusId){
        playerStatusFacade.toggleTwoRaces(playerStatusId);
    }

    @GetMapping("/changeSecondRace/{playerStatusId}/{raceId}")
    public ResponseEntity<?> changeSecondRace (@PathVariable Long playerStatusId, @PathVariable Long raceId){
        ResponseEntity<?> responseEntity = playerStatusFacade.changeSecondRace(playerStatusId, raceId);
        return responseEntity;
    }

    @GetMapping("/changeClass/{playerStatusId}/{classId}")
    public ResponseEntity<?> changeClass (@PathVariable Long playerStatusId, @PathVariable Long classId){
        playerStatusFacade.changeClass(playerStatusId, classId);
        return ResponseEntity.ok("Class was changed successfully");
    }

    @GetMapping("/toggleTwoClasses/{playerStatusId}")
    public void toggleTwoClasses(@PathVariable Long playerStatusId){
        playerStatusFacade.toggleTwoClasses(playerStatusId);
    }

    @GetMapping("/changeSecondClass/{playerStatusId}/{classId}")
    public ResponseEntity<?> changeSecondClass (@PathVariable Long playerStatusId, @PathVariable Long classId){
        ResponseEntity<?> responseEntity = playerStatusFacade.changeSecondClass(playerStatusId, classId);
        return responseEntity;
    }

    @DeleteMapping("/delete/oneStatus/byId/{playerStatusId}")
    public ResponseEntity<?> deletePlayerStatus (@PathVariable Long playerStatusId){
        playerStatusFacade.deletePlayerStatus(playerStatusId);
        return ResponseEntity.ok("PlayerStatus was deleted successfully");
    }

    @DeleteMapping("/delete/allStatuses/byRoomId/{roomId}")
    public ResponseEntity<?> deleteAllPlayersStatuses (@PathVariable Long roomId){
        ResponseEntity responseEntity = playerStatusFacade.deletePlayersStatuses(roomId);
        return responseEntity;
    }
}
