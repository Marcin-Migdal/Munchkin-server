package com.project.munchkin.playerStatus.model;

import com.project.munchkin.playerStatus.dto.PlayerStatus.PlayerStatusDto;
import com.project.munchkin.user.model.User;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user", nullable = false)
    User user;

    @NotNull
    Long classId;

    @NotNull
    Long secondClassId;

    @NotNull
    boolean twoClasses;

    @NotNull
    Long raceId;

    @NotNull
    Long secondRaceId;

    @NotNull
    boolean twoRaces;

    @NotNull
    Long playerLevel;

    @NotNull
    Long playerBonus;

    @NotNull
    boolean playerInRoom;

    @NotBlank
    String gender;

    public static PlayerStatus fromDto(PlayerStatusDto playerStatusDto) {
        return PlayerStatus.builder()
                .id(playerStatusDto.getId())
                .roomId(playerStatusDto.getRoomId())
                .user(playerStatusDto.getUser())
                .secondClassId(playerStatusDto.getSecondClassId())
                .classId(playerStatusDto.getClassId())
                .twoClasses(playerStatusDto.isTwoClasses())
                .raceId(playerStatusDto.getRaceId())
                .secondRaceId(playerStatusDto.getSecondRaceId())
                .twoRaces(playerStatusDto.isTwoRaces())
                .playerLevel(playerStatusDto.getPlayerLevel())
                .playerBonus(playerStatusDto.getPlayerBonus())
                .playerInRoom(playerStatusDto.isPlayerInRoom())
                .gender(playerStatusDto.getGender())
                .build();
    }

    public PlayerStatusDto dto() {
        return PlayerStatusDto.builder()
                .id(id)
                .roomId(roomId)
                .user(user)
                .classId(classId)
                .secondClassId(secondClassId)
                .twoClasses(twoClasses)
                .raceId(raceId)
                .secondRaceId(secondRaceId)
                .twoRaces(twoRaces)
                .playerLevel(playerLevel)
                .playerBonus(playerBonus)
                .playerInRoom(playerInRoom)
                .gender(gender)
                .build();
    }
}