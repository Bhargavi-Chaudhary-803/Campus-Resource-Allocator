package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLES: INHERITANCE & POLYMORPHISM
 * =========================================================================
 * Department represents academic departments (e.g. Computer Science, Physics).
 * Possesses highest institutional priority and authorization to book all resources.
 */
public class Department extends Requester {

    private final String departmentCode;
    private final String headOfDepartment;

    public Department(String id, String name, String email, PriorityLevel priorityLevel,
                      String departmentCode, String headOfDepartment) {
        super(id, name, email, RequesterType.DEPARTMENT, priorityLevel);
        this.departmentCode = departmentCode;
        this.headOfDepartment = headOfDepartment;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public String getHeadOfDepartment() {
        return headOfDepartment;
    }

    // [OOP - POLYMORPHISM]: Base priority 60 -> Effective Score up to 100
    @Override
    public int getBasePriorityScore() {
        return 60;
    }

    // [OOP - POLYMORPHISM]: Departments have clearance to reserve any resource
    @Override
    public boolean canBook(Resource resource) {
        return resource != null;
    }

    @Override
    public String getRoleDescription() {
        return String.format("Dept [%s], HOD: %s", departmentCode, headOfDepartment);
    }
}
