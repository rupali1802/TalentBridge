package com.talentbridge.service;

import com.talentbridge.dto.JobDTO;
import com.talentbridge.entity.Job;
import com.talentbridge.entity.User;
import com.talentbridge.enums.JobType;
import com.talentbridge.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final com.talentbridge.repository.SavedJobRepository savedJobRepository;
    private final ActivityLogService activityLogService;

    public JobService(JobRepository jobRepository, com.talentbridge.repository.SavedJobRepository savedJobRepository, ActivityLogService activityLogService) {
        this.jobRepository = jobRepository;
        this.savedJobRepository = savedJobRepository;
        this.activityLogService = activityLogService;
    }

    public void saveJob(User user, Job job) {
        if (isJobSaved(user, job)) return;
        com.talentbridge.entity.SavedJob savedJob = new com.talentbridge.entity.SavedJob(user, job);
        savedJobRepository.save(savedJob);
    }

    public void unsaveJob(User user, Job job) {
        savedJobRepository.findByUserAndJob(user, job).ifPresent(savedJobRepository::delete);
    }

    public boolean isJobSaved(User user, Job job) {
        return savedJobRepository.findByUserAndJob(user, job).isPresent();
    }

    public List<com.talentbridge.entity.SavedJob> getSavedJobs(User user) {
        return savedJobRepository.findByUserOrderBySavedAtDesc(user);
    }

    public Job createJob(JobDTO dto, User employer) {
        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setSkillsRequired(dto.getSkillsRequired());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setJobType(dto.getJobType());
        job.setApplicationDeadline(dto.getApplicationDeadline());
        job.setEmployer(employer);
        job.setActive(true);

        Job savedJob = jobRepository.save(job);

        activityLogService.log("JOB_POSTED",
                "New job posted: " + dto.getTitle(), employer);

        return savedJob;
    }

    public Job updateJob(Long jobId, JobDTO dto, User employer) {
        Job job = findById(jobId);

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized to edit this job");
        }

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setSkillsRequired(dto.getSkillsRequired());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setJobType(dto.getJobType());
        job.setApplicationDeadline(dto.getApplicationDeadline());

        activityLogService.log("JOB_UPDATED",
                "Job updated: " + dto.getTitle(), employer);

        return jobRepository.save(job);
    }

    public void deleteJob(Long jobId, User employer) {
        Job job = findById(jobId);

        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized to delete this job");
        }

        activityLogService.log("JOB_DELETED",
                "Job deleted: " + job.getTitle(), employer);

        jobRepository.delete(job);
    }

    public void adminDeleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public List<Job> findActiveJobs() {
        return jobRepository.findByActiveTrueOrderByPostedAtDesc();
    }

    public List<Job> findEmployerJobs(User employer) {
        return jobRepository.findByEmployerOrderByPostedAtDesc(employer);
    }

    public List<Job> searchJobs(String keyword, String location, JobType jobType,
                                String experienceLevel, Double salaryMin, Double salaryMax) {
        return jobRepository.searchJobs(keyword, location, jobType, experienceLevel, salaryMin, salaryMax);
    }

    public long countActiveJobs() {
        return jobRepository.countByActiveTrue();
    }

    public long countAllJobs() {
        return jobRepository.count();
    }

    public List<String> getDistinctLocations() {
        return jobRepository.findDistinctLocations();
    }

    public List<Job> findAllJobs() {
        return jobRepository.findAll();
    }

    public void toggleJobStatus(Long jobId) {
        Job job = findById(jobId);
        job.setActive(!job.isActive());
        jobRepository.save(job);
    }

    public JobDTO convertToDTO(Job job) {
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setSkillsRequired(job.getSkillsRequired());
        dto.setLocation(job.getLocation());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setJobType(job.getJobType());
        dto.setApplicationDeadline(job.getApplicationDeadline());
        return dto;
    }
}
