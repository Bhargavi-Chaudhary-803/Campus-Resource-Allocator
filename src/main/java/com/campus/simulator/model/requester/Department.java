package com.campus.simulator.model.requester;

import com.campus.simulator.model.resource.Resource;
import com.campus.simulator.util.ValidationUtils;

/**
 * Concrete Requester representing an academic or administrative department.
 * Possesses highest institutional priority.
 */
public class Department extends Requester {

    private final String departmentCode;
    private final String facultyLead;

    public Department(String id,
                      String name,
                      String contactEmail,
                      PriorityTier priorityTier,
                      String departmentCode,
                      String facultyLead) {
        super(id, name, contactEmail, RequesterType.DEPARTMENT, priorityTier);
        this.departmentCode = ValidationUtils.requireNonBlank(departmentCode, "departmentCode");
        this.facultyLead = ValidationUtils.requireNonBlank(facultyLead, "facultyLead");
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public String getFacultyLead() {
        return facultyLead;
    }

    @Override
    public int getAllocationPriority() {
        // Base 80 plus priority tier ranking (1..4 * 5 = 5..20) -> 85 to 100
        return 80 + (getPriorityTier().getRank() * 5);
    }

    @Override
    public String getDescription() {
        return String.format("Dept [%s] led by %s", departmentCode, facultyLead);
    }

    @Override
    public boolean canRequestResource(Resource resource) {
        // Departments have full institutional authority to request any campus resource
        return resource != null;
    }

    @Override
    public double calculateRequestWeight() {
        return 1.0;
    }
}
