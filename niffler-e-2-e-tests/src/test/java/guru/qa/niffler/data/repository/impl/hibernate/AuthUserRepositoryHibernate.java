package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager em = em(CFG.authJdbcUrl());

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        em.joinTransaction();
        em.persist(user);
        return user;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        em.joinTransaction();
        em.merge(user);
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                em.find(AuthUserEntity.class, id)
        );
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.of(em.createQuery(
                            "select u from UserEntity u where u.username =:username", AuthUserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(AuthUserEntity user) {
        em.joinTransaction();
        em.remove(user);
    }
}
