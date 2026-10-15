package bloodconnect.model;

/* ============================================================
   INHERITANCE + INTERFACE + METHOD OVERRIDING
   Hospital extends Entity and implements Notifiable.
   ============================================================ */

import java.util.ArrayList;
import java.util.List;

public class Hospital extends Entity implements Notifiable {
    private String name, location;
    private double latitude, longitude;
    private final List<String> inbox = new ArrayList<>();

    public Hospital(String id, String name, String location,
                    double latitude, double longitude) {
        super(id);
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public NodeType getNodeType() { return NodeType.HOSPITAL; }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isBlank()) return false;
        String k = keyword.toLowerCase();
        return name.toLowerCase().contains(k)
                || location.toLowerCase().contains(k)
                || id.toLowerCase().contains(k);
    }

    @Override
    public void notifyUser(String message) {
        inbox.add(message);
        System.out.println("[NOTIFY -> Hospital " + name + "] " + message);
    }

    @Override
    public void notifyUser(String title, String message) {
        inbox.add("[" + title + "] " + message);
        System.out.println("[NOTIFY -> Hospital " + name + "] " + title + ": " + message);
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public void displayDetails() {
        System.out.printf("Hospital[%s] %s | Location=%s%n", id, name, location);
    }

    @Override
    public String toString() { return "Hospital[" + id + "] " + name; }
}
