package com.project.munchkin.playerStatus.domain;

import com.project.munchkin.playerStatus.repository.PlayerClassRepository;
import com.project.munchkin.playerStatus.repository.PlayerRaceRepository;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.user.domain.UserFacade;

public class PlayerStatusFacadeCreator {

    private PlayerStatusFacade playerStatusFacade;

    private UserFacade userFacade;

    private PlayerStatusRepository playerStatusRepository;

    private PlayerRaceRepository playerRaceRepository;

    private PlayerClassRepository playerClassRepository;

    public static PlayerStatusFacade gameFacadeCreator (UserFacade userFacade, PlayerStatusRepository playerStatusRepository,
                                                        PlayerRaceRepository playerRaceRepository, PlayerClassRepository playerClassRepository) {
        return PlayerStatusFacade.builder()
                .userFacade(userFacade)
                .playerStatusRepository(playerStatusRepository)
                .playerRaceRepository(playerRaceRepository)
                .playerClassRepository(playerClassRepository)
                .build();
    }

}
