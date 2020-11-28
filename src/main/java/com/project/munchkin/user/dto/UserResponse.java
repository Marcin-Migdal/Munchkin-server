package com.project.munchkin.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String username;
    String inGameName;
    String iconUrl;
    String gender;
}
