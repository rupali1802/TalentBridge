package com.talentbridge.repository;

import com.talentbridge.entity.Application;
import com.talentbridge.entity.Job;
import com.talentbridge.entity.User;
import com.talentbridge.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByUserOrderByAppliedAtDesc(User user);

    List<Application> findByJobOrderByAppliedAtDesc(Job job);

    Optional<Application> findByUserAndJob(User user, Job job);

    boolean existsByUserAndJob(User user, Job job);

    long countByStatus(ApplicationStatus status);

    long countByJob(Job job);

    long countByJobAndStatus(Job job, ApplicationStatus status);

    List<Application> findByJobEmployerOrderByAppliedAtDesc(User employer);
}
