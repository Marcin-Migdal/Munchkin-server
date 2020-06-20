package com.project.munchkin.room.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    Long id;
    String roomName;
    Long slots;
    Long usersInRoom;
    Long creatorId;
    String roomPassword;
    boolean isComplete;
}
