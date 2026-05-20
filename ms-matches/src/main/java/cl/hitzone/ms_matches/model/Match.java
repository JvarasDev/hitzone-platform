package cl.hitzone.ms_matches.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // número interno de BD

    @Column(unique = true, nullable = false)
    private String matchId; // ID único del servidor de juego

    private String mapName; // nombre del mapa: "Ascent", "Bind"...

    @Column(nullable = false)
    private String gameMode; // COMPETITIVE, UNRATED...

    @Column(nullable = false)
    private String result; // WIN / LOSS / DRAW

    private Integer scoreTeam; // rondas ganadas por el equipo
    private Integer scoreEnemy; // rondas ganadas por el enemigo
    @Column(name = "duration_s")
    private Integer durationS; // duración en segundos

    @Column(updatable = false)
    private LocalDateTime playedAt; // cuándo se jugó

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatchPlayer> players; // lista de jugadores

    @PrePersist
    public void prePersist() {
        if (playedAt == null)
            playedAt = LocalDateTime.now();
    }
}
