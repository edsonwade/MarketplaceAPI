package code.vanilson.startup.repository;

import code.vanilson.startup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u.email FROM User u")
    Optional<User> findUsersByEmail(String email);
}
