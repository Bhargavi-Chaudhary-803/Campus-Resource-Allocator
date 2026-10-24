package com.campus.model;

/**
 * =========================================================================
 * OOP PRINCIPLES: INHERITANCE & POLYMORPHISM
 * =========================================================================
 * StudentClub represents student-run societies and sports clubs.
 * Crucial Domain Rule: Student clubs cannot book hazardous laboratories
 * requiring safety clearance without faculty oversight!
 */
public class StudentClub extends Requester {

    private final String clubCategory;
    private final String facultyAdvisor;

    public StudentClub(String id, String name, String email, PriorityLevel priorityLevel,
                       String clubCategory, String facultyAdvisor) {
        super(id, name, email, RequesterType.STUDENT_CLUB, priorityLevel);
        this.clubCategory = clubCategory;
        this.facultyAdvisor = facultyAdvisor;
    }

    public String getClubCategory() {
        return clubCategory;
    }

    public String getFacultyAdvisor() {
        return facultyAdvisor;
    }

    // [OOP - POLYMORPHISM]: Base priority 25 -> Effective score up to 65
    @Override
    public int getBasePriorityScore() {
        return 25;
    }

    // [OOP - POLYMORPHISM]: Policy enforcement via polymorphism!
    @Override
    public boolean canBook(Resource resource) {
        if (resource == null) return false;
        // Student clubs are barred from unescorted access to hazardous labs
        if (resource instanceof Laboratory lab) {
            return !lab.requiresSafetyClearance();
        }
        return true;
    }

    @Override
    public String getRoleDescription() {
        return String.format("%s Club (Advisor: %s)", clubCategory, facultyAdvisor);
    }
}
