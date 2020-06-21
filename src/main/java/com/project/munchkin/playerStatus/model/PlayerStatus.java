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
    Long raceId;

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
                .raceId(playerStatusDto.getRaceId())
                .playerLevel(playerStatusDto.getPlayerLevel())
                .playerBonus(playerStatusDto.getPlayerBonus())
                .playerInRoom(playerStatusDto.getPlayerInRoom())
                .gender(playerStatusDto.gender)
                .build();
    }

    public PlayerStatusDto dto() {
        return PlayerStatusDto.builder()
                .id(id)
                .roomId(roomId)
                .userId(userId)
                .classId(classId)
                .raceId(raceId)
                .playerLevel(playerLevel)
                .playerBonus(playerBonus)
                .playerInRoom(playerInRoom)
                .gender(gender)
                .build();
    }
}
