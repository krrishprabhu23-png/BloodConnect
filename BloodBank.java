package bloodconnect.model;

/* ============================================================
   INHERITANCE + COLLECTIONS / MAPS + INTERFACE
   BloodBank extends Entity and stores inventory using EnumMap.
   ============================================================ */

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BloodBank extends Entity implements Notifiable {
    private String name, location;
    private double latitude, longitude;
    private final Map<BloodGroup, Integer> inventory =
            new EnumMap<>(BloodGroup.class);
    private final List<String> inbox = new ArrayList<>();

    public BloodBank(String id, String name, String location,
                     double latitude, double longitude) {
        super(id);
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        for (BloodGroup bg : BloodGroup.values()) inventory.put(bg, 0);
    }

    @Override
    public NodeType getNodeType() { return NodeType.BLOOD_BANK; }

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
        System.out.println("[NOTIFY -> Blood Bank " + name + "] " + message);
    }

    @Override
    public void notifyUser(String title, String message) {
        inbox.add("[" + title + "] " + message);
        System.out.println("[NOTIFY -> Blood Bank " + name + "] " + title + ": " + message);
    }

    public void setStock(BloodGroup group, int units) {
        inventory.put(group, Math.max(0, units));
    }

    public int getStock(BloodGroup group) {
        return inventory.getOrDefault(group, 0);
    }

    public void addStock(BloodGroup group, int units) {
        inventory.merge(group, units, Integer::sum);
    }

    public boolean removeStock(BloodGroup group, int units) {
        if (units <= 0 || getStock(group) < units) return false;
        inventory.put(group, getStock(group) - units);
        return true;
    }

    public int getTotalUnits() {
        return inventory.values().stream().mapToInt(Integer::intValue).sum();
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public Map<BloodGroup, Integer> getInventory() {
        return new EnumMap<>(inventory);
    }

    @Override
    public void displayDetails() {
        System.out.printf("BloodBank[%s] %s | Location=%s | Total Units=%d%n",
                id, name, location, getTotalUnits());
    }

    @Override
    public String toString() { return "BloodBank[" + id + "] " + name; }
}
