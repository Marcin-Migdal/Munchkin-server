package com.project.munchkin.room.domain;

import com.project.munchkin.playerStatus.domain.PlayerStatusFacade;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.domain.UserFacade;

public class RoomFacadeCreator {

    private RoomFacade roomFacade;

    private RoomRepository roomRepository;

    private UserFacade userFacade;

    private PlayerStatusFacade playerStatusFacade;

    public static RoomFacade createRoomFacade(UserFacade userFacade, RoomRepository roomRepository, PlayerStatusFacade playerStatusFacade) {
        return RoomFacade.builder()
                .userFacade(userFacade)
                .roomRepository(roomRepository)
                .build();
    }
}
