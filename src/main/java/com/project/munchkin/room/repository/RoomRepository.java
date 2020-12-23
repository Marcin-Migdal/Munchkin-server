package com.project.munchkin.room.repository;

import com.project.munchkin.room.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT CASE WHEN c.usersInRoom >= c.slots THEN true ELSE false END FROM Room c WHERE c.id = ?1" )
    boolean isRoomFull(Long roomId);

    @Query("SELECT c FROM Room c WHERE c.roomName Like ?1%")
    Page<Room> searchPageableRoom(String searchValue, Pageable page);

    @Query("SELECT CASE WHEN COUNT(c.id) > 0 THEN true ELSE false END FROM Room c WHERE c.roomName = ?1 and isComplete = 0" )
    boolean existsByRoomName(String roomName);

    @Query("SELECT c FROM Room c WHERE c.isComplete = 0")
    Page<Room> findAllComplete(Pageable page);
}