package com.talentbridge.repository;

import com.talentbridge.entity.Job;
import com.talentbridge.entity.SavedJob;
import com.talentbridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    List<SavedJob> findByUserOrderBySavedAtDesc(User user);

    Optional<SavedJob> findByUserAndJob(User user, Job job);

    boolean existsByUserAndJob(User user, Job job);

    void deleteByUserAndJob(User user, Job job);
}
