package com.project.munchkin.playerStatus.dto.PlayerStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PUBLIC)
public class PlayerStatusDto {
    Long id;
    Long roomId;
    Long userId;
    Long classId;
    boolean twoClasses;
    Long secondClassId;
    Long raceId;
    boolean twoRaces;
    Long secondRaceId;
    Long playerLevel;
    Long playerBonus;
    boolean playerInRoom;
    String gender;
}
