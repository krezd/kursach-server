package com.example.kursachserver.repository;

import com.example.kursachserver.enumModel.Severity;
import com.example.kursachserver.enumModel.ViolationType;
import com.example.kursachserver.model.User;
import com.example.kursachserver.model.Violation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long>, JpaSpecificationExecutor<Violation> {
    List<Violation> findByUser(User user);

    @Query("SELECT v FROM Violation v " +
            "WHERE (:userId IS NULL OR v.user.id = :userId) " +
            "AND (:type IS NULL OR v.type = :type) " +
            "AND (:severity IS NULL OR v.severity = :severity) " +
            "AND (v.occurredAt >= :from AND v.occurredAt <= :to)")
    List<Violation> findAllFiltered(
            @Param("userId") Long userId,
            @Param("type") ViolationType type, // <-- тут enum
            @Param("severity") Severity severity, // <-- тут enum
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );

    @Query("""
        SELECT v.user.id   AS userId,
               v.severity  AS severity,
               COUNT(v)    AS cnt
        FROM Violation v
        WHERE v.occurredAt BETWEEN :from AND :to
        GROUP BY v.user.id, v.severity
        """)
    List<UserSeverityCount> countByUserAndSeverity(
            @Param("from") OffsetDateTime from,
            @Param("to")   OffsetDateTime to
    );

    interface UserSeverityCount {
        Long getUserId();
        Severity getSeverity();
        Long getCnt();
    }

}

