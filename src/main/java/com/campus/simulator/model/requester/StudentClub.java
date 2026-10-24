package com.campus.simulator.model.requester;

import com.campus.simulator.model.resource.Laboratory;
import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.util.ValidationUtils;

/**
 * Concrete Requester representing a registered student organization or hobby club.
 */
public class StudentClub extends Requester {

    private final String clubCategory;
    private final String facultyAdvisor;

    public StudentClub(String id,
                       String name,
                       String contactEmail,
                       PriorityTier priorityTier,
                       String clubCategory,
                       String facultyAdvisor) {
        super(id, name, contactEmail, RequesterType.STUDENT_CLUB, priorityTier);
        this.clubCategory = ValidationUtils.requireNonBlank(clubCategory, "clubCategory");
        this.facultyAdvisor = ValidationUtils.requireNonBlank(facultyAdvisor, "facultyAdvisor");
    }

    public String getClubCategory() {
        return clubCategory;
    }

    public String getFacultyAdvisor() {
        return facultyAdvisor;
    }

    @Override
    public int getAllocationPriority() {
        // Base 40 plus priority tier ranking (1..4 * 5 = 5..20) -> 45 to 60
        return 40 + (getPriorityTier().getRank() * 5);
    }

    @Override
    public String getDescription() {
        return String.format("%s Club (Advisor: %s)", clubCategory, facultyAdvisor);
    }

    @Override
    public boolean canRequestResource(Resource resource) {
        if (resource == null) {
            return false;
        }
        // Student clubs are barred from unescorted access to high-hazard laboratories
        if (resource instanceof Laboratory lab && lab.requiresSafetyClearance()) {
            return false;
        }
        return true;
    }

    @Override
    public double calculateRequestWeight() {
        return 0.8;
    }
}
