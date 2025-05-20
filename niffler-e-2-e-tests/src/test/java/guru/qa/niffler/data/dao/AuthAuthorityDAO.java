package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;

import java.util.List;

public interface AuthAuthorityDAO {

    void createUser(AuthorityEntity... user);

    List<AuthorityEntity> findAll();
}
