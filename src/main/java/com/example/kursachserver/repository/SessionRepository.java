package com.example.kursachserver.repository;

import com.example.kursachserver.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findAllByUserId(Long userId);

    @Query("SELECT s FROM Session s WHERE " +
            "(CAST(s.startSession AS DATE) = :date OR CAST(s.endSession AS DATE) = :date) " +
            "AND s.user.id = :userId")
    List<Session> findByDateAndUser(@Param("date") LocalDate date, @Param("userId") Long userId);

    @Query("SELECT s FROM Session s WHERE " +
            "(CAST(s.startSession AS DATE) = :date OR CAST(s.endSession AS DATE) = :date)")
    List<Session> findByDate(@Param("date") LocalDate date);


    List<Session> findByUser_NameContainingIgnoreCase(String name);

    List<Session> findByUser_PositionContainingIgnoreCase(String position);

    @Query(value = "SELECT s.* FROM session_table s " +
            "JOIN user_table u ON s.user_id = u.id " +
            "WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:position IS NULL OR LOWER(u.position) LIKE LOWER(CONCAT('%', :position, '%'))) " +
            "AND (:date IS NULL OR CAST(s.start_session AS DATE) = :date OR CAST(s.end_session AS DATE) = :date)",
            nativeQuery = true)
    List<Session> searchSessions(@Param("name") String name,
                                 @Param("position") String position,
                                 @Param("date") LocalDate date);

    boolean existsByUserId(Long id);

    void deleteAllByUserId(Long userId);
}
