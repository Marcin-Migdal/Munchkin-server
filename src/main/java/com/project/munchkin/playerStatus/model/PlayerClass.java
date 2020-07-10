package com.project.munchkin.playerStatus.model;

import com.project.munchkin.playerStatus.dto.PlayerClass.PlayerClassDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "classes")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PlayerClass {

    @Id
    Long id;

    @NotNull
    String className;

    @NotNull
    String classIcon;

    @NotNull
    String classDescription;

    public PlayerClass fromDto (PlayerClassDto playerClassDto){
        return PlayerClass.builder()
                .id(playerClassDto.getId())
                .className(playerClassDto.getClassName())
                .classIcon(playerClassDto.getClassIcon())
                .classDescription(playerClassDto.getClassDescription())
                .build();
    }

    public PlayerClassDto dto (){
        return PlayerClassDto.builder()
                .id(id)
                .className(className)
                .classIcon(classIcon)
                .classDescription(classDescription)
                .build();
    }
}
