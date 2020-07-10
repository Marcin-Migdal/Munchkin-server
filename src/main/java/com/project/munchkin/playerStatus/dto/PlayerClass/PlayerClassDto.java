package com.project.munchkin.playerStatus.dto.PlayerClass;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PUBLIC)
public class PlayerClassDto {
    Long id;
    String className;
    String classIcon;
    String classDescription;
}
