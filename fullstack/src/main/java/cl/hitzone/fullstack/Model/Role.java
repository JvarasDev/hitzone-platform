package cl.hitzone.fullstack.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles") // tabla propia de roles (no la join table)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_rol", unique = true, nullable = false)
    private String nameRol; // camelCase — Lombok generará getNameRol()

    @Column(name = "description")
    private String description;
}
