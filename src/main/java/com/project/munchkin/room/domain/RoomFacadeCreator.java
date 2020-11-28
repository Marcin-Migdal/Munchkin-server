package com.project.munchkin.room.domain;

import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.repository.UserRepository;

public class RoomFacadeCreator {

    private RoomFacade roomFacade;
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private PlayerStatusRepository playerStatusRepository;

    public static RoomFacade createRoomFacade(UserRepository userRepository, RoomRepository roomRepository, PlayerStatusRepository playerStatusRepository) {
        return RoomFacade.builder()
                .userRepository(userRepository)
                .roomRepository(roomRepository)
                .playerStatusRepository(playerStatusRepository)
                .build();
    }
}
