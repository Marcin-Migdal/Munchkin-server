package groovy.com.project.munchkin.rooms;

import com.project.munchkin.room.dto.RoomDto;
import com.project.munchkin.room.model.Room;
import com.project.munchkin.room.repository.RoomRepository;
import com.project.munchkin.user.model.User;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class RoomInMemoryRepository implements RoomRepository {

    private final Map<Long, Room> rooms = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public Optional<Room> findById(Long roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    @Override
    public boolean existsById(Long aLong) {
        return rooms.containsKey(aLong);
    }

    @Override
    public List<Room> findAll() {
        return null;
    }

    @Override
    public List<Room> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Room> findAll(Pageable pageable) {
        List<Room> roomList = new ArrayList<>(rooms.values());
        return new PageImpl<>(roomList, pageable, roomList.size());
    }

    @Override
    public List<Room> findAllById(Iterable<Long> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        rooms.remove(aLong);
    }

    @Override
    public void delete(Room room) {

    }

    @Override
    public void deleteAll(Iterable<? extends Room> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Room> S save(S s) {
        Long id = s.getId();
        if (id == null) {
            id = getNextId();
            s.setId(id);
        }
        rooms.put(id, s);
        return s;
    }

    @Override
    public <S extends Room> List<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Room> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<Room> iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Room getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends Room> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Room> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Room> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Room> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Room> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Room> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public boolean isRoomFull(Long roomId) {
        RoomDto roomDto = rooms.get(roomId).dto();
        return roomDto.getUsersInRoom() >= roomDto.getSlots();
    }

    @Override
    public Page<Room> findSearchedPageableRooms(String searchValue, Pageable page) {
        List<Room> roomList = new ArrayList<>(rooms.values());
        roomList.removeIf(room -> !room.getRoomName().contains(searchValue));
        return new PageImpl<>(roomList, page, roomList.size());
    }

    @Override
    public List<Room> searchRooms(String searchValue, Pageable page) {
        List<Room> roomList = new ArrayList<>(rooms.values());
        roomList.removeIf(room -> !room.getRoomName().contains(searchValue) || room.isComplete());

        return roomList;
    }

    @Override
    public Page<Room> findUserRooms(User user, Pageable page) {
        List<Room> roomList = rooms.values().stream()
                .filter(room -> room.getUser().equals(user))
                .collect(Collectors.toList());

        return new PageImpl<>(roomList, page, roomList.size());
    }

    @Override
    public Page<Room> findSearchedPageableUserRooms(String searchValue, User user, Pageable page) {
        List<Room> roomList = rooms.values().stream()
                .filter(room -> room.getUser().equals(user) && room.getRoomName().contains(searchValue))
                .collect(Collectors.toList());

        return new PageImpl<>(roomList, page, roomList.size());
    }

    @Override
    public Optional<Long> findRoomIdWithPlayerIn(User user) {
        Long roomId = rooms.values().stream()
                .filter(room -> room.getUser().equals(user))
                .findFirst()
                .get()
                .getId();

        return Optional.of(roomId);
    }

    @Override
    public boolean existsByRoomName(String roomName) {
        return rooms.values().stream()
                .anyMatch(room -> room.getRoomName().equals(roomName));
    }

    @Override
    public Page<Room> findAllInComplete(Pageable page) {
        List<Room> roomList = rooms.values().stream()
                .filter(room -> !room.isComplete())
                .collect(Collectors.toList());
        return new PageImpl<>(roomList, page, roomList.size());
    }

    private Long getNextId() {
        return nextId++;
    }
}
