package com.project.munchkin.playerStatus.repository;

import com.project.munchkin.playerStatus.model.PlayerRace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRaceRepository extends JpaRepository<PlayerRace, Long> {

}
