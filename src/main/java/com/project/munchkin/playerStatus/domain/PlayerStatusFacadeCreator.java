package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.playerStatus.repository.PlayerClassRepository;
import com.project.munchkin.playerStatus.repository.PlayerRaceRepository;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.user.repository.UserRepository;

public class PlayerStatusFacadeCreator {

    private PlayerStatusFacade playerStatusFacade;

    private UserRepository userRepository;

    private PlayerStatusRepository playerStatusRepository;

    private PlayerRaceRepository playerRaceRepository;

    private PlayerClassRepository playerClassRepository;

    public static PlayerStatusFacade gameFacadeCreator (UserRepository userRepository, PlayerStatusRepository playerStatusRepository,
                                                        PlayerRaceRepository playerRaceRepository, PlayerClassRepository playerClassRepository) {
        return PlayerStatusFacade.builder()
                .userRepository(userRepository)
                .playerStatusRepository(playerStatusRepository)
                .playerRaceRepository(playerRaceRepository)
                .playerClassRepository(playerClassRepository)
                .build();
    }

}
