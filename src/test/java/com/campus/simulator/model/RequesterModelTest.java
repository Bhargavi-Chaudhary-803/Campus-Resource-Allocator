package com.campus.simulator.model;

import com.campus.simulator.model.requester.Department;
import com.campus.simulator.model.requester.EventOrganizer;
import com.campus.simulator.model.requester.FacultyMember;
import com.campus.simulator.model.requester.PriorityTier;
import com.campus.simulator.model.requester.Requester;
import com.campus.simulator.model.requester.RequesterFactory;
import com.campus.simulator.model.requester.RequesterType;
import com.campus.simulator.model.requester.StudentClub;
import com.campus.simulator.model.resource.Classroom;
import com.campus.simulator.model.resource.Laboratory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Requester Domain Model & Polymorphic Priority Tests")
class RequesterModelTest {

    @Test
    @DisplayName("Should demonstrate polymorphic allocation priority ranking across requester types")
    void testPolymorphicPriorities() {
        Requester dept = new Department("REQ-1", "CS Dept", "cs@campus.edu", PriorityTier.HIGH, "CS", "Dr. Vance");
        Requester faculty = new FacultyMember("REQ-2", "Prof. Finch", "finch@campus.edu", PriorityTier.HIGH, "Physics", "Prof");
        Requester club = new StudentClub("REQ-3", "Robotics", "robot@campus.edu", PriorityTier.HIGH, "Tech", "Dr. Vance");
        Requester event = new EventOrganizer("REQ-4", "Hackathon", "hack@campus.edu", PriorityTier.HIGH, "Codefest", 100);

        List<Requester> requesters = List.of(dept, faculty, club, event);

        // Department has highest institutional priority
        assertTrue(dept.getAllocationPriority() > faculty.getAllocationPriority());
        assertTrue(faculty.getAllocationPriority() > club.getAllocationPriority());

        // Event priority scales with attendance
        assertTrue(event.getAllocationPriority() >= club.getAllocationPriority());
    }

    @Test
    @DisplayName("Should enforce domain permissions: Student clubs barred from BSL-2 hazardous labs")
    void testRequesterPermissionRules() {
        Department dept = new Department("REQ-1", "Bio Dept", "bio@campus.edu", PriorityTier.CRITICAL, "BIO", "Dr. Chen");
        StudentClub club = new StudentClub("REQ-2", "Bio Club", "bioclub@campus.edu", PriorityTier.LOW, "Biology", "Dr. Chen");

        Classroom standardClassroom = new Classroom("RES-1", "Room 101", "Main", 30, "101", true, false);
        Laboratory hazardousLab = new Laboratory("RES-2", "Virology Lab", "Science Bldg", 20, "Bio", "BSL-2");

        // Both can book standard classrooms
        assertTrue(dept.canRequestResource(standardClassroom));
        assertTrue(club.canRequestResource(standardClassroom));

        // Department can book BSL-2 lab, but student club is barred
        assertTrue(dept.canRequestResource(hazardousLab));
        assertFalse(club.canRequestResource(hazardousLab));
    }

    @Test
    @DisplayName("Should create requesters via RequesterFactory")
    void testRequesterFactory() {
        Requester req = RequesterFactory.createRequester(
                RequesterType.STUDENT_CLUB,
                "REQ-010",
                "Chess Club",
                "chess@campus.edu",
                PriorityTier.MEDIUM,
                Map.of("clubCategory", "Strategy & Games", "facultyAdvisor", "Prof. Fisher")
        );

        assertInstanceOf(StudentClub.class, req);
        assertEquals("Chess Club", req.getName());
        assertEquals(PriorityTier.MEDIUM, req.getPriorityTier());
    }
}
