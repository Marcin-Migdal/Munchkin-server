package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.playerStatus.repository.PlayerClassRepository;
import com.project.munchkin.playerStatus.repository.PlayerRaceRepository;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.repository.UserRepository;

public class PlayerStatusFacadeCreator {

    private PlayerStatusFacade playerStatusFacade;
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private PlayerStatusRepository playerStatusRepository;
    private PlayerRaceRepository playerRaceRepository;
    private PlayerClassRepository playerClassRepository;

    public static PlayerStatusFacade createPlayerStatusFacade (UserRepository userRepository, RoomRepository roomRepository, PlayerStatusRepository playerStatusRepository,
                                                        PlayerRaceRepository playerRaceRepository, PlayerClassRepository playerClassRepository) {
        return PlayerStatusFacade.builder()
                .userRepository(userRepository)
                .roomRepository(roomRepository)
                .playerStatusRepository(playerStatusRepository)
                .playerRaceRepository(playerRaceRepository)
                .playerClassRepository(playerClassRepository)
                .build();
    }

}
