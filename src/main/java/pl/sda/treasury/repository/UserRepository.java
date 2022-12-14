package pl.sda.treasury.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sda.treasury.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);


}
