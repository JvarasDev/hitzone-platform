package cl.hitzone.ms_rank.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Table(
        name = "player_ranks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "rank_name", nullable = false, length = 50)
    private String rankName;

    @Column(name = "rank_number", nullable = false)
    private Integer rankNumber;

    @Column(name = "rr_points", nullable = false)
    private Integer rrPoints;

    @Column(nullable = false)
    private Integer wins;

    @Column(nullable = false)
    private Integer losses;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
