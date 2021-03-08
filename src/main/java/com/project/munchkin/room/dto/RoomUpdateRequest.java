package com.project.munchkin.room.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomUpdateRequest {
    Long id;

    @NotBlank
    @Size(min = 3, max = 30)
    String roomName;

    @NotNull
    Long slots;

    @NotBlank
    @Size(min = 4, max = 24)
    String roomPassword;
}
