package com.campus.simulator.model.resource;

/**
 * Concrete Resource representing a library or collaborative study breakout room.
 */
public class StudyRoom extends Resource {

    private final boolean quietZone;
    private final boolean hasMonitor;

    public StudyRoom(String id,
                     String name,
                     String location,
                     int capacity,
                     boolean quietZone,
                     boolean hasMonitor) {
        super(id, name, location, capacity, ResourceType.STUDY_ROOM);
        this.quietZone = quietZone;
        this.hasMonitor = hasMonitor;
    }

    public boolean isQuietZone() {
        return quietZone;
    }

    public boolean hasMonitor() {
        return hasMonitor;
    }

    @Override
    public String getSpecificDetailsSummary() {
        return String.format("Quiet Zone: %s | External Monitor: %s",
                quietZone ? "Strict" : "Discussion Permitted",
                hasMonitor ? "Yes" : "No");
    }
}
