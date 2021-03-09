package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.base.dto.ApiResponse;
import com.project.munchkin.base.security.CurrentUser;
import com.project.munchkin.base.security.UserPrincipal;
import com.project.munchkin.playerStatus.dto.EditRequest.BonusLevelEditRequest;
import com.project.munchkin.playerStatus.dto.EditRequest.ClassRaceEditRequest;
import com.project.munchkin.playerStatus.dto.JoinRequest;
import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusResponse;
import com.project.munchkin.playerStatus.dto.RaceAndClassResponse;
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

    @GetMapping("/getAllRacesAndClasses")
    public ResponseEntity<?> getAllRacesAndClasses(){
        try{
            RaceAndClassResponse allPlayerRacesAndClasses = playerStatusFacade.getAllRacesAndClasses();
            return ResponseEntity.ok().body(new ApiResponse<>(true, "All races and classes returned successfully", allPlayerRacesAndClasses));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/byRoomId/{roomId}")
    public ResponseEntity<?> getPlayerStatusByRoomId(@PathVariable Long roomId, @CurrentUser UserPrincipal currentUser){
        try{
            PlayerStatusResponse playerStatusResponse = playerStatusFacade.getPlayerStatusResponseByRoomId(roomId, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,
                    "Player Status returned successfully by room id: " + roomId + " and user id: " + currentUser.getId(), playerStatusResponse));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/byPlayerStatusId/{playerStatusId}")
    public ResponseEntity<?> getPlayerStatusById(@PathVariable Long playerStatusId){
        try{
            PlayerStatusResponse playerStatusResponse = playerStatusFacade.getPlayerStatusResponseById(playerStatusId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,
                    "Player Status returned successfully by player Status id: " + playerStatusId, playerStatusResponse));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/allPlayersStatuses/{roomId}")
    public ResponseEntity<?> getAllPlayersStatuses(@PathVariable Long roomId){
        try{
            List<PlayerStatusResponse> allPlayersStatusResponse = playerStatusFacade.getAllPlayersStatusesResponse(roomId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,
                    "All Player Statuses in the room returned successfully by room id: " + roomId, allPlayersStatusResponse));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/allPlayersStatusesInRoom/{roomId}")
    public ResponseEntity<?> getPlayersStatusesResponseInRoom(@PathVariable Long roomId){
        try{
            List<PlayerStatusResponse> allPlayersStatusResponse = playerStatusFacade.getPlayersStatusesResponseInRoom(roomId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,
                    "Player Statuses in the room returned successfully by room id: " + roomId, allPlayersStatusResponse));
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PutMapping("joinRoom")
    public ResponseEntity<?> joinRoom (@Valid @RequestBody JoinRequest joinRequest, @CurrentUser UserPrincipal currentUser){
        try{
            playerStatusFacade.joinRoom(joinRequest.getRoomId(), joinRequest.getRoomPassword(), currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse<>(true, "Player joined room successfully"));
        }catch (ResourceNotFoundException | UserAlreadyInRoomException | RoomIsFullException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("exitRoom/{roomId}")
    public ResponseEntity<?> exitRoom (@PathVariable Long roomId, @CurrentUser UserPrincipal currentUser){
        try{
            playerStatusFacade.exitRoom(roomId, currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player leaves the room successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/exitRoomOnLogIn")
    public ResponseEntity<?> exitRoomOnLogIn (@CurrentUser UserPrincipal currentUser){
        try{
            playerStatusFacade.exitRoomOnLogIn(currentUser.getId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player leaves the room successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PutMapping("/setPlayerStatus")
    public ResponseEntity<?> setPlayerStatus (@RequestBody BonusLevelEditRequest bonusLevelEditRequest){
        try{
            playerStatusFacade.setPlayerStatus(bonusLevelEditRequest);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player level is set successfully"));
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

    @PatchMapping("/race/changeRace")
    public ResponseEntity<?> changeFirstRace (@RequestBody ClassRaceEditRequest classRaceEditRequest){
        try{
            playerStatusFacade.setFirstRace(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player race was changed successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/race/changeSecondRace")
    public ResponseEntity<?> changeSecondRace (@RequestBody ClassRaceEditRequest classRaceEditRequest){
        try{
            playerStatusFacade.setSecondRace(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Second player race was changed successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/class/changeClass")
    public ResponseEntity changeFirstClass(@RequestBody ClassRaceEditRequest classRaceEditRequest){
        try{
            playerStatusFacade.setFirstClass(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Player class was changed successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @PatchMapping("/class/changeSecondClass")
    public ResponseEntity<?> changeSecondClass (@RequestBody ClassRaceEditRequest classRaceEditRequest){
        try{
            playerStatusFacade.setSecondClass(classRaceEditRequest.getPlayerStatusId(), classRaceEditRequest.getNewId());
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Second player class was changed successfully"));
        }catch (ResourceNotFoundException | NotAuthorizedException e){
            return new ResponseEntity<>(new ApiResponse <>(false, e.getMessage()), e.getHttpStatus());
        }
    }

    @GetMapping("/getGameSummary/{roomId}")
    public ResponseEntity<?> getGameSummary (@PathVariable Long roomId){
        try{
            List<PlayerStatusResponse> gameSummary = playerStatusFacade.getGameSummary(roomId);
            return ResponseEntity.ok().body(new ApiResponse<>(true,"Game Summary returned successfully", gameSummary));
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
