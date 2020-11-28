package com.project.munchkin.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Builder
public class UserEditRequest {
    @Size(max = 15)
    @Getter
    @Setter
    private String username;

    @Size(max = 40)
    @Getter
    private String inGameName;

    @Size(min = 6, max = 20)
    @Getter
    private String userPassword;

    @Getter
    private String iconUrl;

    @Getter
    private String gender;
}