package com.project.munchkin.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Builder
public class UserPasswordRecoveryRequest {
    @Size(min = 3, max = 15)
    @Getter
    private String username;

    @Size(min = 6, max = 20)
    @Getter
    private String userPassword;

    @Size(max = 40)
    @Email
    @Getter
    private String email;

}