package com.project.munchkin.playerStatus.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PUBLIC)
public class PlayerRaceDto {
    Long id;
    String raceName;
    String raceIcon;
    String raceDescription;
}
