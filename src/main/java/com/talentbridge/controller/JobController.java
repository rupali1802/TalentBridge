package com.talentbridge.controller;

import com.talentbridge.entity.Job;
import com.talentbridge.enums.JobType;
import com.talentbridge.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public String browseJobs(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) JobType jobType,
                             @RequestParam(required = false) String experienceLevel,
                             @RequestParam(required = false) Double salaryMin,
                             @RequestParam(required = false) Double salaryMax,
                             Model model) {
        List<Job> jobs;
        boolean hasFilters = keyword != null || location != null || jobType != null
                || experienceLevel != null || salaryMin != null || salaryMax != null;

        if (hasFilters) {
            jobs = jobService.searchJobs(keyword, location, jobType, experienceLevel, salaryMin, salaryMax);
        } else {
            jobs = jobService.findActiveJobs();
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("jobType", jobType);
        model.addAttribute("experienceLevel", experienceLevel);
        model.addAttribute("salaryMin", salaryMin);
        model.addAttribute("salaryMax", salaryMax);
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("locations", jobService.getDistinctLocations());
        model.addAttribute("totalResults", jobs.size());

        return "student/browse-jobs";
    }

    @GetMapping("/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        Job job = jobService.findById(id);
        model.addAttribute("job", job);
        return "student/job-detail";
    }
}
