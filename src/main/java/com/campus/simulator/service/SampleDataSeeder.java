package com.campus.simulator.service;

import com.campus.simulator.model.allocation.RequestPriority;
import com.campus.simulator.model.allocation.RequestStatus;
import com.campus.simulator.model.allocation.ResourceRequest;
import com.campus.simulator.model.allocation.ResourceRequestBuilder;
import com.campus.simulator.model.requester.Department;
import com.campus.simulator.model.requester.EventOrganizer;
import com.campus.simulator.model.requester.FacultyMember;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.requester.StudentClub;
import com.campus.simulator.model.resource.Classroom;
import com.campus.simulator.model.resource.Laboratory;
import com.campus.simulator.model.resource.LaptopSet;
import com.campus.simulator.model.resource.Projector;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.model.resource.ResourceType;
import com.campus.simulator.model.resource.SportsFacility;
import com.campus.simulator.model.resource.StudyRoom;
import com.campus.simulator.model.schedule.TimeSlot;
import com.campus.simulator.repository.RequesterRepository;
import com.campus.simulator.repository.ResourceRepository;
import com.campus.simulator.repository.ResourceRequestRepository;
import com.campus.simulator.util.IdGenerator;
import com.campus.simulator.util.ValidationUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Seeds a comprehensive, realistic campus dataset showcasing varied resource types,
 * requester profiles, and deliberate scheduling collisions to exercise simulation rules.
 */
public class SampleDataSeeder {

    private final ResourceRepository resourceRepository;
    private final RequesterRepository requesterRepository;
    private final ResourceRequestRepository requestRepository;

    public SampleDataSeeder(ResourceRepository resourceRepository,
                            RequesterRepository requesterRepository,
                            ResourceRequestRepository requestRepository) {
        this.resourceRepository = ValidationUtils.requireNonNull(resourceRepository, "resourceRepository");
        this.requesterRepository = ValidationUtils.requireNonNull(requesterRepository, "requesterRepository");
        this.requestRepository = ValidationUtils.requireNonNull(requestRepository, "requestRepository");
    }

    public void seed() {
        IdGenerator.reset();

        // 1. Seed 14 Resources across all 6 ResourceTypes
        Resource r1 = new Classroom("RES-001", "Newton Lecture Hall", "Hall A, Science Quad", 150, "A-101", true, true);
        Resource r2 = new Classroom("RES-002", "Turing Seminar Room", "Math Building 2nd Floor", 40, "M-204", true, true);
        Resource r3 = new Classroom("RES-003", "Curie Interactive Classroom", "Old Main East Wing", 25, "OM-112", true, false);

        Resource r4 = new Laboratory("RES-004", "Ada Lovelace Computing Lab", "CS Annex Room 301", 50, "Computer Science", "General");
        Resource r5 = new Laboratory("RES-005", "Franklin Molecular Bio Lab", "Bio Sciences Level 3", 24, "Biochemistry", "BSL-2");
        Resource r6 = new Laboratory("RES-006", "Faraday Applied Physics Lab", "Physics Hall Room 105", 30, "Applied Physics", "General");

        Resource r7 = new Projector("RES-007", "Optoma High-Lumen 4K Projector", "AV Tech Depot", 5000, "4K UHD", true, "Depot Rack 1");
        Resource r8 = new Projector("RES-008", "Epson Portable Conference Projector", "Library Helpdesk", 3500, "1080p Full HD", true, "Helpdesk Cabinet");

        Resource r9 = new LaptopSet("RES-009", "Fleet A: ThinkPad Exam Carts", "IT Central Storage", 30, "Ubuntu Linux 24.04 LTS", "IT Room 12");
        Resource r10 = new LaptopSet("RES-010", "Fleet B: Latitude Workshop Kits", "Engineering Locker", 20, "Windows 11 Enterprise", "Locker Bay B");

        Resource r11 = new SportsFacility("RES-011", "Olympic Indoor Arena", "Student Recreation Center", 300, "Basketball / Volleyball", false);
        Resource r12 = new SportsFacility("RES-012", "East Campus Varsity Soccer Pitch", "Athletics Complex", 100, "Soccer / Track", true);

        Resource r13 = new StudyRoom("RES-013", "Library Quiet Breakout Pod 1", "Main Library 4th Floor", 6, true, true);
        Resource r14 = new StudyRoom("RES-014", "Collaborative Discussion Suite 2", "Student Union 2nd Floor", 12, false, true);

        List.of(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14).forEach(resourceRepository::save);

        // 2. Seed 10 Requesters across all 4 RequesterTypes
        Requester req1 = new Department("REQ-001", "Department of Computer Science", "cs-admin@campus.edu",
                PriorityTier.HIGH, "CS-DEPT", "Dr. Evelyn Vance");
        Requester req2 = new Department("REQ-002", "Department of Biology & Biotech", "biotech-chair@campus.edu",
                PriorityTier.CRITICAL, "BIO-DEPT", "Dr. Marcus Chen");

        Requester req3 = new FacultyMember("REQ-003", "Prof. Alistair Finch", "a.finch@campus.edu",
                PriorityTier.HIGH, "Physics", "Full Professor");
        Requester req4 = new FacultyMember("REQ-004", "Dr. Priya Patel", "p.patel@campus.edu",
                PriorityTier.MEDIUM, "Mathematics", "Assistant Professor");

        Requester req5 = new StudentClub("REQ-005", "Autonomous Robotics Society", "robotics-club@campus.edu",
                PriorityTier.MEDIUM, "Engineering & Tech", "Dr. Evelyn Vance");
        Requester req6 = new StudentClub("REQ-006", "Campus Film & Media Guild", "film-guild@campus.edu",
                PriorityTier.LOW, "Arts & Culture", "Prof. Sarah Jenkins");
        Requester req7 = new StudentClub("REQ-007", "Bio-Enthusiasts Student Club", "bio-club@campus.edu",
                PriorityTier.LOW, "Life Sciences", "Dr. Marcus Chen");

        Requester req8 = new EventOrganizer("REQ-008", "Annual Campus Hackathon Committee", "hackathon@campus.edu",
                PriorityTier.HIGH, "24-Hour Hackathon", 120);
        Requester req9 = new EventOrganizer("REQ-009", "Varsity Athletics Council", "athletics@campus.edu",
                PriorityTier.MEDIUM, "Inter-Collegiate Tournament", 250);
        Requester req10 = new Department("REQ-010", "Alumni & Career Relations", "alumni@campus.edu",
                PriorityTier.LOW, "ALUMNI-REL", "Arthur Pendelton");

        List.of(req1, req2, req3, req4, req5, req6, req7, req8, req9, req10).forEach(requesterRepository::save);

        // 3. Seed 18 ResourceRequests across multiple days with intentional conflicts
        LocalDate baseDate = LocalDate.now().plusDays(1);

        // Request 1: CS Dept booking Newton Lecture Hall (09:00 - 11:00)
        ResourceRequest q1 = buildRequest("RQST-001", req1, ResourceType.CLASSROOM, null,
                baseDate, 9, 0, 120, 110, RequestPriority.URGENT, "CS201 Data Structures Core Lecture");

        // Request 2: Film Guild conflicting directly with Request 1 for Classroom on baseDate (09:30 - 11:30)
        // -> Conflict! Lower priority should be waitlisted
        ResourceRequest q2 = buildRequest("RQST-002", req6, ResourceType.CLASSROOM, null,
                baseDate, 9, 30, 120, 100, RequestPriority.NORMAL, "Independent Documentary Screening");

        // Request 3: Hackathon Committee booking large hall in afternoon
        ResourceRequest q3 = buildRequest("RQST-003", req8, ResourceType.CLASSROOM, null,
                baseDate, 13, 0, 240, 120, RequestPriority.HIGH, "Campus Hackathon Opening Keynote");

        // Request 4: Prof. Finch booking Computer Lab
        ResourceRequest q4 = buildRequest("RQST-004", req3, ResourceType.LABORATORY, null,
                baseDate, 14, 0, 120, 45, RequestPriority.HIGH, "Computational Physics Simulation Lab");

        // Request 5: Student Club requesting BSL-2 hazardous bio lab
        // -> Domain Policy Violation! Student clubs cannot book BSL-2 labs -> REJECTED!
        ResourceRequest q5 = buildRequest("RQST-005", req7, ResourceType.LABORATORY, "RES-005",
                baseDate, 14, 0, 120, 20, RequestPriority.NORMAL, "Student CRISPR Culture Workshop");

        // Request 6: Bio Club requesting small Study Room
        ResourceRequest q6 = buildRequest("RQST-006", req7, ResourceType.STUDY_ROOM, null,
                baseDate, 10, 0, 120, 5, RequestPriority.LOW, "Peer Tutoring Study Session");

        // Day 2 Requests
        LocalDate day2 = baseDate.plusDays(1);

        // Request 7: Dr. Patel requesting Classroom (Cap: 35)
        ResourceRequest q7 = buildRequest("RQST-007", req4, ResourceType.CLASSROOM, null,
                day2, 10, 0, 120, 35, RequestPriority.NORMAL, "Linear Algebra Review Session");

        // Request 8: Robotics Club requesting Classroom for 20 seats at same time (10:00 - 12:00)
        // -> Capacity Optimized Strategy chooses Curie Classroom (Cap 25), leaving Turing (Cap 40) for Patel!
        ResourceRequest q8 = buildRequest("RQST-008", req5, ResourceType.CLASSROOM, null,
                day2, 10, 0, 120, 20, RequestPriority.NORMAL, "Robotics Software Architecture Meeting");

        // Request 9: CS Dept requesting Laptop Set
        ResourceRequest q9 = buildRequest("RQST-009", req1, ResourceType.LAPTOP_SET, null,
                day2, 9, 0, 180, 25, RequestPriority.HIGH, "Hands-on Cybersecurity Capture-The-Flag");

        // Request 10: Film Guild requesting Projector
        ResourceRequest q10 = buildRequest("RQST-010", req6, ResourceType.PROJECTOR, null,
                day2, 18, 0, 180, 3000, RequestPriority.NORMAL, "Evening Open-Air Film Showcase");

        // Day 3 Requests
        LocalDate day3 = baseDate.plusDays(2);

        // Request 11: Athletics Council booking Indoor Arena
        ResourceRequest q11 = buildRequest("RQST-011", req9, ResourceType.SPORTS_FACILITY, "RES-011",
                day3, 8, 0, 240, 200, RequestPriority.HIGH, "Regional Collegiate Basketball Playoffs");

        // Request 12: Athletics Council booking Varsity Pitch
        ResourceRequest q12 = buildRequest("RQST-012", req9, ResourceType.SPORTS_FACILITY, "RES-012",
                day3, 14, 0, 240, 80, RequestPriority.NORMAL, "Varsity Soccer Exhibition Match");

        // Request 13: CS Dept requesting Computing Lab on Day 3 (09:00 - 12:00)
        ResourceRequest q13 = buildRequest("RQST-013", req1, ResourceType.LABORATORY, null,
                day3, 9, 0, 180, 45, RequestPriority.URGENT, "ACM Programming Contest Elimination Round");

        // Request 14: Prof. Finch requesting Computing Lab on Day 3 (10:00 - 13:00)
        // -> Collision on Computing Lab with Request 13!
        ResourceRequest q14 = buildRequest("RQST-014", req3, ResourceType.LABORATORY, null,
                day3, 10, 0, 180, 30, RequestPriority.HIGH, "Quantum Computing Lab Exercises");

        // Request 15: Dr. Patel requesting Discussion Suite
        ResourceRequest q15 = buildRequest("RQST-015", req4, ResourceType.STUDY_ROOM, null,
                day3, 13, 0, 120, 8, RequestPriority.NORMAL, "Undergraduate Research Mentorship");

        // Request 16: Film Guild requesting Projector on Day 3
        ResourceRequest q16 = buildRequest("RQST-016", req6, ResourceType.PROJECTOR, null,
                day3, 19, 0, 180, 3000, RequestPriority.LOW, "Student Animation Short Showcase");

        // Request 17: Bio Dept requesting Molecular Bio Lab (authorized institutional request)
        ResourceRequest q17 = buildRequest("RQST-017", req2, ResourceType.LABORATORY, "RES-005",
                day3, 10, 0, 120, 20, RequestPriority.URGENT, "Accredited Genetic Sequencing Lab");

        // Request 18: Hackathon Committee requesting Laptop Set
        ResourceRequest q18 = buildRequest("RQST-018", req8, ResourceType.LAPTOP_SET, null,
                day3, 9, 0, 480, 20, RequestPriority.HIGH, "Hardware Hackathon Loaner Laptops");

        List.of(q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15, q16, q17, q18)
                .forEach(requestRepository::save);

        // Advance counters past seeded entities
        IdGenerator.setCounter("RES", 14);
        IdGenerator.setCounter("REQ", 10);
        IdGenerator.setCounter("RQST", 18);
    }

    private ResourceRequest buildRequest(String id,
                                         Requester requester,
                                         ResourceType resourceType,
                                         String targetResourceId,
                                         LocalDate date,
                                         int startHour,
                                         int startMinute,
                                         int durationMinutes,
                                         int requiredCapacity,
                                         RequestPriority priority,
                                         String purpose) {
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(startHour, startMinute));
        TimeSlot slot = TimeSlot.of(start, durationMinutes);

        return new ResourceRequestBuilder()
                .id(id)
                .requester(requester)
                .resourceType(resourceType)
                .targetResourceId(targetResourceId)
                .requestedTimeSlot(slot)
                .requiredCapacity(requiredCapacity)
                .priority(priority)
                .purpose(purpose)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now().minusHours(24))
                .build();
    }
}
