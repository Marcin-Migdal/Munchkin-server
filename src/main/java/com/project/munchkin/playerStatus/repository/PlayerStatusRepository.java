package com.project.munchkin.playerStatus.repository;

import com.project.munchkin.playerStatus.model.PlayerStatus;
import com.project.munchkin.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerStatusRepository extends JpaRepository<PlayerStatus, Long> {

    @Query("SELECT u FROM PlayerStatus u WHERE u.roomId = ?1 and u.user = ?2")
    Optional<PlayerStatus> findByRoomIdAndUserId(Long roomId, User user);

    @Query("SELECT CASE WHEN COUNT(u.id) > 0 THEN true ELSE false END FROM PlayerStatus u WHERE u.user = ?1 AND u.playerInRoom = true" )
    boolean playerIsInAnyRoom(User user);

    @Query("SELECT u FROM PlayerStatus u WHERE u.roomId = ?1" )
    List<PlayerStatus> findAllPlayerStatusesByRoomId(Long roomId);

    @Query("SELECT u FROM PlayerStatus u WHERE u.roomId = ?1 ORDER BY u.playerLevel desc" )
    List<PlayerStatus> findAllSortedPlayerStatusByRoomId(Long roomId);

    @Query("SELECT u FROM PlayerStatus u WHERE u.roomId = ?1 and u.playerInRoom = true" )
    List<PlayerStatus> findAllPlayerStatusesInRoom(Long roomId);

    @Transactional
    @Modifying
    @Query("UPDATE PlayerStatus u SET u.playerInRoom = false where u.roomId = ?1 " )
    void allPLayersLeaveRoom(Long roomId);
}
