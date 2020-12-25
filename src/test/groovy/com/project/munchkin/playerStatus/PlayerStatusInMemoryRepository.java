package groovy.com.project.munchkin.playerStatus;

import com.project.munchkin.playerStatus.model.PlayerStatus;
import com.project.munchkin.playerStatus.repository.PlayerStatusRepository;
import com.project.munchkin.user.model.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerStatusInMemoryRepository implements PlayerStatusRepository {

    private final Map<Long, PlayerStatus> playerStatuses = new HashMap<>();
    private Long nextId = 1L;

    private Long getNextId() {
        return nextId++;
    }

    @Override
    public Optional<PlayerStatus> findByRoomIdAndUserId(Long roomId, User user) {
        Optional<PlayerStatus> playerStatus = playerStatuses.values().stream()
                .filter((item) -> item.getRoomId() == roomId && item.getUser().getId() == user.getId())
                .findFirst();

        return playerStatus;
    }

    @Override
    public boolean playerIsInAnyRoom(User user) {
        List<PlayerStatus> oneUserPlayerStatuses = playerStatuses.values().stream()
                .filter((item) -> item.getUser().getId() == user.getId())
                .collect(Collectors.toList());
        if(!oneUserPlayerStatuses.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public List<PlayerStatus> findAllPlayerStatusByRoomId(Long roomId) {
        List<PlayerStatus> playerStatusesInRoom = this.playerStatuses.values().stream()
                .filter(playerStatus -> playerStatus.getRoomId() == roomId)
                .collect(Collectors.toList());
        return playerStatusesInRoom;
    }

    @Override
    public List<PlayerStatus> findAllSortedPlayerStatusByRoomId(Long roomId) {
        return this.playerStatuses.values().stream()
                .filter(playerStatus -> playerStatus.getRoomId() == roomId)
                .sorted(Comparator.comparingLong(PlayerStatus::getPlayerLevel))
                .collect(Collectors.toList());
    }

    @Override
    public List<PlayerStatus> findAll() {
        return null;
    }

    @Override
    public List<PlayerStatus> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<PlayerStatus> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<PlayerStatus> findAllById(Iterable<Long> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        playerStatuses.remove(aLong);
    }

    @Override
    public void delete(PlayerStatus playerStatus) {

    }

    @Override
    public void deleteAll(Iterable<? extends PlayerStatus> iterable) {
        iterable.forEach((playerStatus) -> playerStatuses.remove(playerStatus.getId()));
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends PlayerStatus> S save(S s) {
        Long id = s.getId();
        if (id == null) {
            id = getNextId();
            s.setId(id);
        }
        playerStatuses.put(id, s);
        return s;
    }

    @Override
   public <S extends PlayerStatus> List<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<PlayerStatus> findById(Long playerStatusId) {
        return Optional.ofNullable(playerStatuses.get(playerStatusId));
    }

    @Override
    public boolean existsById(Long aLong) {
        return playerStatuses.containsKey(aLong);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends PlayerStatus> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<PlayerStatus> iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public PlayerStatus getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends PlayerStatus> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends PlayerStatus> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends PlayerStatus> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends PlayerStatus> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends PlayerStatus> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends PlayerStatus> boolean exists(Example<S> example) {
        return false;
    }
}
