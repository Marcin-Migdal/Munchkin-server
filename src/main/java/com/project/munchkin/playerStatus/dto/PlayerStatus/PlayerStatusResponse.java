package com.project.munchkin.playerStatus.dto.PlayerStatus;

import com.project.munchkin.playerStatus.dto.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRaceDto;
import com.project.munchkin.user.dto.UserResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PUBLIC)
public class PlayerStatusResponse {
    Long id;
    UserResponse user;
    PlayerClassDto playerClassDto;
    PlayerClassDto secondPlayerClassDto;
    boolean twoClasses;
    PlayerRaceDto playerRaceDto;
    PlayerRaceDto secondPlayerRaceDto;
    boolean twoRaces;
    Long playerLevel;
    Long playerBonus;
    boolean playerInRoom;
    String gender;
}
