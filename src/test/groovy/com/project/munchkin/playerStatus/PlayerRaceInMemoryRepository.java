package groovy.com.project.munchkin.playerStatus;

import com.project.munchkin.playerStatus.model.PlayerRace;
import com.project.munchkin.playerStatus.repository.PlayerRaceRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

public class PlayerRaceInMemoryRepository implements PlayerRaceRepository {

    private final Map<Long, PlayerRace> playerRaces = new HashMap<>();
    private Long nextId = 1L;

    private Long getNextId() {
        return nextId++;
    }

    @Override
    public List<PlayerRace> findAll() {
        return new ArrayList<>(playerRaces.values());
    }

    @Override
    public List<PlayerRace> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<PlayerRace> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<PlayerRace> findAllById(Iterable<Long> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(PlayerRace playerRace) {

    }

    @Override
    public void deleteAll(Iterable<? extends PlayerRace> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends PlayerRace> S save(S s) {
        Long id = s.getId();
        if (id == null) {
            id = getNextId();
            s.setId(id);
        }
        playerRaces.put(id, s);
        return s;
    }

    @Override
    public <S extends PlayerRace> List<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<PlayerRace> findById(Long aLong) {
        return Optional.ofNullable(playerRaces.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return playerRaces.containsKey(aLong);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends PlayerRace> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<PlayerRace> iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public PlayerRace getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends PlayerRace> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends PlayerRace> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends PlayerRace> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends PlayerRace> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends PlayerRace> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends PlayerRace> boolean exists(Example<S> example) {
        return false;
    }
}
