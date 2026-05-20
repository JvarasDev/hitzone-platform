package cl.hitzone.ms_weapons.repository;

import cl.hitzone.ms_weapons.model.Weapon;
import cl.hitzone.ms_weapons.model.WeaponCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeaponRepository extends JpaRepository<Weapon,Long> {

    // esto es como hacer una query (SELECT * FROM weapons WHERE category = ?)
    List<Weapon> findByCategory(WeaponCategory category);

    // Busca un arma exactamente por su nombre. Retorna Optional para manejar de forma segura si no se encuentra.
    Optional<Weapon> findByName(String name);
    
    // Verifica si ya existe un arma con el nombre especificado en la base de datos.
    // Retorna true o false. Es muy útil para validaciones previas a crear una nueva arma (POST).
    boolean existsByName(String name);

    // Verifica si existe OTRA arma con el mismo nombre, ignorando un ID específico.
    // Útil para validaciones al actualizar (PUT), para asegurar de que no le pongas a esta arma
    // un nombre que ya le pertenece a otra arma distinta, sin que el propio nombre lance falso positivo.
    boolean existsByNameAndIdNot(String name, Long id);

    // Consulta personalizada usando JPQL (Java Persistence Query Language).
    // Busca y retorna una lista de armas cuyo precio se encuentre dentro del rango proporcionado (inclusive).
    @Query("SELECT w FROM Weapon w WHERE w.price >= :min AND w.price <= :max")
    List<Weapon> findByPriceRange(@Param("min") Integer min, @Param("max") Integer max);

    @Query("SELECT w.category AS category, COUNT(w) AS count " +
           "FROM Weapon w GROUP BY w.category ORDER BY COUNT(w) DESC")
    List<Object[]> countByCategory();

}
