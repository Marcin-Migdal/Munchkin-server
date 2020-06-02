package com.project.munchkin.rooms.model;

import com.project.munchkin.rooms.dto.RoomDto;
import com.project.munchkin.rooms.dto.RoomResponse;
import com.project.munchkin.users.model.DateAudit;
import com.project.munchkin.users.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "rooms")
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Room extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    String roomName;

    @NotNull
    Long slots;

    @NotNull
    boolean isComplete;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "createdBy", nullable = false)
    User user;

    @NotNull
    String roomPassword;

    public static Room fromDto(RoomDto roomDto) {
        return Room.builder()
                .id(roomDto.getId())
                .roomName(roomDto.getRoomName())
                .slots(roomDto.getSlots())
                .isComplete(roomDto.isComplete)
                .user(roomDto.getUser())
                .roomPassword(roomDto.getRoomPassword())
                .build();
    }

    public RoomDto dto() {
        return RoomDto.builder()
                .id(id)
                .roomName(roomName)
                .slots(slots)
                .isComplete(isComplete)
                .user(user)
                .roomPassword(roomPassword)
                .build();
    }

    public RoomResponse response() {
        return RoomResponse.builder()
                .id(id)
                .roomName(roomName)
                .slots(slots)
                .isComplete(isComplete)
                .creatorName(user.getUsername())
                .roomPassword(roomPassword)
                .build();
    }
}
