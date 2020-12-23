package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.playerStatus.dto.EditRequest.BonusLevelEditRequest;
import com.project.munchkin.playerStatus.dto.EditRequest.ClassRaceEditRequest;
import com.project.munchkin.playerStatus.dto.JoinLeaveRequest;
import com.project.munchkin.playerStatus.dto.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRaceDto;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import com.project.munchkin.playerStatus.exception.RoomIsFullException;
import com.project.munchkin.playerStatus.exception.UserAlreadyInRoomException;
import com.project.munchkin.playerStatus.exception.WrongValueException;
import com.project.munchkin.room.exception.NotAuthorizedException;
import com.project.munchkin.room.exception.ResourceNotFoundException;
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
    public ResponseEntity<?> getRace(@PathVariable Long raceId){
        try{
            PlayerRaceDto playerRaceDto = playerStatusFacade.getRace(raceId);
            return ResponseEntity.ok().body(new ApiResponse<PlayerRaceDto>(true, "Race returned successfully", playerRaceDto));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/race/getAll")
    public ResponseEntity<?> getAllRaces(){
        try{
            List<PlayerRaceDto> allPlayerRaces = playerStatusFacade.getAllRaces();
            return ResponseEntity.ok().body(new ApiResponse<List<PlayerRaceDto>>(true, "All races returned successfully", allPlayerRaces));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/class/byId/{classId}")
    public ResponseEntity<?> getClass(@PathVariable Long classId){
        try{
            PlayerClassDto classDto = playerStatusFacade.getClass(classId);
            return ResponseEntity.ok().body(new ApiResponse<PlayerClassDto>(true, "Class returned successfully", classDto));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/class/getAll")
    public ResponseEntity<?> getAllClasses(){
        try{
            List<PlayerClassDto> allClasses = playerStatusFacade.getAllClasses();
            return ResponseEntity.ok().body(new ApiResponse<List<PlayerClassDto>>(true, "All classes returned successfully", allClasses));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/status/byRoomId/{roomId}")
    public ResponseEntity<?> getPlayerStatusByRoomId(@PathVariable Long roomId, @CurrentUser UserPrincipal currentUser){
        try{
            PlayerStatusResponse playerStatusResponse = playerStatusFacade.getPlayerStatusResponseByRoomId(roomId, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse<PlayerStatusResponse>(true,
                    "Player Status returned successfully by room id: " + roomId + " and user id: " + currentUser.getId(), playerStatusResponse));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/status/byPlayerStatusId/{playerStatusId}")
    public ResponseEntity<?> getPlayerStatusById(@PathVariable Long playerStatusId){
        try{
            PlayerStatusResponse playerStatusResponse = playerStatusFacade.getPlayerStatusResponseById(playerStatusId);
            return ResponseEntity.ok().body(new ApiResponse<PlayerStatusResponse>(true,
                    "Player Status returned successfully by player Status id: " + playerStatusId, playerStatusResponse));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/status/allPlayersStatuses/{roomId}")
    public ResponseEntity<?> getAllPlayersStatuses(@PathVariable Long roomId){
        try{
            List<PlayerStatusResponse> allPlayersStatusResponse = playerStatusFacade.getAllPlayersStatusResponse(roomId);
            return ResponseEntity.ok().body(new ApiResponse<List<PlayerStatusResponse>>(true,
                    "All Player Statuses in the room returned successfully by room id: " + roomId, allPlayersStatusResponse));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PutMapping("joinRoom")
    public ResponseEntity<?> joinRoom (@Valid @RequestBody JoinLeaveRequest joinLeaveRequest, @CurrentUser UserPrincipal currentUser){
        try{
            playerStatusFacade.joinRoom(joinLeaveRequest.getRoomId(), joinLeaveRequest.getRoomPassword(), currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse<>(true, "Player joined room successfully"));
        }catch (ResourceNotFoundException | UserAlreadyInRoomException | RoomIsFullException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("exitRoom")
    public ResponseEntity<?> exitRoom (@RequestBody JoinLeaveRequest joinLeaveRequest, @CurrentUser UserPrincipal currentUser){
        try{
            playerStatusFacade.exitRoom(joinLeaveRequest.getRoomId(), currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player leaves the room successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/setLevel")
    public ResponseEntity<?> setPlayerLevel (@RequestBody BonusLevelEditRequest bonusLevelEditRequest){
        try{
            playerStatusFacade.setPlayerLevel(bonusLevelEditRequest.getPlayerStatusId(), bonusLevelEditRequest.getNewValue());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player level is set successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException | WrongValueException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/setBonus")
    public ResponseEntity<?> setPlayerBonus (@RequestBody BonusLevelEditRequest bonusLevelEditRequest){
        try{
            playerStatusFacade.setPlayerBonus(bonusLevelEditRequest.getPlayerStatusId(), bonusLevelEditRequest.getNewValue());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player bonus is set successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException | WrongValueException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/changeGender/{playerStatusId}")
    public ResponseEntity<?> changeGender (@PathVariable Long playerStatusId){
        try{
            String changedGender = playerStatusFacade.changeGender(playerStatusId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player gender was changed successfully to: " + changedGender));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/changeRace")
    public ResponseEntity<?> changeFirstRace (@RequestBody ClassRaceEditRequest classRaceEditRequest){
        try{
            playerStatusFacade.setFirstRace(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player race was changed successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/changeSecondRace")
    public ResponseEntity<?> changeSecondRace (@RequestBody ClassRaceEditRequest classRaceEditRequest){
        try{
            playerStatusFacade.setSecondRace(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Second player race was changed successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/setTwoRaces/{playerStatusId}")
    public ResponseEntity<?> setTwoRaces(@PathVariable Long playerStatusId){
        try{
            boolean isTwoRaces = playerStatusFacade.toggleTwoRaces(playerStatusId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player can have two races: " + isTwoRaces));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/changeClass")
    public ResponseEntity changeFirstClass(@RequestBody ClassRaceEditRequest classRaceEditRequest){
        try{
            playerStatusFacade.setFirstClass(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player class was changed successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/changeSecondClass")
    public ResponseEntity<?> changeSecondClass (@RequestBody ClassRaceEditRequest classRaceEditRequest){
        try{
            playerStatusFacade.setSecondClass(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Second player class was changed successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/setTwoClasses/{playerStatusId}")
    public ResponseEntity<?> setTwoClasses(@PathVariable Long playerStatusId){
        try{
            boolean isTwoClasses = playerStatusFacade.toggleTwoClasses(playerStatusId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player can have two classes: " + isTwoClasses));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/getGameSummary/{roomId}")
    public ResponseEntity<?> getGameSummary (@PathVariable Long roomId){
        try{
            List<PlayerStatusResponse> gameSummary = playerStatusFacade.getGameSummary(roomId);
            return ResponseEntity.ok().body(new ApiResponse<List<PlayerStatusResponse>>(true,"Game Summary returned successfully", gameSummary));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @DeleteMapping("/delete/oneStatus/byId/{playerStatusId}")
    public ResponseEntity<?> deletePlayerStatus (@PathVariable Long playerStatusId, @CurrentUser UserPrincipal currentUser){
        try{
            playerStatusFacade.deletePlayerStatus(playerStatusId, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse<>(true, "Player Status was deleted successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @DeleteMapping("/delete/allStatuses/byRoomId/{roomId}")
    public ResponseEntity<?> deleteAllPlayersStatuses (@PathVariable Long roomId, @CurrentUser UserPrincipal currentUser){
        try{
            playerStatusFacade.deletePlayersStatuses(roomId, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"All statuses in the room were deleted"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

}
