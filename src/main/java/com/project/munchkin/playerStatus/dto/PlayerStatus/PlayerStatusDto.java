package com.project.munchkin.playerStatus.dto.PlayerStatus;

import com.project.munchkin.user.model.User;
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
    User user;
    Long roomId;
    Long classId;
    Long secondClassId;
    boolean twoClasses;
    Long raceId;
    Long secondRaceId;
    boolean twoRaces;
    Long playerLevel;
    Long playerBonus;
    boolean playerInRoom;
    String gender;
}
