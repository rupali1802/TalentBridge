package com.talentbridge.service;

import com.talentbridge.entity.ActivityLog;
import com.talentbridge.entity.User;
import com.talentbridge.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void log(String action, String details, User user) {
        ActivityLog activityLog = new ActivityLog(action, details, user);
        activityLogRepository.save(activityLog);
    }

    public List<ActivityLog> getRecentActivity() {
        return activityLogRepository.findTop50ByOrderByTimestampDesc();
    }

    public List<ActivityLog> getUserActivity(User user) {
        return activityLogRepository.findByUserOrderByTimestampDesc(user);
    }

    public List<ActivityLog> searchActivity(String action) {
        return activityLogRepository.findByActionContainingIgnoreCaseOrderByTimestampDesc(action);
    }
}
