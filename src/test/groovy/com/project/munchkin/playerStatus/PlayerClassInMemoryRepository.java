package groovy.com.project.munchkin.playerStatus;

import com.project.munchkin.playerStatus.model.PlayerClass;
import com.project.munchkin.playerStatus.repository.PlayerClassRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

public class PlayerClassInMemoryRepository implements PlayerClassRepository {

    private final Map<Long, PlayerClass> playerClasses = new HashMap<>();
    private Long nextId = 1L;

    private Long getNextId() {
        return nextId++;
    }

    @Override
    public List<PlayerClass> findAll() {
        return new ArrayList<>(playerClasses.values());
    }

    @Override
    public List<PlayerClass> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<PlayerClass> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<PlayerClass> findAllById(Iterable<Long> iterable) {
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
    public void delete(PlayerClass playerClass) {

    }

    @Override
    public void deleteAll(Iterable<? extends PlayerClass> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends PlayerClass> S save(S s) {
        Long id = s.getId();
        if (id == null) {
            id = getNextId();
            s.setId(id);
        }
        playerClasses.put(id, s);
        return s;
    }

    @Override
    public <S extends PlayerClass> List<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<PlayerClass> findById(Long aLong) {
        return Optional.ofNullable(playerClasses.get(aLong));
    }

    @Override
    public boolean existsById(Long aLong) {
        return playerClasses.containsKey(aLong);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends PlayerClass> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<PlayerClass> iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public PlayerClass getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends PlayerClass> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends PlayerClass> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends PlayerClass> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends PlayerClass> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends PlayerClass> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends PlayerClass> boolean exists(Example<S> example) {
        return false;
    }
}
