package com.project.munchkin.room.repository;

import com.project.munchkin.room.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT CASE WHEN c.usersInRoom >= c.slots THEN true ELSE false END FROM Room c WHERE c.id = ?1" )
    boolean isRoomFull(Long roomId);
}