package com.project.munchkin.room.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomUpdateRequest {
    Long id;
    String roomName;
    Long slots;
    String roomPassword;
}
