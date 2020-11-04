package com.project.munchkin.playerStatus.model;

import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "player_status")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    Long roomId;

    @NotNull
    Long userId;

    @NotNull
    Long classId;

    @NotNull
    boolean twoClasses;

    @NotNull
    Long secondClassId;

    @NotNull
    Long raceId;

    @NotNull
    boolean twoRaces;

    @NotNull
    Long secondRaceId;

    @NotNull
    Long playerLevel;

    @NotNull
    Long playerBonus;

    @NotNull
    Boolean playerInRoom;

    @NotBlank
    String gender;

    public static PlayerStatus fromDto(PlayerStatusDto playerStatusDto) {
        return PlayerStatus.builder()
                .id(playerStatusDto.getId())
                .roomId(playerStatusDto.getRoomId())
                .userId(playerStatusDto.getUserId())
                .classId(playerStatusDto.getClassId())
                .twoClasses(playerStatusDto.isTwoClasses())
                .secondClassId(playerStatusDto.getSecondClassId())
                .raceId(playerStatusDto.getRaceId())
                .twoRaces(playerStatusDto.isTwoRaces())
                .secondRaceId(playerStatusDto.getSecondRaceId())
                .playerLevel(playerStatusDto.getPlayerLevel())
                .playerBonus(playerStatusDto.getPlayerBonus())
                .playerInRoom(playerStatusDto.playerInRoom)
                .gender(playerStatusDto.getGender())
                .build();
    }

    public PlayerStatusDto dto() {
        return PlayerStatusDto.builder()
                .id(id)
                .roomId(roomId)
                .userId(userId)
                .classId(classId)
                .twoClasses(twoClasses)
                .secondClassId(secondClassId)
                .raceId(raceId)
                .twoRaces(twoRaces)
                .secondRaceId(secondRaceId)
                .playerLevel(playerLevel)
                .playerBonus(playerBonus)
                .playerInRoom(playerInRoom)
                .gender(gender)
                .build();
    }
}
