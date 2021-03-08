package com.project.munchkin.user.dto;

import lombok.Getter;

import javax.validation.constraints.Size;

public class ChangePasswordRequest {
    @Size(min = 6, max = 15)
    @Getter
    private String username;

    @Size(min = 6, max = 20)
    @Getter
    private String oldPassword;

    @Size(min = 6, max = 20)
    @Getter
    private String newPassword;

    @Size(min = 6, max = 20)
    @Getter
    private String newRePassword;
}


