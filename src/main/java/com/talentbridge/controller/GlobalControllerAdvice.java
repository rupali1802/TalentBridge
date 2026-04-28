package com.talentbridge.controller;

import com.talentbridge.entity.User;
import com.talentbridge.service.NotificationService;
import com.talentbridge.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;
    private final NotificationService notificationService;

    public GlobalControllerAdvice(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model, jakarta.servlet.http.HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            userService.findOptionalByEmail(auth.getName()).ifPresent(user -> {
                model.addAttribute("currentUser", user);
                model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
                model.addAttribute("notifications", notificationService.getRecentNotifications(user));
            });
        } else {
            // Default values for anonymous users to avoid NPE in header
            model.addAttribute("unreadCount", 0L);
            model.addAttribute("notifications", new java.util.ArrayList<>());
        }
    }
}
