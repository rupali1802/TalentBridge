package com.talentbridge.service;

import com.talentbridge.dto.PasswordDTO;
import com.talentbridge.dto.ProfileDTO;
import com.talentbridge.dto.RegisterDTO;
import com.talentbridge.entity.User;
import com.talentbridge.enums.Role;
import com.talentbridge.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       NotificationService notificationService,
                       ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.activityLogService = activityLogService;
    }

    public User registerUser(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setCompany(dto.getCompany());
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        notificationService.createNotification(
                "Welcome to TalentBridge!",
                "Your account has been created successfully. Start exploring opportunities!",
                savedUser
        );

        activityLogService.log("USER_REGISTERED",
                "New " + dto.getRole().name().toLowerCase() + " registered: " + dto.getFullName(),
                savedUser);

        return savedUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> findOptionalByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateProfile(Long userId, ProfileDTO dto) {
        User user = findById(userId);
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setSkills(dto.getSkills());
        user.setExperience(dto.getExperience());
        user.setEducation(dto.getEducation());
        user.setCompany(dto.getCompany());

        activityLogService.log("PROFILE_UPDATED", "Profile updated for: " + user.getEmail(), user);
        return userRepository.save(user);
    }

    public void uploadResume(Long userId, org.springframework.web.multipart.MultipartFile file) {
        User user = findById(userId);
        try {
            String fileName = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get("uploads/resumes/" + fileName);
            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.write(path, file.getBytes());
            
            user.setResumePath(fileName);
            userRepository.save(user);
            activityLogService.log("RESUME_UPLOADED", "Resume uploaded: " + fileName, user);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not store file: " + e.getMessage());
        }
    }

    public void changePassword(String email, PasswordDTO dto) {
        User user = findByEmail(email);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        activityLogService.log("PASSWORD_CHANGED", "Password changed successfully", user);

        notificationService.createNotification(
                "Password Changed",
                "Your password was changed successfully. If this wasn't you, please contact support.",
                user
        );
    }

    public String generateResetToken(String email) {
        User user = findByEmail(email);
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        notificationService.createNotification(
                "Password Reset Requested",
                "A password reset was requested for your account. Use the reset link to set a new password.",
                user
        );

        return token;
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);

        activityLogService.log("PASSWORD_RESET", "Password reset via token", user);
    }

    public void updateResumePath(String email, String path) {
        User user = findByEmail(email);
        user.setResumePath(path);
        userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }

    public long countAll() {
        return userRepository.count();
    }

    public void toggleUserStatus(Long id) {
        User user = findById(id);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }
}
