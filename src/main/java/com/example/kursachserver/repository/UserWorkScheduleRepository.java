package com.example.kursachserver.repository;

import com.example.kursachserver.model.User;
import com.example.kursachserver.model.UserWorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWorkScheduleRepository extends JpaRepository<UserWorkSchedule, Long> {
    List<UserWorkSchedule> findByUser(User user);
    List<UserWorkSchedule> findByUserId(Long userId);
    Optional<UserWorkSchedule> findByUserIdAndDayOfWeek(Long userId, int dayOfWeek);
    void deleteByUserId(Long userId);
}
