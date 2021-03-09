package com.project.munchkin.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Size;

@Builder
public class UserEditRequest {
    @Size(max = 15)
    @Getter
    private String username;

    @Size(max = 40)
    @Getter
    private String inGameName;

    @Getter
    private String gender;
}