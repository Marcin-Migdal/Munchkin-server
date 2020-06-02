package com.project.munchkin.users.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
public class SignUpRequest {
    @NotBlank
    @Size(min = 4, max = 40)
    @Getter
    private String inGameName;

    @NotBlank
    @Size(min = 3, max = 15)
    @Getter
    @Setter
    private String username;

    @NotBlank
    @Size(max = 40)
    @Email
    @Getter
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    @Getter
    private String userPassword;

    @NotBlank
    @Getter
    private String iconUrl;
}