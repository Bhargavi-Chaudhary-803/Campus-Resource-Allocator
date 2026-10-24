package com.campus.service;

import com.campus.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * =========================================================================
 * DESIGN PATTERN: SINGLETON PATTERN
 * =========================================================================
 * CampusDatabase maintains the in-memory collections of campus resources,
 * registered requesters, submitted requests, and active allocations.
 * Preloaded with realistic university seed data.
 */
public class CampusDatabase {

    private static CampusDatabase instance;

    // [JAVA CONCEPT - COLLECTIONS FRAMEWORK]: Generic Lists storing polymorphic objects
    private final List<Resource> resources = new ArrayList<>();
    private final List<Requester> requesters = new ArrayList<>();
    private final List<AllocationRequest> requests = new ArrayList<>();
    private final List<Allocation> allocations = new ArrayList<>();

    private int requestCounter = 100;
    private int allocationCounter = 500;

    // Private constructor for Singleton pattern
    private CampusDatabase() {
        seedInitialData();
    }

    public static synchronized CampusDatabase getInstance() {
        if (instance == null) {
            instance = new CampusDatabase();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Seed Sample Data (Pre-loaded for Viva / Demo)
    // -------------------------------------------------------------------------
    public void seedInitialData() {
        resources.clear();
        requesters.clear();
        requests.clear();
        allocations.clear();

        // 1. Resources (Classrooms, Labs, Auditoriums, Equipment)
        resources.add(new Classroom("RES-01", "Newton Lecture Hall", "Science Quad Block A", 120, "A-101", true, true));
        resources.add(new Classroom("RES-02", "Turing Seminar Room", "Math Building 2nd Floor", 40, "M-204", true, false));
        resources.add(new Classroom("RES-03", "Curie Interactive Room", "East Wing Ground Floor", 25, "E-105", true, true));

        resources.add(new Laboratory("RES-04", "Ada Lovelace Computing Lab", "CS Annex Room 301", 50, "Computer Science", false, 50));
        resources.add(new Laboratory("RES-05", "Franklin Biochemistry Lab", "Bio Sciences Level 3", 24, "Biochemistry", true, 24)); // BSL safety clearance!
        resources.add(new Laboratory("RES-06", "Faraday Applied Physics Lab", "Physics Hall Room 102", 30, "Applied Physics", false, 30));

        resources.add(new Auditorium("RES-07", "Main University Auditorium", "Student Center Plaza", 300, true, true, "1200 sq ft"));
        resources.add(new Auditorium("RES-08", "Mini Amphitheatre", "Arts Complex Wing C", 80, true, true, "400 sq ft"));

        resources.add(new Equipment("RES-09", "Optoma 4K Laser Projector", "AV Tech Hub", 4000, "Projector", "SN-9821", true));
        resources.add(new Equipment("RES-10", "ThinkPad Exam Laptop Cart (30x)", "IT Central Depot", 30, "Laptop Fleet", "SN-5541", true));
        resources.add(new Equipment("RES-11", "Wireless Conference PA Kit", "Student Union Locker", 150, "Audio PA", "SN-1290", true));

        // 2. Requesters (Departments, Faculty, Student Clubs)
        Department csDept = new Department("REQ-01", "Dept of Computer Science", "cs-dept@campus.edu", PriorityLevel.URGENT, "CS", "Dr. Alan Turing");
        Department bioDept = new Department("REQ-02", "Dept of Biotechnology", "biotech@campus.edu", PriorityLevel.HIGH, "BIO", "Dr. Rosalind Franklin");
        FacultyMember profSmith = new FacultyMember("REQ-03", "Prof. John Smith", "j.smith@campus.edu", PriorityLevel.HIGH, "Senior Professor", "Computer Science");
        FacultyMember drPriya = new FacultyMember("REQ-04", "Dr. Priya Sharma", "p.sharma@campus.edu", PriorityLevel.NORMAL, "Associate Professor", "Physics");
        StudentClub roboticsClub = new StudentClub("REQ-05", "Autonomous Robotics Society", "robotics@campus.edu", PriorityLevel.NORMAL, "Technology", "Dr. Alan Turing");
        StudentClub filmClub = new StudentClub("REQ-06", "Campus Cinema & Drama Guild", "drama@campus.edu", PriorityLevel.LOW, "Cultural & Arts", "Prof. John Smith");
        StudentClub bioClub = new StudentClub("REQ-07", "Undergrad Life Sciences Club", "bioclub@campus.edu", PriorityLevel.LOW, "Science Club", "Dr. Rosalind Franklin");

        requesters.add(csDept);
        requesters.add(bioDept);
        requesters.add(profSmith);
        requesters.add(drPriya);
        requesters.add(roboticsClub);
        requesters.add(filmClub);
        requesters.add(bioClub);

        // 3. Requests with intentional scheduling collisions & capacity variations!
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate dayAfter = LocalDate.now().plusDays(2);

        // Conflict 1: CS Dept vs Film Club competing for Newton Lecture Hall at overlapping hours!
        requests.add(new AllocationRequest("RQST-01", csDept, ResourceType.CLASSROOM, null,
                new TimeSlot(tomorrow, 9, 11), 100, PriorityLevel.URGENT, "CS201 Data Structures Core Lecture"));

        requests.add(new AllocationRequest("RQST-02", filmClub, ResourceType.CLASSROOM, null,
                new TimeSlot(tomorrow, 10, 12), 80, PriorityLevel.NORMAL, "Independent Documentary Screening")); // Collides!

        // Conflict 2: Student Club requesting hazardous Bio Lab (Domain Policy Violation -> REJECTED!)
        requests.add(new AllocationRequest("RQST-03", bioClub, ResourceType.LABORATORY, "RES-05",
                new TimeSlot(tomorrow, 14, 16), 18, PriorityLevel.LOW, "Student CRISPR Gene Workshop"));

        // Normal Lab request by Faculty
        requests.add(new AllocationRequest("RQST-04", profSmith, ResourceType.LABORATORY, null,
                new TimeSlot(tomorrow, 13, 16), 40, PriorityLevel.HIGH, "Parallel Computing Lab Exam"));

        // Capacity Best-Fit demo: Small request (20 seats) vs Medium request (35 seats) at same time
        requests.add(new AllocationRequest("RQST-05", drPriya, ResourceType.CLASSROOM, null,
                new TimeSlot(dayAfter, 10, 12), 35, PriorityLevel.NORMAL, "Quantum Physics Problem Session"));

        requests.add(new AllocationRequest("RQST-06", roboticsClub, ResourceType.CLASSROOM, null,
                new TimeSlot(dayAfter, 10, 12), 20, PriorityLevel.NORMAL, "Club Leadership Meeting"));

        // Auditorium Keynote
        requests.add(new AllocationRequest("RQST-07", csDept, ResourceType.AUDITORIUM, "RES-07",
                new TimeSlot(dayAfter, 14, 18), 250, PriorityLevel.HIGH, "Annual Tech Symposium Keynote"));

        // Equipment Request
        requests.add(new AllocationRequest("RQST-08", filmClub, ResourceType.EQUIPMENT, null,
                new TimeSlot(dayAfter, 18, 21), 1, PriorityLevel.NORMAL, "Campus Open Air Film Festival"));
    }

    // -------------------------------------------------------------------------
    // Query & Mutation Methods
    // -------------------------------------------------------------------------
    public List<Resource> getResources() {
        return Collections.unmodifiableList(resources);
    }

    public List<Requester> getRequesters() {
        return Collections.unmodifiableList(requesters);
    }

    public List<AllocationRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    public List<Allocation> getAllocations() {
        return Collections.unmodifiableList(allocations);
    }

    public void addResource(Resource resource) {
        if (resource != null) {
            resources.add(resource);
        }
    }

    public void addRequester(Requester requester) {
        if (requester != null) {
            requesters.add(requester);
        }
    }

    public AllocationRequest submitRequest(Requester requester, ResourceType type, String specificResId,
                                           TimeSlot slot, int capacity, PriorityLevel priority, String purpose) {
        String id = "RQST-" + (++requestCounter);
        AllocationRequest req = new AllocationRequest(id, requester, type, specificResId, slot, capacity, priority, purpose);
        requests.add(req);
        return req;
    }

    public void addAllocation(Allocation allocation) {
        if (allocation != null) {
            allocations.add(allocation);
        }
    }

    public void clearAllocations() {
        allocations.forEach(Allocation::release);
        allocations.clear();
        requests.forEach(AllocationRequest::resetToPending);
        requesters.forEach(Requester::resetAllocationCount);
    }

    public String generateNextAllocationId() {
        return "ALC-" + (++allocationCounter);
    }

    public Optional<Resource> findResourceById(String id) {
        return resources.stream().filter(r -> r.getId().equalsIgnoreCase(id)).findFirst();
    }
}
