package com.project.munchkin.rooms.dto;

import com.project.munchkin.users.model.User;
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
    User user;
    String roomPassword;
    boolean isComplete;
}
