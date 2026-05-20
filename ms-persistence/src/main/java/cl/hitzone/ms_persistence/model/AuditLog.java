package cl.hitzone.ms_persistence.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ENTIDAD - AuditLog
 *
 * Mapea la tabla audit_log de la base de datos.
 * Registra eventos de auditoría generados por cualquier microservicio.
 *
 * COLUMNAS:
 *  id          → BIGSERIAL PRIMARY KEY       (autoincremental)
 *  serviceName → VARCHAR(100) NOT NULL       (microservicio que genera el evento)
 *  action      → VARCHAR(100) NOT NULL       (acción realizada: CREATE, UPDATE, DELETE...)
 *  entityType  → VARCHAR(100)                (entidad afectada: Map, Agent, Weapon...)
 *  entityId    → BIGINT                      (ID del objeto afectado)
 *  username    → VARCHAR(100)                (usuario que ejecutó la acción)
 *  details     → TEXT                        (información adicional libre)
 *  status      → VARCHAR(20) DEFAULT SUCCESS (SUCCESS o ERROR)
 *  createdAt   → TIMESTAMP DEFAULT NOW()     (fecha y hora del evento)
 */
@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Microservicio que disparó el evento (ej: "ms-maps", "ms-weapons")
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    // Acción realizada (ej: "CREATE_MAP", "DELETE_AGENT", "LOGIN")
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    // Tipo de entidad afectada (ej: "Map", "Agent", "Weapon") — puede ser null
    @Column(name = "entity_type", length = 100)
    private String entityType;

    // ID del objeto afectado — puede ser null (ej. en eventos de login)
    @Column(name = "entity_id")
    private Long entityId;

    // Usuario que ejecutó la acción — puede ser null (ej. eventos del sistema)
    @Column(name = "username", length = 100)
    private String username;

    // Información adicional libre (ej: JSON con datos previos y nuevos)
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    // Estado del evento: SUCCESS (por defecto) o ERROR
    @Column(name = "status", length = 20)
    private String status = "SUCCESS";

    // Fecha y hora exacta del evento — se asigna automáticamente al crear
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Asigna automáticamente la fecha/hora ANTES de insertar en BD
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "SUCCESS";
        }
    }
}

