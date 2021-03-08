package com.project.munchkin.playerStatus.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RaceAndClassResponse {
    List<PlayerRaceDto> playerRaces;
    List<PlayerClassDto> playerClasses;
}
