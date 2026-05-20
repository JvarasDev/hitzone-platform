package cl.hitzone.ms_weapons.dto;

import cl.hitzone.ms_weapons.model.Weapon;
import cl.hitzone.ms_weapons.model.WeaponCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeaponResponseDTO {

    private Long id;
    private String name;
    private WeaponCategory category;
    private BigDecimal damage;
    private BigDecimal fireRate;
    private Integer magazineSize;
    private Integer price;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor de mapeo desde entidad
    public WeaponResponseDTO(Weapon weapon) {
        this.id           = weapon.getId();
        this.name         = weapon.getName();
        this.category     = weapon.getCategory();
        this.damage       = weapon.getDamage();
        this.fireRate     = weapon.getFireRate();
        this.magazineSize = weapon.getMagazineSize();
        this.price        = weapon.getPrice();
        this.description  = weapon.getDescription();
        this.createdAt    = weapon.getCreatedAt();
        this.updatedAt    = weapon.getUpdatedAt();
    }
}
