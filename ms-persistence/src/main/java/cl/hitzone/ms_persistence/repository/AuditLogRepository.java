package cl.hitzone.ms_persistence.repository;

import cl.hitzone.ms_persistence.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // SELECT * FROM audit_log WHERE service_name = :serviceName
    List<AuditLog> findByServiceName(String serviceName);

    // SELECT * FROM audit_log WHERE username = :username
    List<AuditLog> findByUsername(String username);
}

