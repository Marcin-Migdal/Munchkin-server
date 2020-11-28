package com.project.munchkin.user.dto;

import lombok.Getter;

import javax.validation.constraints.Size;

public class ChangePasswordRequest {
    @Size(min = 6, max = 20)
    @Getter
    private String userPassword;
}


