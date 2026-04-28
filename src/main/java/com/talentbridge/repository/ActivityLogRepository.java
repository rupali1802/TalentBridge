package com.talentbridge.repository;

import com.talentbridge.entity.ActivityLog;
import com.talentbridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUserOrderByTimestampDesc(User user);

    List<ActivityLog> findTop50ByOrderByTimestampDesc();

    List<ActivityLog> findByActionContainingIgnoreCaseOrderByTimestampDesc(String action);
}
