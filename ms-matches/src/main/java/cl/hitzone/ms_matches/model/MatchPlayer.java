package cl.hitzone.ms_matches.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa a un jugador dentro de una partida específica.
 * Relación N:1 con Match (muchos jugadores pertenecen a una partida).
 * Cada fila es una "línea de estadísticas" de un jugador en ese match.
 */
@Entity
@Table(name = "match_players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia a la partida dueña de esta fila.
     * FetchType.LAZY: la partida NO se carga en BD hasta que se acceda explícitamente,
     * evitando N+1 queries innecesarias.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;           // FK → matches.id

    @Column(nullable = false)
    private String username;       // nombre del jugador en el juego

    private String agentName;      // agente usado: "Jett", "Sage", "Omen"...

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer kills;         // eliminaciones

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer deaths;        // muertes

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer assists;       // asistencias

    private Integer rrChange;      // RR ganados o perdidos (+20, -15...)
}
