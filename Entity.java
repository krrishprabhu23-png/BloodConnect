package bloodconnect.model;

import java.time.LocalDateTime;
import java.util.Objects;

/*
 * ============================================================
 * ABSTRACTION + INHERITANCE
 * ============================================================
 * Entity is an ABSTRACT class.
 * Donor, Hospital, BloodBank, Patient and BloodRequest inherit
 * from this class.
 */
public abstract class Entity implements GraphEntity, Searchable {
    protected String id;
    protected LocalDateTime createdAt;

    protected Entity(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String getNodeId() { return id; }

    // ABSTRACT METHOD: subclasses must override it.
    public abstract void displayDetails();

    // METHOD OVERLOADING: same method name, different parameter.
    public void displayDetails(boolean verbose) {
        displayDetails();
        if (verbose) System.out.println("Created At: " + createdAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity other = (Entity) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
