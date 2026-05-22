package cl.hitzone.fullstack.Repository;

import cl.hitzone.fullstack.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Te dejo este método listo porque lo vas a necesitar más adelante
    // para el login y la seguridad JWT
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT r.nameRol, COUNT(u) FROM User u JOIN u.roles r GROUP BY r.nameRol")
    List<Object[]> countUsersByRole();

    List<User> findByUsernameContainingIgnoreCase(String username);
}

