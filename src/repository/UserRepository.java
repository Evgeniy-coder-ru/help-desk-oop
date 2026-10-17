package repository;

import model.User;

import java.util.List;
import java.util.Optional;

/**
 * Контракт доступа к пользователям.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();
}
