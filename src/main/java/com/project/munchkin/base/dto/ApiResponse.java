package com.project.munchkin.base.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ApiResponse <O> {
    private Boolean success;
    private String message;
    private O body;

    public ApiResponse(Boolean success, String message){
        this.success = success;
        this.message = message;
    }
}