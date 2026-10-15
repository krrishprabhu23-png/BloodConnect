package bloodconnect.model;

public enum Urgency {
    LOW(1), NORMAL(2), HIGH(3), CRITICAL(4), EMERGENCY(5);

    private final int priority;

    Urgency(int priority) { this.priority = priority; }

    public int getPriority() { return priority; }
}
