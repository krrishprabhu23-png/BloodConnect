package bloodconnect.model;

/* ============================================================
   INHERITANCE + METHOD OVERRIDING + ENCAPSULATION
   BloodRequest extends Entity and encapsulates request data.
   Emergency requests are CRITICAL or EMERGENCY.
   ============================================================ */

public class BloodRequest extends Entity {
    private String hospitalId, patientId;
    private BloodGroup bloodGroup;
    private int quantity;
    private Urgency urgency;
    private RequestStatus status = RequestStatus.PENDING;
    private boolean emergency;

    public BloodRequest(String id, String hospitalId, String patientId,
                        BloodGroup bloodGroup, int quantity, Urgency urgency) {
        super(id);
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.urgency = urgency;
        emergency = urgency == Urgency.EMERGENCY || urgency == Urgency.CRITICAL;
        if (emergency) status = RequestStatus.SEARCHING;
    }

    @Override
    public NodeType getNodeType() {
        return emergency ? NodeType.EMERGENCY_REQUEST : NodeType.BLOOD_REQUEST;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isBlank()) return false;
        String k = keyword.toLowerCase();
        return id.toLowerCase().contains(k)
                || bloodGroup.toString().toLowerCase().contains(k)
                || status.name().toLowerCase().contains(k);
    }

    public String getHospitalId() { return hospitalId; }
    public String getPatientId() { return patientId; }
    public BloodGroup getBloodGroup() { return bloodGroup; }
    public int getQuantity() { return quantity; }
    public Urgency getUrgency() { return urgency; }
    public RequestStatus getStatus() { return status; }
    // Encapsulated emergency flag used by the emergency-request view.
    public boolean isEmergency() { return emergency; }
    public void setStatus(RequestStatus status) { this.status = status; }

    @Override
    public void displayDetails() {
        System.out.printf("Request[%s] | Blood=%s | Quantity=%d | Urgency=%s | Status=%s%n",
                id, bloodGroup, quantity, urgency, status);
    }

    @Override
    public String toString() {
        return String.format("Request[%s] %s x%d | %s | Status=%s",
                id, bloodGroup, quantity, urgency, status);
    }
}
