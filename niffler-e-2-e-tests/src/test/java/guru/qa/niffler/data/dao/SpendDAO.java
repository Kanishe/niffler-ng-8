package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDAO {

    SpendEntity createSpend(SpendEntity spend);

    Optional<SpendEntity> findSpendById(UUID id);

    List<SpendEntity> findAllByUsername(String username);

    void deleteSpend(SpendEntity spend);

    Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description);

    List<SpendEntity> findAll();

    SpendEntity update(SpendEntity spend);

}
