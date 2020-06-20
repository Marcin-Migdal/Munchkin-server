package com.project.munchkin.playerStatus.repository;

import com.project.munchkin.playerStatus.model.PlayerClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerClassRepository extends JpaRepository<PlayerClass, Long> {

}
