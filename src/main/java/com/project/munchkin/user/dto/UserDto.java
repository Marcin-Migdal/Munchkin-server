package com.project.munchkin.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public class UserDto {
    Long id;
    String inGameName;
    String username;
    String email;
    String userPassword;
    String iconUrl;
    String gender;
}