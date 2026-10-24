package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLES: INHERITANCE & POLYMORPHISM
 * =========================================================================
 * FacultyMember represents professors, lecturers, and researchers.
 */
public class FacultyMember extends Requester {

    private final String academicRank;
    private final String departmentAffiliation;

    public FacultyMember(String id, String name, String email, PriorityLevel priorityLevel,
                         String academicRank, String departmentAffiliation) {
        super(id, name, email, RequesterType.FACULTY, priorityLevel);
        this.academicRank = academicRank;
        this.departmentAffiliation = departmentAffiliation;
    }

    public String getAcademicRank() {
        return academicRank;
    }

    public String getDepartmentAffiliation() {
        return departmentAffiliation;
    }

    // [OOP - POLYMORPHISM]: Base priority 45 -> Effective score up to 85
    @Override
    public int getBasePriorityScore() {
        return 45;
    }

    @Override
    public boolean canBook(Resource resource) {
        return resource != null;
    }

    @Override
    public String getRoleDescription() {
        return String.format("%s (%s Dept)", academicRank, departmentAffiliation);
    }
}
