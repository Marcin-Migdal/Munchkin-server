package com.project.munchkin.room.domain;

import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.domain.UserFacade;

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
