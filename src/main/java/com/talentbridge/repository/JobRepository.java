package com.talentbridge.repository;

import com.talentbridge.entity.Job;
import com.talentbridge.entity.User;
import com.talentbridge.enums.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByEmployerOrderByPostedAtDesc(User employer);

    List<Job> findByActiveTrueOrderByPostedAtDesc();

    @Query("SELECT j FROM Job j WHERE j.active = true " +
           "AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:jobType IS NULL OR j.jobType = :jobType) " +
           "AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) " +
           "AND (:salaryMin IS NULL OR j.salaryMax >= :salaryMin) " +
           "AND (:salaryMax IS NULL OR j.salaryMin <= :salaryMax) " +
           "ORDER BY j.postedAt DESC")
    List<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("jobType") JobType jobType,
                         @Param("experienceLevel") String experienceLevel,
                         @Param("salaryMin") Double salaryMin,
                         @Param("salaryMax") Double salaryMax);

    long countByActiveTrue();

    @Query("SELECT DISTINCT j.location FROM Job j WHERE j.active = true ORDER BY j.location")
    List<String> findDistinctLocations();
}
