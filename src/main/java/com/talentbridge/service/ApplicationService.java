package com.talentbridge.service;

import com.talentbridge.entity.Application;
import com.talentbridge.entity.Job;
import com.talentbridge.entity.User;
import com.talentbridge.enums.ApplicationStatus;
import com.talentbridge.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              NotificationService notificationService,
                              ActivityLogService activityLogService) {
        this.applicationRepository = applicationRepository;
        this.notificationService = notificationService;
        this.activityLogService = activityLogService;
    }

    public Application applyForJob(User user, Job job) {
        if (applicationRepository.existsByUserAndJob(user, job)) {
            throw new RuntimeException("You have already applied for this job");
        }

        Application application = new Application(user, job);
        Application saved = applicationRepository.save(application);

        notificationService.createNotification(
                "Application Submitted",
                "Your application for '" + job.getTitle() + "' has been submitted successfully.",
                user
        );

        notificationService.createNotification(
                "New Applicant",
                user.getFullName() + " applied for '" + job.getTitle() + "'.",
                job.getEmployer()
        );

        activityLogService.log("APPLICATION_SUBMITTED",
                "Applied for job: " + job.getTitle(), user);

        return saved;
    }

    public Application updateStatus(Long applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(newStatus);

        switch (newStatus) {
            case UNDER_REVIEW:
                application.setReviewedAt(LocalDateTime.now());
                break;
            case SHORTLISTED:
                application.setShortlistedAt(LocalDateTime.now());
                notificationService.createNotification(
                        "Congratulations! You've been Shortlisted",
                        "Your application for '" + application.getJob().getTitle() + "' has been shortlisted!",
                        application.getUser()
                );
                break;
            case REJECTED:
                application.setRejectedAt(LocalDateTime.now());
                notificationService.createNotification(
                        "Application Update",
                        "Your application for '" + application.getJob().getTitle() + "' was not selected at this time.",
                        application.getUser()
                );
                break;
            default:
                break;
        }

        activityLogService.log("STATUS_UPDATED",
                "Application status updated to " + newStatus.getDisplayName() + " for job: " + application.getJob().getTitle(),
                application.getJob().getEmployer());

        return applicationRepository.save(application);
    }

    public List<Application> getUserApplications(User user) {
        return applicationRepository.findByUserOrderByAppliedAtDesc(user);
    }

    public List<Application> getJobApplications(Job job) {
        return applicationRepository.findByJobOrderByAppliedAtDesc(job);
    }

    public boolean hasApplied(User user, Job job) {
        return applicationRepository.existsByUserAndJob(user, job);
    }

    public long countAll() {
        return applicationRepository.count();
    }

    public long countByStatus(ApplicationStatus status) {
        return applicationRepository.countByStatus(status);
    }

    public long countByJob(Job job) {
        return applicationRepository.countByJob(job);
    }

    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    public List<Application> getEmployerApplications(User employer) {
        return applicationRepository.findByJobEmployerOrderByAppliedAtDesc(employer);
    }
}
