package com.project.munchkin.playerStatus.model;

import com.project.munchkin.playerStatus.dto.PlayerRaceDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "races")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRace {

    @Id
    Long id;

    @NotNull
    String raceName;

    @NotNull
    String raceIcon;

    @NotNull
    String raceDescription;

    public PlayerRace fromDto (PlayerRaceDto playerRaceDto){
        return PlayerRace.builder()
                .id(playerRaceDto.getId())
                .raceName(playerRaceDto.getRaceName())
                .raceIcon(playerRaceDto.getRaceIcon())
                .raceDescription(playerRaceDto.getRaceDescription())
                .build();
    }

    public  PlayerRaceDto dto (){
        return PlayerRaceDto.builder()
                .id(id)
                .raceName(raceName)
                .raceIcon(raceIcon)
                .raceDescription(raceDescription)
                .build();
    }
}
