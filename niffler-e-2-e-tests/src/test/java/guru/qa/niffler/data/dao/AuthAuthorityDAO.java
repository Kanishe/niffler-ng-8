package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userAuth.AuthorityEntity;

public interface AuthAuthorityDAO {

    void createUser(AuthorityEntity... user);
}
