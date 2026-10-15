package bloodconnect.model;

/*
 * ============================================================
 * INTERFACE
 * ============================================================
 */
public interface Notifiable {
    void notifyUser(String message);

    // METHOD OVERLOADING
    void notifyUser(String title, String message);
}
