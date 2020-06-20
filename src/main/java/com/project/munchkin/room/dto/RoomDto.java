package com.project.munchkin.room.dto;

import com.project.munchkin.user.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PUBLIC)
public class RoomDto {
    Long id;
    String roomName;
    Long slots;
    Long usersInRoom;
    User user;
    String roomPassword;
    boolean isComplete;
}
