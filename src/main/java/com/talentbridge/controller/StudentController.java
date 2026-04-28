package com.talentbridge.controller;

import com.talentbridge.dto.PasswordDTO;
import com.talentbridge.dto.ProfileDTO;
import com.talentbridge.entity.Application;
import com.talentbridge.entity.Job;
import com.talentbridge.entity.SavedJob;
import com.talentbridge.entity.User;
import com.talentbridge.enums.JobType;
import com.talentbridge.service.ApplicationService;
import com.talentbridge.service.JobService;
import com.talentbridge.service.NotificationService;
import com.talentbridge.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final NotificationService notificationService;

    public StudentController(UserService userService, JobService jobService,
                             ApplicationService applicationService, NotificationService notificationService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<Application> recentApps = applicationService.getUserApplications(user);
        
        model.addAttribute("user", user);
        model.addAttribute("recentApplications", recentApps.subList(0, Math.min(recentApps.size(), 5)));
        model.addAttribute("totalApplications", recentApps.size());
        model.addAttribute("savedJobsCount", jobService.getSavedJobs(user).size());
        model.addAttribute("shortlistedCount", recentApps.stream().filter(a -> a.getStatus().name().equals("SHORTLISTED")).count());
        model.addAttribute("reviewCount", recentApps.stream().filter(a -> a.getStatus().name().equals("UNDER_REVIEW")).count());
        
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setFullName(user.getFullName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhone(user.getPhone());
        profileDTO.setEducation(user.getEducation());
        profileDTO.setSkills(user.getSkills());
        profileDTO.setExperience(user.getExperience());
        
        model.addAttribute("user", user);
        model.addAttribute("profileDTO", profileDTO);
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @Valid @ModelAttribute("profileDTO") ProfileDTO profileDTO,
                                BindingResult result, Model model, RedirectAttributes ra) {
        User user = userService.findByEmail(userDetails.getUsername());
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "student/profile";
        }
        userService.updateProfile(user.getId(), profileDTO);
        ra.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/student/profile";
    }

    @PostMapping("/upload-resume")
    public String uploadResume(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam("resume") MultipartFile file, RedirectAttributes ra) {
        User user = userService.findByEmail(userDetails.getUsername());
        try {
            userService.uploadResume(user.getId(), file);
            ra.addFlashAttribute("successMessage", "Resume uploaded successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/browse-jobs")
    public String browseJobs(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) JobType jobType,
                             @RequestParam(required = false) String experienceLevel,
                             @RequestParam(required = false) Double salaryMin,
                             @RequestParam(required = false) Double salaryMax,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        List<Job> jobs = jobService.searchJobs(keyword, location, jobType, experienceLevel, salaryMin, salaryMax);
        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("jobType", jobType);
        model.addAttribute("experienceLevel", experienceLevel);
        model.addAttribute("salaryMin", salaryMin);
        model.addAttribute("salaryMax", salaryMax);
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("totalResults", jobs.size());
        return "student/browse-jobs";
    }

    @GetMapping("/job/{id}")
    public String jobDetail(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Job job = jobService.findById(id);
        model.addAttribute("job", job);
        model.addAttribute("user", user);
        model.addAttribute("hasApplied", applicationService.hasApplied(user, job));
        model.addAttribute("isSaved", jobService.isJobSaved(user, job));
        return "student/job-detail";
    }

    @PostMapping("/apply/{id}")
    public String applyForJob(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        User user = userService.findByEmail(userDetails.getUsername());
        Job job = jobService.findById(id);
        
        if (user.getResumePath() == null) {
            ra.addFlashAttribute("errorMessage", "Please upload your resume in the profile section before applying.");
            return "redirect:/student/profile";
        }
        
        try {
            applicationService.applyForJob(user, job);
            ra.addFlashAttribute("successMessage", "Application submitted successfully for " + job.getTitle());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/my-applications";
    }

    @GetMapping("/my-applications")
    public String myApplications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("applications", applicationService.getUserApplications(user));
        return "student/my-applications";
    }

    @GetMapping("/saved-jobs")
    public String savedJobs(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("savedJobs", jobService.getSavedJobs(user));
        return "student/saved-jobs";
    }

    @PostMapping("/save-job/{id}")
    public String saveJob(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        User user = userService.findByEmail(userDetails.getUsername());
        Job job = jobService.findById(id);
        jobService.saveJob(user, job);
        ra.addFlashAttribute("successMessage", "Job saved to your wishlist!");
        return "redirect:/student/job/" + id;
    }

    @PostMapping("/unsave-job/{id}")
    public String unsaveJob(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        User user = userService.findByEmail(userDetails.getUsername());
        Job job = jobService.findById(id);
        jobService.unsaveJob(user, job);
        ra.addFlashAttribute("successMessage", "Job removed from wishlist.");
        return "redirect:/student/saved-jobs";
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("passwordDTO", new PasswordDTO());
        return "student/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute("passwordDTO") PasswordDTO passwordDTO,
                                 BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) return "student/change-password";
        
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            return "student/change-password";
        }
        
        try {
            userService.changePassword(userDetails.getUsername(), passwordDTO);
            ra.addFlashAttribute("successMessage", "Password updated successfully!");
            return "redirect:/student/dashboard";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/student/change-password";
        }
    }
}
