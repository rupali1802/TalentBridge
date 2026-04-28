package com.talentbridge.controller;

import com.talentbridge.dto.JobDTO;
import com.talentbridge.dto.PasswordDTO;
import com.talentbridge.entity.Application;
import com.talentbridge.entity.Job;
import com.talentbridge.entity.User;
import com.talentbridge.enums.ApplicationStatus;
import com.talentbridge.enums.JobType;
import com.talentbridge.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;

    public EmployerController(UserService userService, JobService jobService,
                              ApplicationService applicationService,
                              NotificationService notificationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        List<Job> jobs = jobService.findEmployerJobs(user);
        List<Application> allApps = applicationService.getEmployerApplications(user);
        long shortlisted = allApps.stream().filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED).count();
        long pending = allApps.stream().filter(a -> a.getStatus() == ApplicationStatus.APPLIED).count();

        model.addAttribute("user", user);
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("totalApplications", allApps.size());
        model.addAttribute("shortlistedCount", shortlisted);
        model.addAttribute("pendingCount", pending);
        model.addAttribute("recentJobs", jobs.stream().limit(5).toList());
        model.addAttribute("recentApplications", allApps.stream().limit(5).toList());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "employer/dashboard";
    }

    @GetMapping("/post-job")
    public String postJobPage(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("jobDTO", new JobDTO());
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("user", user);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "employer/post-job";
    }

    @PostMapping("/post-job")
    public String postJob(@Valid @ModelAttribute("jobDTO") JobDTO jobDTO, BindingResult result,
                          Principal principal, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        if (result.hasErrors()) {
            model.addAttribute("jobTypes", JobType.values());
            model.addAttribute("user", user);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
            return "employer/post-job";
        }
        jobService.createJob(jobDTO, user);
        redirectAttributes.addFlashAttribute("successMessage", "Job posted successfully!");
        return "redirect:/employer/my-jobs";
    }

    @GetMapping("/edit-job/{id}")
    public String editJobPage(@PathVariable Long id, Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        Job job = jobService.findById(id);
        if (!job.getEmployer().getId().equals(user.getId())) {
            return "redirect:/employer/my-jobs";
        }
        model.addAttribute("jobDTO", jobService.convertToDTO(job));
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("user", user);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "employer/edit-job";
    }

    @PostMapping("/edit-job/{id}")
    public String editJob(@PathVariable Long id, @Valid @ModelAttribute("jobDTO") JobDTO jobDTO,
                          BindingResult result, Principal principal, Model model,
                          RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        if (result.hasErrors()) {
            model.addAttribute("jobTypes", JobType.values());
            model.addAttribute("user", user);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
            return "employer/edit-job";
        }
        jobService.updateJob(id, jobDTO, user);
        redirectAttributes.addFlashAttribute("successMessage", "Job updated successfully!");
        return "redirect:/employer/my-jobs";
    }

    @PostMapping("/delete-job/{id}")
    public String deleteJob(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        jobService.deleteJob(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "Job deleted successfully!");
        return "redirect:/employer/my-jobs";
    }

    @GetMapping("/my-jobs")
    public String myJobs(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("jobs", jobService.findEmployerJobs(user));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "employer/my-jobs";
    }

    @GetMapping("/applicants/{jobId}")
    public String viewApplicants(@PathVariable Long jobId, Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        Job job = jobService.findById(jobId);
        if (!job.getEmployer().getId().equals(user.getId())) {
            return "redirect:/employer/my-jobs";
        }
        model.addAttribute("user", user);
        model.addAttribute("job", job);
        model.addAttribute("applications", applicationService.getJobApplications(job));
        model.addAttribute("statuses", ApplicationStatus.values());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "employer/applicants";
    }

    @PostMapping("/update-status/{applicationId}")
    public String updateStatus(@PathVariable Long applicationId,
                               @RequestParam("status") ApplicationStatus status,
                               RedirectAttributes redirectAttributes) {
        Application app = applicationService.updateStatus(applicationId, status);
        redirectAttributes.addFlashAttribute("successMessage", "Application status updated!");
        return "redirect:/employer/applicants/" + app.getJob().getId();
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("passwordDTO", new PasswordDTO());
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "employer/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO,
                                 BindingResult result, Principal principal, Model model,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
            return "employer/change-password";
        }
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "New passwords do not match.");
            model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
            return "employer/change-password";
        }
        try {
            userService.changePassword(principal.getName(), passwordDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/employer/change-password";
    }
}
