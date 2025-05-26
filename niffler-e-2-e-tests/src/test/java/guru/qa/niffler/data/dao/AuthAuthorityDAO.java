package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.List;

public interface AuthAuthorityDAO {

    void create(AuthorityEntity... user);

    List<AuthorityEntity> findAll();
}
