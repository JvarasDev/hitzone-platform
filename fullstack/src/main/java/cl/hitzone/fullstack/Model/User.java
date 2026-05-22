package cl.hitzone.fullstack.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "users",
    indexes = {
        // Indice B-Tree en username - acelera findByUsername() usado en cada login
        @Index(name = "idx_users_username", columnList = "username"),
        // Indice B-Tree en email - acelera findByEmail() usado en cada registro
        @Index(name = "idx_users_email", columnList = "email")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // Relacion ManyToMany con Role - tabla intermedia "user_roles"
    // EAGER: los roles se cargan junto al usuario
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }
}
