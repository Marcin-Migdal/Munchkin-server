package com.project.munchkin.room.domain;

import com.project.munchkin.playerStatus.domain.PlayerStatusFacade;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.repository.UserRepository;

public class RoomFacadeCreator {

    private RoomFacade roomFacade;

    private RoomRepository roomRepository;

    private UserRepository userRepository;

    private PlayerStatusFacade playerStatusFacade;

    public static RoomFacade createRoomFacade(UserRepository userRepository, RoomRepository roomRepository, PlayerStatusFacade playerStatusFacade) {
        return RoomFacade.builder()
                .userRepository(userRepository)
                .roomRepository(roomRepository)
                .build();
    }
}
