package bloodconnect.service;

import bloodconnect.exception.*;
import bloodconnect.model.*;
import bloodconnect.util.GeoUtils;

import java.time.LocalDateTime;
import java.util.*;

/*
 * ============================================================
 * SERVICE LAYER
 * ============================================================
 * Uses:
 * - Collections: List, Set, Map
 * - Maps: LinkedHashMap, EnumMap
 * - Generics: Graph<Object>
 * - Exception handling
 * - Method overloading
 */
public class GraphManager {

    private final Map<String, Donor> donors = new LinkedHashMap<>();
    private final Map<String, Hospital> hospitals = new LinkedHashMap<>();
    private final Map<String, BloodBank> bloodBanks = new LinkedHashMap<>();
    private final Map<String, Patient> patients = new LinkedHashMap<>();
    private final Map<String, BloodRequest> requests = new LinkedHashMap<>();
    private final List<String> auditLog = new ArrayList<>();

    // STATIC MEMBER: shared ID counter for the whole application.
    private static int idSequence = 1000;

    // STATIC METHOD: creates unique IDs.
    private static synchronized String nextId(String prefix) {
        return prefix + (++idSequence);
    }

    private void audit(String message) {
        auditLog.add(LocalDateTime.now().toString().substring(0, 19)
                + " | " + message);
    }

    // METHOD OVERLOADING
    public Donor registerDonor(String name, BloodGroup group, int age,
                               String location, double lat, double lon)
            throws DuplicateNodeException {
        String id = nextId("D");
        Donor donor = new Donor(id, name, group, age, location, lat, lon);
        donor.checkEligibility();
        donors.put(id, donor);
        
        audit("Registered donor " + id + " " + name);
        return donor;
    }

    public Donor registerDonor(String name, BloodGroup group, int age)
            throws DuplicateNodeException {
        return registerDonor(name, group, age,
                "Chennai", 13.0800, 80.2700);
    }

    public Hospital registerHospital(String name, String location,
                                      double lat, double lon)
            throws DuplicateNodeException {
        String id = nextId("H");
        Hospital hospital = new Hospital(id, name, location, lat, lon);
        hospitals.put(id, hospital);
        
        audit("Registered hospital " + id);
        return hospital;
    }

    public BloodBank registerBloodBank(String name, String location,
                                       double lat, double lon)
            throws DuplicateNodeException {
        String id = nextId("B");
        BloodBank bank = new BloodBank(id, name, location, lat, lon);
        bloodBanks.put(id, bank);
        
        audit("Registered blood bank " + id);
        return bank;
    }

    public Patient registerPatient(String name, BloodGroup group, String hospitalId)
            throws BloodConnectException {
        if (!hospitals.containsKey(hospitalId))
            throw new InvalidRequestException("Hospital not found.");

        String id = nextId("P");
        Patient patient = new Patient(id, name, group, hospitalId);
        patients.put(id, patient);
        
        
        audit("Registered patient " + id);
        return patient;
    }

    public void connectHospitalToBank(String hospitalId, String bankId)
            throws BloodConnectException {
        if (!hospitals.containsKey(hospitalId) || !bloodBanks.containsKey(bankId))
            throw new InvalidRequestException("Hospital or blood bank not found.");

        double distance = distanceBetween(hospitalId, bankId);
        
        
        audit("Connected " + hospitalId + " <-> " + bankId);
    }

    public void recordDonation(String donorId, String bankId)
            throws BloodConnectException {
        Donor donor = donors.get(donorId);
        BloodBank bank = bloodBanks.get(bankId);

        if (donor == null || bank == null)
            throw new InvalidRequestException("Donor or blood bank not found.");

        if (!donor.checkEligibility())
            throw new DonorNotEligibleException("Donor is not eligible.");

        String donationId = nextId("DN");
        
        
        

        donor.recordDonation();
        bank.addStock(donor.getBloodGroup(), 1);
        audit("Donation " + donationId + " by " + donorId);
    }

    public BloodRequest createRequest(String hospitalId, String patientId,
                                      BloodGroup group, int quantity,
                                      Urgency urgency)
            throws BloodConnectException {
        if (!hospitals.containsKey(hospitalId))
            throw new InvalidRequestException("Hospital not found.");
        if (quantity <= 0)
            throw new InvalidRequestException("Quantity must be greater than zero.");
        if (patientId != null && !patientId.isBlank()
                && !patients.containsKey(patientId))
            throw new InvalidRequestException("Patient not found.");

        String id = nextId("R");
        BloodRequest request =
                new BloodRequest(id, hospitalId, patientId, group, quantity, urgency);
        requests.put(id, request);
        
        

        if (patientId != null && !patientId.isBlank())
            

        audit("Created request " + id);
        return request;
    }

    // METHOD OVERLOADING
    public List<DonorMatch> matchDonors(String requestId)
            throws InvalidRequestException {
        return matchDonors(requestId, Integer.MAX_VALUE);
    }

    public List<DonorMatch> matchDonors(String requestId, int topK)
            throws InvalidRequestException {
        BloodRequest request = requests.get(requestId);
        if (request == null) throw new InvalidRequestException("Request not found.");

        Hospital hospital = hospitals.get(request.getHospitalId());
        if (hospital == null) throw new InvalidRequestException("Hospital not found.");

        List<DonorMatch> matches = new ArrayList<>();

        for (Donor donor : donors.values()) {
            donor.checkEligibility();

            if (!donor.isEligible() || !donor.isEmergencyAvailable()) continue;
            if (!donor.getBloodGroup().canDonateTo(request.getBloodGroup())) continue;

            double distance = GeoUtils.distanceKm(
                    donor.getLatitude(), donor.getLongitude(),
                    hospital.getLatitude(), hospital.getLongitude());

            double score = computeScore(donor, distance, request);
            matches.add(new DonorMatch(donor, distance, score));
        }

        matches.sort(Comparator.comparingDouble(DonorMatch::getScore).reversed());

        if (topK <= 0 || topK >= matches.size()) return matches;
        return new ArrayList<>(matches.subList(0, topK));
    }

    private double computeScore(Donor donor, double distance, BloodRequest request) {
        double distanceScore = Math.max(0, 100 - distance * 5);
        return distanceScore * 0.4
                + donor.getReliabilityScore() * 0.4
                + (donor.isEmergencyAvailable() ? 20 : 0)
                + request.getUrgency().getPriority() * 2;
    }

    /*
     * ========================================================
     * STATIC NESTED DATA CLASS
     * ========================================================
     */
    public static class DonorMatch {
        private final Donor donor;
        private final double distanceKm;
        private final double score;

        public DonorMatch(Donor donor, double distanceKm, double score) {
            this.donor = donor;
            this.distanceKm = distanceKm;
            this.score = score;
        }

        public Donor getDonor() { return donor; }
        public double getDistanceKm() { return distanceKm; }
        public double getScore() { return score; }

        @Override
        public String toString() {
            return String.format("%s | %.1f km | Score=%.1f | Reliability=%.0f%%",
                    donor.getName(), distanceKm, score, donor.getReliabilityScore());
        }
    }

    public List<BankMatch> locateBlood(String hospitalId, BloodGroup group,
                                       int minimumUnits)
            throws InvalidRequestException {
        Hospital hospital = hospitals.get(hospitalId);
        if (hospital == null) throw new InvalidRequestException("Hospital not found.");

        List<BankMatch> result = new ArrayList<>();
        for (BloodBank bank : bloodBanks.values()) {
            int stock = bank.getStock(group);
            if (stock < minimumUnits) continue;

            double distance = GeoUtils.distanceKm(
                    hospital.getLatitude(), hospital.getLongitude(),
                    bank.getLatitude(), bank.getLongitude());

            result.add(new BankMatch(bank, distance, stock));
        }

        result.sort(Comparator.comparingDouble(BankMatch::getDistanceKm));
        return result;
    }

    public static class BankMatch {
        private final BloodBank bank;
        private final double distanceKm;
        private final int availableUnits;

        public BankMatch(BloodBank bank, double distanceKm, int availableUnits) {
            this.bank = bank;
            this.distanceKm = distanceKm;
            this.availableUnits = availableUnits;
        }

        public BloodBank getBank() { return bank; }
        public double getDistanceKm() { return distanceKm; }
        public int getAvailableUnits() { return availableUnits; }

        @Override
        public String toString() {
            return String.format("%s | %.1f km | %d units",
                    bank.getName(), distanceKm, availableUnits);
        }
    }

    public void fulfillFromBank(String requestId, String bankId, int units)
            throws BloodConnectException {
        if (units <= 0)
            throw new InvalidRequestException("Units must be greater than zero.");

        BloodRequest request = requests.get(requestId);
        BloodBank bank = bloodBanks.get(bankId);

        if (request == null || bank == null)
            throw new InvalidRequestException("Request or blood bank not found.");

        if (request.getStatus() == RequestStatus.FULFILLED)
            throw new InvalidRequestException("Request is already fulfilled.");

        if (!bank.removeStock(request.getBloodGroup(), units))
            throw new InsufficientBloodException(
                    "Not enough " + request.getBloodGroup() + " blood in bank.");

        request.setStatus(units >= request.getQuantity()
                ? RequestStatus.FULFILLED
                : RequestStatus.PARTIALLY_FULFILLED);

        

        Hospital hospital = hospitals.get(request.getHospitalId());
        if (hospital != null)
            hospital.notifyUser("Request Update",
                    "Request " + requestId + " fulfilled with " + units + " unit(s).");

        audit("Fulfilled " + requestId + " using " + bankId);
    }

    public void donorRespond(String donorId, String requestId, boolean accept)
            throws BloodConnectException {
        Donor donor = donors.get(donorId);
        BloodRequest request = requests.get(requestId);

        if (donor == null || request == null)
            throw new InvalidRequestException("Donor or request not found.");

        

        donor.recordEmergencyResponse(accept);

        if (accept) {
            request.setStatus(RequestStatus.MATCHED);
            donor.notifyUser("Emergency", "You accepted request " + requestId);
        } else {
            donor.notifyUser("You declined request " + requestId);
        }
    }

    public void transfer(String sourceId, String destinationId,
                         BloodGroup group, int quantity)
            throws BloodConnectException {
        if (quantity <= 0)
            throw new InvalidRequestException("Quantity must be greater than zero.");

        BloodBank source = bloodBanks.get(sourceId);
        BloodBank destination = bloodBanks.get(destinationId);

        if (source == null || destination == null)
            throw new InvalidRequestException("Source or destination bank not found.");

        if (!source.removeStock(group, quantity))
            throw new InsufficientBloodException("Source bank does not have enough blood.");

        destination.addStock(group, quantity);

        

        audit("Transferred " + group + " x" + quantity);
    }

    public List<Searchable> search(String keyword) {
        List<Searchable> result = new ArrayList<>();
        addMatches(result, donors.values(), keyword);
        addMatches(result, hospitals.values(), keyword);
        addMatches(result, bloodBanks.values(), keyword);
        addMatches(result, patients.values(), keyword);
        addMatches(result, requests.values(), keyword);
        return result;
    }

    // GENERIC HELPER METHOD
    private <T extends Searchable> void addMatches(
            List<Searchable> result, Collection<T> items, String keyword) {
        for (T item : items)
            if (item.matches(keyword)) result.add(item);
    }

    public Map<BloodGroup, Integer> getSystemInventory() {
        Map<BloodGroup, Integer> total = new EnumMap<>(BloodGroup.class);
        for (BloodGroup group : BloodGroup.values()) total.put(group, 0);

        for (BloodBank bank : bloodBanks.values()) {
            for (BloodGroup group : BloodGroup.values())
                total.merge(group, bank.getStock(group), Integer::sum);
        }
        return total;
    }

    public List<BloodGroup> getCriticalGroups(int threshold) {
        List<BloodGroup> result = new ArrayList<>();
        for (Map.Entry<BloodGroup, Integer> e : getSystemInventory().entrySet())
            if (e.getValue() <= threshold) result.add(e.getKey());
        return result;
    }


    private double distanceBetween(String id1, String id2) {
        Object a = entityData(id1), b = entityData(id2);
        double[] p = coordinates(a), q = coordinates(b);
        return GeoUtils.distanceKm(p[0], p[1], q[0], q[1]);
    }

    private double[] coordinates(Object object) {
        if (object instanceof Donor d) return new double[]{d.getLatitude(), d.getLongitude()};
        if (object instanceof Hospital h) return new double[]{h.getLatitude(), h.getLongitude()};
        if (object instanceof BloodBank b) return new double[]{b.getLatitude(), b.getLongitude()};
        return new double[]{0, 0};
    }
    private Object entityData(String id) {
        if (donors.containsKey(id)) return donors.get(id);
        if (hospitals.containsKey(id)) return hospitals.get(id);
        if (bloodBanks.containsKey(id)) return bloodBanks.get(id);
        if (patients.containsKey(id)) return patients.get(id);
        if (requests.containsKey(id)) return requests.get(id);
        return null;
    }

    public Map<String, Donor> getDonors() { return Collections.unmodifiableMap(donors); }
    public Map<String, Hospital> getHospitals() { return Collections.unmodifiableMap(hospitals); }
    public Map<String, BloodBank> getBloodBanks() { return Collections.unmodifiableMap(bloodBanks); }
    public Map<String, Patient> getPatients() { return Collections.unmodifiableMap(patients); }
    public Map<String, BloodRequest> getRequests() { return Collections.unmodifiableMap(requests); }
    /* ========================================================
       EMERGENCY REQUEST COLLECTION
       ========================================================
       COLLECTIONS + GENERICS:
       Returns a typed List containing only emergency requests.
       BloodRequest.isEmergency() identifies CRITICAL and
       EMERGENCY requests.
       ======================================================== */
    public List<BloodRequest> getEmergencyRequests() {
        List<BloodRequest> emergency = new ArrayList<>();

        for (BloodRequest request : requests.values()) {
            if (request.isEmergency()) {
                emergency.add(request);
            }
        }

        return Collections.unmodifiableList(emergency);
    }

    public List<String> getAuditLog() { return Collections.unmodifiableList(auditLog); }
}
