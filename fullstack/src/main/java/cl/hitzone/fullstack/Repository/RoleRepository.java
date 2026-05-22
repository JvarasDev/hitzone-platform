package cl.hitzone.fullstack.Repository;

import cl.hitzone.fullstack.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca un rol por su nombre (ej: "ROLE_USER", "ROLE_ADMIN").
     * Se usa en AuthService al registrar un nuevo usuario
     * para asignarle el rol por defecto desde la BD.
     */
    Optional<Role> findByNameRol(String nameRol);
}
