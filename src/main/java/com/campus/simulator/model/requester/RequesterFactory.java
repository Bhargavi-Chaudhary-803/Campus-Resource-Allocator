package com.campus.simulator.model.requester;

import com.campus.simulator.util.ValidationUtils;

import java.util.Map;

/**
 * Factory class implementing the Factory Method / Simple Factory pattern
 * for creating polymorphic Requester instances.
 */
public final class RequesterFactory {

    private RequesterFactory() {
        // Prevent instantiation
    }

    /**
     * Creates a concrete Requester based on RequesterType, standard fields, and attributes map.
     */
    public static Requester createRequester(RequesterType type,
                                            String id,
                                            String name,
                                            String email,
                                            PriorityTier tier,
                                            Map<String, Object> attributes) {
        ValidationUtils.requireNonNull(type, "type");
        Map<String, Object> attrs = attributes == null ? Map.of() : attributes;

        return switch (type) {
            case DEPARTMENT -> new Department(
                    id,
                    name,
                    email,
                    tier,
                    attrs.getOrDefault("departmentCode", "DEPT-" + id).toString(),
                    attrs.getOrDefault("facultyLead", "Head of Department").toString()
            );
            case FACULTY_MEMBER -> new FacultyMember(
                    id,
                    name,
                    email,
                    tier,
                    attrs.getOrDefault("departmentName", "General Academics").toString(),
                    attrs.getOrDefault("academicTitle", "Associate Professor").toString()
            );
            case STUDENT_CLUB -> new StudentClub(
                    id,
                    name,
                    email,
                    tier,
                    attrs.getOrDefault("clubCategory", "Student Life").toString(),
                    attrs.getOrDefault("facultyAdvisor", "Staff Advisor").toString()
            );
            case EVENT_ORGANIZER -> new EventOrganizer(
                    id,
                    name,
                    email,
                    tier,
                    attrs.getOrDefault("eventType", "Campus Symposium").toString(),
                    Integer.parseInt(attrs.getOrDefault("expectedAttendance", "50").toString())
            );
        };
    }
}
