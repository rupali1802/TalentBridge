package com.talentbridge.controller;

import com.talentbridge.dto.JobDTO;
import com.talentbridge.entity.User;
import com.talentbridge.enums.JobType;
import com.talentbridge.enums.ApplicationStatus;
import com.talentbridge.enums.Role;
import com.talentbridge.service.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    public AdminController(UserService userService, JobService jobService,
                           ApplicationService applicationService,
                           ActivityLogService activityLogService,
                           NotificationService notificationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("totalUsers", userService.countAll());
        model.addAttribute("totalStudents", userService.countByRole(Role.STUDENT));
        model.addAttribute("totalEmployers", userService.countByRole(Role.EMPLOYER));
        model.addAttribute("totalJobs", jobService.countAllJobs());
        model.addAttribute("activeJobs", jobService.countActiveJobs());
        model.addAttribute("totalApplications", applicationService.countAll());
        model.addAttribute("shortlistedCount", applicationService.countByStatus(ApplicationStatus.SHORTLISTED));
        model.addAttribute("recentActivity", activityLogService.getRecentActivity());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "admin/dashboard";
    }

    @GetMapping("/manage-users")
    public String manageUsers(@RequestParam(required = false) String search,
                              @RequestParam(required = false) Role role,
                              Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        if (search != null && !search.isBlank()) {
            model.addAttribute("users", userService.searchUsers(search));
        } else if (role != null) {
            model.addAttribute("users", userService.findByRole(role));
        } else {
            model.addAttribute("users", userService.findAllUsers());
        }
        model.addAttribute("search", search);
        model.addAttribute("selectedRole", role);
        model.addAttribute("roles", Role.values());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "admin/manage-users";
    }

    @PostMapping("/toggle-user/{id}")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "User status updated!");
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        return "redirect:/admin/manage-users";
    }

    @GetMapping("/manage-jobs")
    public String manageJobs(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("jobs", jobService.findAllJobs());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "admin/manage-jobs";
    }

    @PostMapping("/toggle-job/{id}")
    public String toggleJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        jobService.toggleJobStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Job status updated!");
        return "redirect:/admin/manage-jobs";
    }

    @PostMapping("/delete-job/{id}")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        jobService.adminDeleteJob(id);
        redirectAttributes.addFlashAttribute("successMessage", "Job deleted!");
        return "redirect:/admin/manage-jobs";
    }

    @GetMapping("/post-job")
    public String postJobPage(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("jobDTO", new JobDTO());
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("employers", userService.findByRole(Role.EMPLOYER));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "admin/post-job";
    }

    @PostMapping("/post-job")
    public String postJob(@Valid @ModelAttribute("jobDTO") JobDTO jobDTO, BindingResult result,
                          Principal principal, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        if (result.hasErrors() || jobDTO.getEmployerId() == null) {
            if (jobDTO.getEmployerId() == null) {
                model.addAttribute("employerError", "Please select an employer");
            }
            model.addAttribute("user", user);
            model.addAttribute("jobTypes", JobType.values());
            model.addAttribute("employers", userService.findByRole(Role.EMPLOYER));
            model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
            return "admin/post-job";
        }
        
        User selectedEmployer = userService.findById(jobDTO.getEmployerId());
        jobService.createJob(jobDTO, selectedEmployer);
        
        redirectAttributes.addFlashAttribute("successMessage", "New job posted successfully on behalf of " + selectedEmployer.getCompany() + "!");
        return "redirect:/admin/manage-jobs";
    }

    @GetMapping("/activity-log")
    public String activityLog(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("activities", activityLogService.getRecentActivity());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "admin/activity-log";
    }
}
