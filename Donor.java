package bloodconnect.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * ============================================================
 * INHERITANCE + INTERFACE IMPLEMENTATION
 * ============================================================
 * Donor IS-A Entity and IS-A Notifiable.
 */
public class Donor extends Entity implements Notifiable {
    private String name;
    private int age;
    private BloodGroup bloodGroup;
    private String location;
    private double latitude, longitude;
    private boolean eligible = true;
    private boolean emergencyAvailable = true;
    private LocalDate lastDonationDate;
    private int donationCount;
    private double reliabilityScore = 70.0;
    private int emergencyResponses, successfulResponses;
    private final List<String> inbox = new ArrayList<>();

    public Donor(String id, String name, BloodGroup bloodGroup, int age,
                 String location, double latitude, double longitude) {
        super(id);
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.age = age;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public NodeType getNodeType() { return NodeType.DONOR; }

    // METHOD OVERRIDING: implementation of Searchable.matches().
    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isBlank()) return false;
        String k = keyword.toLowerCase();
        return name.toLowerCase().contains(k)
                || id.toLowerCase().contains(k)
                || bloodGroup.toString().toLowerCase().contains(k)
                || location.toLowerCase().contains(k);
    }

    // METHOD OVERRIDING: implementation of Notifiable.
    @Override
    public void notifyUser(String message) {
        inbox.add(message);
        System.out.println("[NOTIFY -> Donor " + name + "] " + message);
    }

    // METHOD OVERLOADING + OVERRIDING.
    @Override
    public void notifyUser(String title, String message) {
        inbox.add("[" + title + "] " + message);
        System.out.println("[NOTIFY -> Donor " + name + "] " + title + ": " + message);
    }

    public boolean checkEligibility() {
        if (age < 18 || age > 65) {
            eligible = false;
            return false;
        }
        if (lastDonationDate != null &&
                ChronoUnit.DAYS.between(lastDonationDate, LocalDate.now()) < 90) {
            eligible = false;
            return false;
        }
        eligible = true;
        return true;
    }

    public void recordDonation() {
        lastDonationDate = LocalDate.now();
        donationCount++;
        eligible = false;
    }

    public void recordEmergencyResponse(boolean success) {
        emergencyResponses++;
        if (success) successfulResponses++;
        reliabilityScore = Math.min(100.0,
                successfulResponses * 100.0 / emergencyResponses);
    }

    public String getName() { return name; }
    public BloodGroup getBloodGroup() { return bloodGroup; }
    public int getAge() { return age; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isEligible() { return eligible; }
    public boolean isEmergencyAvailable() { return emergencyAvailable; }
    public double getReliabilityScore() { return reliabilityScore; }
    public int getDonationCount() { return donationCount; }

    public void setEmergencyAvailable(boolean value) { emergencyAvailable = value; }

    public List<String> getInbox() {
        return Collections.unmodifiableList(inbox);
    }

    @Override
    public void displayDetails() {
        System.out.printf(
                "Donor[%s] %s | %s | Age=%d | Location=%s | Eligible=%s | Reliability=%.0%%%n",
                id, name, bloodGroup, age, location, eligible, reliabilityScore);
    }

    @Override
    public String toString() {
        return String.format("Donor[%s] %s (%s)", id, name, bloodGroup);
    }
}
