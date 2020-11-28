package com.project.munchkin.room.model;

import com.project.munchkin.room.dto.RoomDto;
import com.project.munchkin.room.dto.RoomResponse;
import com.project.munchkin.user.model.DateAudit;
import com.project.munchkin.user.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "roomName"
        })
})
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
    Long usersInRoom;

    @NotNull
    boolean isComplete;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    User user;

    @NotNull
    String roomPassword;

    public static Room fromDto(RoomDto roomDto) {
        return Room.builder()
                .id(roomDto.getId())
                .roomName(roomDto.getRoomName())
                .slots(roomDto.getSlots())
                .usersInRoom(roomDto.getUsersInRoom())
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
                .usersInRoom(usersInRoom)
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
                .usersInRoom(usersInRoom)
                .isComplete(isComplete)
                .creatorId(user.getId())
                .roomPassword(roomPassword)
                .build();
    }
}
