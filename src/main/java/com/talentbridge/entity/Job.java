package com.talentbridge.entity;

import com.talentbridge.enums.JobType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Job description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String skillsRequired;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    private Double salaryMin;

    private Double salaryMax;

    @Column(length = 50)
    private String experienceLevel;

    @NotNull(message = "Job type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    private LocalDate applicationDeadline;

    @Column(nullable = false, updatable = false)
    private LocalDateTime postedAt;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SavedJob> savedJobs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.postedAt = LocalDateTime.now();
    }

    public Job() {}

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSkillsRequired() { return skillsRequired; }
    public void setSkillsRequired(String skillsRequired) { this.skillsRequired = skillsRequired; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Double salaryMin) { this.salaryMin = salaryMin; }

    public Double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Double salaryMax) { this.salaryMax = salaryMax; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDate applicationDeadline) { this.applicationDeadline = applicationDeadline; }

    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public User getEmployer() { return employer; }
    public void setEmployer(User employer) { this.employer = employer; }

    public List<Application> getApplications() { return applications; }
    public void setApplications(List<Application> applications) { this.applications = applications; }

    public List<SavedJob> getSavedJobs() { return savedJobs; }
    public void setSavedJobs(List<SavedJob> savedJobs) { this.savedJobs = savedJobs; }

    public String getSalaryDisplay() {
        if (salaryMin != null && salaryMax != null) {
            return String.format("₹%,.0f - ₹%,.0f", salaryMin, salaryMax);
        } else if (salaryMin != null) {
            return String.format("From ₹%,.0f", salaryMin);
        } else if (salaryMax != null) {
            return String.format("Up to ₹%,.0f", salaryMax);
        }
        return "Not specified";
    }
}
