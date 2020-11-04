package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.playerStatus.dto.EditRequest.BonusLevelEditRequest;
import com.project.munchkin.playerStatus.dto.EditRequest.ClassRaceEditRequest;
import com.project.munchkin.playerStatus.dto.JoinLeaveRequest;
import com.project.munchkin.playerStatus.dto.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRaceDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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


    @DeleteMapping("/delete/oneStatus/byId/{playerStatusId}")
    public ResponseEntity<?> deletePlayerStatus (@PathVariable Long playerStatusId){
        playerStatusFacade.deletePlayerStatus(playerStatusId);
        return ResponseEntity.ok("Player Status was deleted successfully");
    }

    @DeleteMapping("/delete/allStatuses/byRoomId/{roomId}")
    public ResponseEntity<?> deleteAllPlayersStatuses (@PathVariable Long roomId){
        return playerStatusFacade.deletePlayersStatuses(roomId);
    }

    @PutMapping("joinRoom")
    public ResponseEntity<?> joinRoom (@Valid @RequestBody JoinLeaveRequest joinLeaveRequest, @CurrentUser UserPrincipal currentUser){
        return playerStatusFacade.joinRoom(joinLeaveRequest.getRoomId(), joinLeaveRequest.getRoomPassword(), currentUser);
    }

    @PatchMapping("exitRoom")
    public ResponseEntity<?> exitRoom (@RequestBody JoinLeaveRequest joinLeaveRequest, @CurrentUser UserPrincipal currentUser){
        playerStatusFacade.exitRoom(joinLeaveRequest.getRoomId(), currentUser.getId());
        return ResponseEntity.ok("Player leaves the room");
    }

    @PatchMapping("/setLevel")
    public ResponseEntity<?> setPlayerLevel (@RequestBody BonusLevelEditRequest bonusLevelEditRequest){
        return playerStatusFacade.setPlayerLevel(bonusLevelEditRequest.getPlayerStatusId(), bonusLevelEditRequest.getNewValue());
    }

    @PatchMapping("/setBonus")
    public ResponseEntity<?> setPlayerBonus (@RequestBody BonusLevelEditRequest bonusLevelEditRequest){
        return playerStatusFacade.setPlayerBonus(bonusLevelEditRequest.getPlayerStatusId(), bonusLevelEditRequest.getNewValue());
    }

    @PatchMapping("/changeGender/{playerStatusId}")
    public ResponseEntity<?> changeGender (@PathVariable Long playerStatusId){
        return playerStatusFacade.changeGender(playerStatusId);
    }

    @PatchMapping("/changeRace")
    public ResponseEntity<?> changeFirstRace (@RequestBody ClassRaceEditRequest classRaceEditRequest){
        return playerStatusFacade.changeFirstRace(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
    }

    @PatchMapping("/changeSecondRace")
    public ResponseEntity<?> changeSecondRace (@RequestBody ClassRaceEditRequest classRaceEditRequest){
        return playerStatusFacade.changeSecondRace(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
    }

    @PatchMapping("/setTwoRaces/{playerStatusId}")
    public ResponseEntity<?> setTwoRaces(@PathVariable Long playerStatusId){
        return playerStatusFacade.toggleTwoRaces(playerStatusId);
    }

    @PatchMapping("/changeClass")
    public ResponseEntity changeFirstClass(@RequestBody ClassRaceEditRequest classRaceEditRequest){
        return playerStatusFacade.changeClass(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
    }

    @PatchMapping("/changeSecondClass")
    public ResponseEntity<?> changeSecondClass (@RequestBody ClassRaceEditRequest classRaceEditRequest){
        return playerStatusFacade.changeSecondClass(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
    }

    @PatchMapping("/setTwoClasses/{playerStatusId}")
    public ResponseEntity<?> setTwoClasses(@PathVariable Long playerStatusId){
        return playerStatusFacade.toggleTwoClasses(playerStatusId);
    }
}
