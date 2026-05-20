package cl.hitzone.ms_maps.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "maps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class modelMaps {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private typeDifficulty difficulty;
    private typeMaps type;
    private String description;
    private Integer totalSite;
    private boolean active;

}
