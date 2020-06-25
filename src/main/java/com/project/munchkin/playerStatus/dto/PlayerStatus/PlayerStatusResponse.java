package com.project.munchkin.playerStatus.dto.PlayerStatus;

import com.project.munchkin.playerStatus.dto.PlayerClass.PlayerClassDto;
import com.project.munchkin.playerStatus.dto.PlayerRace.PlayerRaceDto;
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
    Long playerStatusId;
    Long userId;
    String userName;
    PlayerClassDto playerClassDto;
    PlayerRaceDto playerRaceDto;
    Long playerLevel;
    Long playerBonus;
    String gender;
}
