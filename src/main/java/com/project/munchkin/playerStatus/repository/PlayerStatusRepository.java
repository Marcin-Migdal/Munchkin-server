package com.project.munchkin.playerStatus.repository;

import com.project.munchkin.playerStatus.model.PlayerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerStatusRepository extends JpaRepository<PlayerStatus, Long> {

    @Query("SELECT u FROM PlayerStatus u WHERE u.roomId = ?1 and u.userId = ?2")
    Optional<PlayerStatus> findByRoomIdAndUserId(Long roomId, Long userId);

    @Query("SELECT CASE WHEN COUNT(c.id) > 0 THEN true ELSE false END FROM PlayerStatus c WHERE c.userId = ?1 AND c.playerInRoom = 1" )
    boolean playerIsInAnyRoom(Long userId);

    @Query("SELECT u FROM PlayerStatus u WHERE u.roomId = ?1" )
    List<PlayerStatus> findAllPlayerStatusByRoomId(Long roomId);

    @Query("SELECT u FROM PlayerStatus u WHERE u.roomId = ?1 ORDER BY u.playerLevel desc" )
    List<PlayerStatus> findAllSortedPlayerStatusByRoomId(Long roomId);
}
