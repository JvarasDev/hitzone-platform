package cl.hitzone.ms_weapons.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "weapon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weapon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private WeaponCategory  category;

    private BigDecimal damage;

    private BigDecimal fireRate;

    private Integer magazineSize;

    private Integer price;

    private String description;

    @CreationTimestamp
    @Column(updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;





}
