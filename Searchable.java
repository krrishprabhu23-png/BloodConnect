package bloodconnect.model;

/*
 * ============================================================
 * INTERFACE + METHOD OVERLOADING
 * ============================================================
 */
public interface Searchable {
    boolean matches(String keyword);

    // Method overloading using a default interface method.
    default boolean matches(String keyword, NodeType typeFilter) {
        return matches(keyword);
    }
}
