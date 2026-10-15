package bloodconnect.model;

/* ============================================================
   INHERITANCE + METHOD OVERRIDING
   Patient extends the abstract Entity class.
   ============================================================ */

public class Patient extends Entity {
    private String name;
    private BloodGroup bloodGroup;
    private String hospitalId;

    public Patient(String id, String name, BloodGroup bloodGroup, String hospitalId) {
        super(id);
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.hospitalId = hospitalId;
    }

    @Override
    public NodeType getNodeType() { return NodeType.PATIENT; }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isBlank()) return false;
        String k = keyword.toLowerCase();
        return name.toLowerCase().contains(k)
                || bloodGroup.toString().toLowerCase().contains(k);
    }

    public String getName() { return name; }
    public BloodGroup getBloodGroup() { return bloodGroup; }
    public String getHospitalId() { return hospitalId; }

    @Override
    public void displayDetails() {
        System.out.printf("Patient[%s] %s | Blood Group=%s | Hospital=%s%n",
                id, name, bloodGroup, hospitalId);
    }

    @Override
    public String toString() {
        return String.format("Patient[%s] %s (%s)", id, name, bloodGroup);
    }
}
