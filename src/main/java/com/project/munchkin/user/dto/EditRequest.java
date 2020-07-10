package com.project.munchkin.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Builder
public class EditRequest {
    @Size(min = 3, max = 15)
    @Getter
    @Setter
    private String username;

    @Size(min = 4, max = 40)
    @Getter
    private String inGameName;

    @Size(min = 6, max = 20)
    @Getter
    private String userPassword;

    @Getter
    private String iconUrl;

    @Size(max = 40)
    @Email
    @Getter
    private String email;

}