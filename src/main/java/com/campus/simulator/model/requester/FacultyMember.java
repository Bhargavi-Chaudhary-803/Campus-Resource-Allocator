package com.campus.simulator.model.requester;

import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.util.ValidationUtils;

/**
 * Concrete Requester representing individual faculty conducting lectures, research, or seminars.
 */
public class FacultyMember extends Requester {

    private final String departmentName;
    private final String academicTitle;

    public FacultyMember(String id,
                         String name,
                         String contactEmail,
                         PriorityTier priorityTier,
                         String departmentName,
                         String academicTitle) {
        super(id, name, contactEmail, RequesterType.FACULTY_MEMBER, priorityTier);
        this.departmentName = ValidationUtils.requireNonBlank(departmentName, "departmentName");
        this.academicTitle = ValidationUtils.requireNonBlank(academicTitle, "academicTitle");
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getAcademicTitle() {
        return academicTitle;
    }

    @Override
    public int getAllocationPriority() {
        // Base 65 plus priority tier ranking (1..4 * 5 = 5..20) -> 70 to 85
        return 65 + (getPriorityTier().getRank() * 5);
    }

    @Override
    public String getDescription() {
        return String.format("%s, Department of %s", academicTitle, departmentName);
    }

    @Override
    public boolean canRequestResource(Resource resource) {
        // Faculty members can request classrooms, labs, study rooms, projectors, etc.
        return resource != null;
    }

    @Override
    public double calculateRequestWeight() {
        return 1.1;
    }
}
