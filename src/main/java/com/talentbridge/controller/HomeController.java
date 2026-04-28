package com.talentbridge.controller;

import com.talentbridge.entity.Job;
import com.talentbridge.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final JobService jobService;
    private final com.talentbridge.service.UserService userService;

    public HomeController(JobService jobService, com.talentbridge.service.UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
    }

    @GetMapping({"/", "/home"})
    public String homePage(Model model) {
        List<Job> featuredJobs = jobService.findActiveJobs();
        int displayCount = Math.min(featuredJobs.size(), 6);
        model.addAttribute("featuredJobs", featuredJobs.subList(0, displayCount));
        model.addAttribute("totalJobs", jobService.countActiveJobs());
        model.addAttribute("totalCompanies", userService.countByRole(com.talentbridge.enums.Role.EMPLOYER));
        model.addAttribute("totalStudents", userService.countByRole(com.talentbridge.enums.Role.STUDENT));
        return "home";
    }
}
