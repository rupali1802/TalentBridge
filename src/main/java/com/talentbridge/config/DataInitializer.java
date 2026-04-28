package com.talentbridge.config;

import com.talentbridge.entity.Job;
import com.talentbridge.entity.User;
import com.talentbridge.enums.JobType;
import com.talentbridge.enums.Role;
import com.talentbridge.repository.JobRepository;
import com.talentbridge.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, JobRepository jobRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Create Default Admin
        if (!userRepository.existsByEmail("admin@talentbridge.com")) {
            User admin = new User();
            admin.setFullName("Platform Admin");
            admin.setEmail("admin@talentbridge.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            logger.info("Default admin account created: admin@talentbridge.com / Admin@123");
        }

        // 2. Create Sample Employers
        User google = createEmployer("Google", "hr@google.com");
        User infosys = createEmployer("Infosys", "hr@infosys.com");
        User tcs = createEmployer("TCS", "hr@tcs.com");
        User wipro = createEmployer("Wipro", "hr@wipro.com");
        User accenture = createEmployer("Accenture", "hr@accenture.com");
        User startup = createEmployer("StartUp Labs", "hr@startup.com");
        User hcl = createEmployer("HCL", "hr@hcl.com");
        User apollo = createEmployer("Apollo Hospitals", "hr@apollo.com");
        User reliance = createEmployer("Reliance", "hr@reliance.com");
        User zomato = createEmployer("Zomato", "hr@zomato.com");

        // 3. Create Sample Jobs (at least 25-30)
        if (jobRepository.count() < 10) { 
            List<Job> jobs = new ArrayList<>();
            
            // IT Sector
            jobs.add(createJob(infosys, "Software Developer – Java", "Bangalore", 600000.0, 1000000.0, "Fresher", "Java, Spring Boot, MySQL", "Develop and maintain robust web applications.", JobType.FULL_TIME));
            jobs.add(createJob(tcs, "Data Analyst", "Hyderabad", 500000.0, 800000.0, "1–3 years", "Python, SQL, Excel", "Analyze complex datasets to provide business insights.", JobType.FULL_TIME));
            jobs.add(createJob(wipro, "Frontend Developer", "Chennai", 400000.0, 700000.0, "Fresher", "HTML, CSS, JavaScript", "Design and implement modern user interfaces.", JobType.FULL_TIME));
            jobs.add(createJob(google, "Senior Site Reliability Engineer", "Bangalore", 2500000.0, 4000000.0, "Senior", "Go, Kubernetes, Cloud", "Scaling and maintaining high availability systems.", JobType.FULL_TIME));
            jobs.add(createJob(startup, "AI/ML Intern", "Remote", 15000.0, 25000.0, "Fresher", "Python, PyTorch", "Research and develop ML models for predictive analysis.", JobType.INTERNSHIP));

            // Finance & HR
            jobs.add(createJob(accenture, "Business Analyst", "Mumbai", 800000.0, 1500000.0, "Mid-level", "Consulting, Strategy", "Bridging the gap between IT and business teams.", JobType.FULL_TIME));
            jobs.add(createJob(hcl, "HR Manager", "Delhi", 800000.0, 1200000.0, "Mid-level", "Recruitment, Payroll", "Oversee human resources operations and policy.", JobType.FULL_TIME));
            jobs.add(createJob(reliance, "Sales Executive", "Mumbai", 300000.0, 600000.0, "Fresher", "Communication, Sales", "Drive sales growth and maintain client relationships.", JobType.FULL_TIME));

            // Healthcare & Education
            jobs.add(createJob(apollo, "Staff Nurse", "Chennai", 250000.0, 500000.0, "Fresher", "Nursing, Patient Care", "Provide high-quality healthcare and patient support.", JobType.FULL_TIME));
            jobs.add(createJob(startup, "EdTech Content Specialist", "Remote", 400000.0, 600000.0, "1–2 years", "Content Creation, LMS", "Design educational content for online learning.", JobType.FULL_TIME));

            // Marketing & Sales
            jobs.add(createJob(zomato, "Digital Marketing Executive", "Remote", 300000.0, 600000.0, "Fresher", "SEO, Social Media", "Manage social media campaigns and digital presence.", JobType.FULL_TIME));
            jobs.add(createJob(zomato, "Brand Manager", "Gurgaon", 1200000.0, 1800000.0, "Mid-level", "Branding, Marketing", "Develop and execute brand strategy.", JobType.FULL_TIME));

            // More Jobs to reach 25+
            jobs.add(createJob(infosys, "Cloud Architect", "Pune", 1500000.0, 2500000.0, "Senior", "AWS, Azure", "Design cloud infrastructure solutions.", JobType.FULL_TIME));
            jobs.add(createJob(tcs, "QA Automation Engineer", "Noida", 500000.0, 900000.0, "2–4 years", "Selenium, Jenkins", "Ensure software quality through automated testing.", JobType.FULL_TIME));
            jobs.add(createJob(wipro, "Network Engineer", "Bangalore", 450000.0, 800000.0, "1–3 years", "Cisco, Networking", "Manage and optimize enterprise network systems.", JobType.FULL_TIME));
            jobs.add(createJob(google, "Product Manager", "Hyderabad", 3000000.0, 5000000.0, "Senior", "Agile, Product Strategy", "Define the product roadmap and drive innovation.", JobType.FULL_TIME));
            jobs.add(createJob(accenture, "UI/UX Designer", "Pune", 600000.0, 1100000.0, "2–5 years", "Figma, Adobe XD", "Create user-centric design experiences.", JobType.FULL_TIME));
            jobs.add(createJob(reliance, "Retail Store Manager", "Delhi", 500000.0, 900000.0, "3+ years", "Management, Retail", "Handle store operations and staff management.", JobType.FULL_TIME));
            jobs.add(createJob(hcl, "System Administrator", "Bangalore", 600000.0, 1000000.0, "Mid-level", "Linux, Shell Scripting", "Manage servers and system configurations.", JobType.FULL_TIME));
            jobs.add(createJob(apollo, "Medical Lab Technician", "Mumbai", 300000.0, 500000.0, "Fresher", "Lab Analysis", "Perform diagnostic tests and sample analysis.", JobType.FULL_TIME));
            jobs.add(createJob(infosys, "Java Backend Intern", "Bangalore", 20000.0, 30000.0, "Fresher", "Java, Core Java", "Assist in building backend microservices.", JobType.INTERNSHIP));
            jobs.add(createJob(tcs, "Python Developer", "Kolkata", 500000.0, 850000.0, "Fresher", "Python, Django", "Develop scalable backend applications using Python.", JobType.FULL_TIME));
            jobs.add(createJob(wipro, "Cybersecurity Analyst", "Bangalore", 700000.0, 1300000.0, "Mid-level", "Pen-testing, Security", "Protect systems from cyber threats.", JobType.FULL_TIME));
            jobs.add(createJob(accenture, "Full Stack Developer", "Gurgaon", 900000.0, 1600000.0, "Mid-level", "React, Node.js, MongoDB", "Develop end-to-end web applications.", JobType.FULL_TIME));
            jobs.add(createJob(startup, "Content Writer Intern", "Remote", 10000.0, 15000.0, "Fresher", "Writing, SEO", "Write engaging blog posts and articles.", JobType.INTERNSHIP));
            jobs.add(createJob(google, "Mobile App Developer", "Bangalore", 1200000.0, 2200000.0, "Mid-level", "Flutter, Dart", "Build cross-platform mobile applications.", JobType.FULL_TIME));
            jobs.add(createJob(reliance, "Supply Chain Manager", "Ahmedabad", 1000000.0, 1800000.0, "Senior", "Logistics, Supply Chain", "Optimize product flow and logistics.", JobType.FULL_TIME));
            jobs.add(createJob(hcl, "Technical Support Engineer", "Chennai", 350000.0, 550000.0, "Fresher", "Troubleshooting", "Resolve technical issues for clients.", JobType.FULL_TIME));
            jobs.add(createJob(google, "DevOps Engineer", "Hyderabad", 1500000.0, 2500000.0, "Mid-level", "Docker, Terraform", "Automate infrastructure and deployment pipelines.", JobType.FULL_TIME));
            jobs.add(createJob(zomato, "Operations Manager", "Mumbai", 900000.0, 1400000.0, "Mid-level", "Operations, Logistics", "Manage day-to-day business operations.", JobType.FULL_TIME));

            jobRepository.saveAll(jobs);
            logger.info("30 Sample jobs created across multiple sectors.");
        }
    }

    private User createEmployer(String name, String email) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setFullName(name + " HR");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("Password@123"));
            user.setRole(Role.EMPLOYER);
            user.setCompany(name);
            user.setEnabled(true);
            return userRepository.save(user);
        });
    }

    private Job createJob(User employer, String title, String location, Double min, Double max, String exp, String skills, String desc, JobType type) {
        Job job = new Job();
        job.setTitle(title);
        job.setEmployer(employer);
        job.setLocation(location);
        job.setSalaryMin(min);
        job.setSalaryMax(max);
        job.setExperienceLevel(exp);
        job.setSkillsRequired(skills);
        job.setDescription(desc);
        job.setJobType(type);
        job.setApplicationDeadline(LocalDate.now().plusMonths(2));
        job.setActive(true);
        return job;
    }
}
