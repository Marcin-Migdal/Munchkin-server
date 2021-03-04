package com.project.munchkin.room.repository;

import com.project.munchkin.room.model.Room;
import com.project.munchkin.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT CASE WHEN c.usersInRoom >= c.slots THEN true ELSE false END FROM Room c WHERE c.id = ?1" )
    boolean isRoomFull(Long roomId);

    @Query("SELECT CASE WHEN COUNT(c.id) > 0 THEN true ELSE false END FROM Room c WHERE c.roomName = ?1 and isComplete = false" )
    boolean existsByRoomName(String roomName);

    @Query("SELECT c FROM Room c WHERE c.isComplete = false")
    Page<Room> findAllInComplete(Pageable page);

    @Query("SELECT c FROM Room c WHERE c.roomName Like ?1% order by isComplete")
    Page<Room> findSearchedPageableRooms(String searchValue, Pageable page);

    @Query("SELECT c FROM Room c WHERE c.roomName Like ?1% and isComplete = false")
    List<Room> searchRooms(String searchValue, Pageable page);

    @Query("SELECT c FROM Room c WHERE c.isComplete = false and c.user = ?1")
    Page<Room> findUserRooms(User user, Pageable page);

    @Query("SELECT c FROM Room c WHERE c.roomName Like ?1% and c.user = ?2 order by c.isComplete asc, c.roomName asc")
    Page<Room> findSearchedPageableUserRooms(String searchValue, User user, Pageable page);

    @Query("SELECT u.roomId FROM PlayerStatus u WHERE u.user = ?1 and u.playerInRoom = true")
    Optional<Long> findRoomIdWithPlayerIn(User user);
}