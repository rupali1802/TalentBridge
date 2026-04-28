package com.talentbridge.controller;

import com.talentbridge.entity.User;
import com.talentbridge.service.NotificationService;
import com.talentbridge.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final NotificationService notificationService;
    private final UserService userService;

    public ApiController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/notifications/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        long count = notificationService.getUnreadCount(user);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/notifications/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
