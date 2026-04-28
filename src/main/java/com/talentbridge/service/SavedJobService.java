package com.talentbridge.service;

import com.talentbridge.entity.Job;
import com.talentbridge.entity.SavedJob;
import com.talentbridge.entity.User;
import com.talentbridge.repository.SavedJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;

    public SavedJobService(SavedJobRepository savedJobRepository) {
        this.savedJobRepository = savedJobRepository;
    }

    public SavedJob saveJob(User user, Job job) {
        if (savedJobRepository.existsByUserAndJob(user, job)) {
            throw new RuntimeException("Job already saved");
        }
        return savedJobRepository.save(new SavedJob(user, job));
    }

    public void unsaveJob(User user, Job job) {
        savedJobRepository.deleteByUserAndJob(user, job);
    }

    public boolean isJobSaved(User user, Job job) {
        return savedJobRepository.existsByUserAndJob(user, job);
    }

    public List<SavedJob> getSavedJobs(User user) {
        return savedJobRepository.findByUserOrderBySavedAtDesc(user);
    }

    public void toggleSaveJob(User user, Job job) {
        if (savedJobRepository.existsByUserAndJob(user, job)) {
            savedJobRepository.deleteByUserAndJob(user, job);
        } else {
            savedJobRepository.save(new SavedJob(user, job));
        }
    }
}
