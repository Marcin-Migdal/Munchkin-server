package com.project.munchkin.playerStatus.model;

import com.project.munchkin.playerStatus.dto.PlayerRaceDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
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
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    String raceName;

    @NotNull
    @Lob
    String raceDescription;

    public  PlayerRaceDto dto (){
        return PlayerRaceDto.builder()
                .id(id)
                .name(raceName)
                .description(raceDescription)
                .build();
    }
}
