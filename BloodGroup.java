package bloodconnect.model;

import java.util.Set;

public enum BloodGroup {
    O_NEG("O-"), O_POS("O+"), A_NEG("A-"), A_POS("A+"),
    B_NEG("B-"), B_POS("B+"), AB_NEG("AB-"), AB_POS("AB+");

    private final String label;

    BloodGroup(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }

    public Set<BloodGroup> compatibleDonors() {
        return switch (this) {
            case O_NEG -> Set.of(O_NEG);
            case O_POS -> Set.of(O_NEG, O_POS);
            case A_NEG -> Set.of(O_NEG, A_NEG);
            case A_POS -> Set.of(O_NEG, O_POS, A_NEG, A_POS);
            case B_NEG -> Set.of(O_NEG, B_NEG);
            case B_POS -> Set.of(O_NEG, O_POS, B_NEG, B_POS);
            case AB_NEG -> Set.of(O_NEG, A_NEG, B_NEG, AB_NEG);
            case AB_POS -> Set.of(values());
        };
    }

    public boolean canDonateTo(BloodGroup recipient) {
        return recipient.compatibleDonors().contains(this);
    }
}
