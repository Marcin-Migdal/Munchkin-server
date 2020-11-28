package com.project.munchkin.playerStatus.model;

import com.project.munchkin.playerStatus.dto.PlayerClassDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "classes")
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PlayerClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    Long id;

    @NotNull
    String className;

    @NotNull
    String classIcon;

    @NotNull
    String classDescription;

    public PlayerClassDto dto (){
        return PlayerClassDto.builder()
                .id(id)
                .className(className)
                .classIcon(classIcon)
                .classDescription(classDescription)
                .build();
    }
}
