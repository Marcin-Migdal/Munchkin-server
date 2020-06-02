package com.project.munchkin.rooms.domain;

import com.project.munchkin.rooms.repository.RoomRepository;
import com.project.munchkin.users.domain.UserFacade;

public class RoomFacadeCreator {

    private RoomFacade roomFacade;

    private RoomRepository roomRepository;

    private UserFacade userFacade;

    public static RoomFacade createRoomFacade(UserFacade userFacade, RoomRepository roomRepository) {
        return RoomFacade.builder()
                .userFacade(userFacade)
                .roomRepository(roomRepository)
                .build();
    }
}
