package com.project.munchkin.room.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomUpdateRequest {
    Long id;
    String roomName;
    Long slots;
    String roomPassword;
}
