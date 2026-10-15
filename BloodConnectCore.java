package bloodconnect.app;

import bloodconnect.exception.*;
import bloodconnect.model.*;
import bloodconnect.service.*;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/*
 * ============================================================
 * BLOODCONNECT - PACKAGED OOP APPLICATION
 * ============================================================
 *
 * REQUIRED CONCEPTS USED IN THIS PROJECT:
 *
 * 1. PACKAGES
 *    Classes are separated into model, graph, service,
 *    exception, util and app packages.
 *
 * 2. INHERITANCE
 *    Donor/Hospital/BloodBank/Patient/BloodRequest
 *    extend the abstract Entity class.
 *
 * 3. ABSTRACTION
 *    Entity is abstract and contains abstract displayDetails().
 *
 * 4. INTERFACES
 *    GraphEntity, Searchable, Notifiable and GraphAlgorithm.
 *
 * 5. METHOD OVERRIDING
 *    Subclasses override displayDetails(), matches(),
 *    notifyUser(), toString(), etc.
 *
 * 6. METHOD OVERLOADING
 *    registerDonor(), matchDonors(), addNode(), addEdge(),
 *    displayDetails(), notifyUser() etc.
 *
 * 7. STATIC MEMBERS
 *    GraphManager.idSequence, GeoUtils methods/constants,
 *    GraphAlgorithms methods and main application objects.
 *
 * 8. USER-DEFINED EXCEPTIONS
 *    BloodConnectException and its subclasses.
 *
 * 9. EXCEPTION HANDLING
 *    try-catch in main and input methods + throws in services.
 *
 * 10. GENERICS
 *     Graph<T>, GraphNode<T>, GraphEdge<T>,
 *     GraphAlgorithm<T> and generic helper methods.
 *
 * 11. COLLECTIONS / MAPS
 *     ArrayList, LinkedHashMap, EnumMap, HashSet,
 *     PriorityQueue, Queue, ConcurrentLinkedQueue.
 *
 * 12. THREADS
 *     MonitorService uses ScheduledExecutorService and
 *     background threads for stock, notification and graph
 *     monitoring.
 *
 * COMPILE FROM PROJECT ROOT:
 *     javac -d out [all .java files under src]
 *
 * RUN:
 *     java -cp out bloodconnect.app.BloodConnectCore
 *
 * If your shell does not expand **, use:
 *     javac -d out $(find src -name "*.java")
 *
 * ============================================================
 */
public class BloodConnectCore {
    private static final Scanner scanner = new Scanner(System.in);
    private static final GraphManager graphManager = new GraphManager();
    private static final MonitorService monitors =
            new MonitorService(graphManager);

    public static void main(String[] args) {
        printBanner();
        monitors.start();

        boolean running = true;

        while (running) {
            try {
                printMenu();
                int choice = readInt("Enter your choice: ");
                System.out.println();
                switch (choice) {
                    case 1 -> registerDonorMenu();
                    case 2 -> registerHospitalMenu();
                    case 3 -> registerBloodBankMenu();
                    case 4 -> registerPatientMenu();
                    case 5 -> connectHospitalBankMenu();
                    case 6 -> recordDonationMenu();
                    case 7 -> createBloodRequestMenu();
                    case 8 -> matchDonorsMenu();
                    case 9 -> locateBloodMenu();
                    case 10 -> fulfillRequestMenu();
                    case 11 -> transferBloodMenu();
                    case 12 -> searchMenu();
                    case 13 -> showDonors();
                    case 14 -> showHospitals();
                    case 15 -> showBloodBanks();
                    case 16 -> showRequests();
                    case 17 -> showEmergencyRequestsMenu();
                    case 0 -> {
                        running = false;
                        System.out.println("Exiting BloodConnect...");
                    }
                    default -> System.out.println("Invalid choice.");
                }

            } catch (BloodConnectException e) {
                // USER-DEFINED EXCEPTION HANDLING
                System.out.println("\nBloodConnect Error: " + e.getMessage());

            } catch (Exception e) {
                // GENERAL EXCEPTION HANDLING
                System.out.println("\nERROR: " + e.getMessage());
            }

            if (running) {
                System.out.println("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }

        monitors.stop();
        scanner.close();
        System.out.println("\nThank you for using BloodConnect!");
    }

    private static void printBanner() {
        System.out.println("\n======================================================");
        System.out.println("              BLOODCONNECT SYSTEM");
        System.out.println("       Blood Donation & Hospital Network");
        System.out.println("======================================================");
    }

    /* ========================================================
       MAIN MENU - ONLY REQUESTED OPTIONS
       ========================================================
       The removed options are intentionally not shown.
       Option 26 is the new Emergency Requests option.
       ======================================================== */
    private static void printMenu() {
        System.out.println();
        System.out.println("---------------- MAIN MENU ----------------");
        System.out.println(" 1. Register Donor");
        System.out.println(" 2. Register Hospital");
        System.out.println(" 3. Register Blood Bank");
        System.out.println(" 4. Register Patient");
        System.out.println(" 5. Connect Hospital to Blood Bank");
        System.out.println(" 6. Record Blood Donation");
        System.out.println(" 7. Create Blood Request");
        System.out.println(" 8. Match Donors");
        System.out.println(" 9. Locate Blood");
        System.out.println("10. Fulfill Blood Request");
        System.out.println("11. Transfer Blood Between Banks");
        System.out.println("12. Search");
        System.out.println("13. Show Donors");
        System.out.println("14. Show Hospitals");
        System.out.println("15. Show Blood Banks");
        System.out.println("16. Show Blood Requests");
        System.out.println("17. Show Emergency Requests");
        System.out.println(" 0. Exit");
        System.out.println("--------------------------------------------");
    }

    private static void registerDonorMenu() throws DuplicateNodeException {
        String name = readString("Donor name: ");
        int age = readInt("Age: ");
        BloodGroup group = readBloodGroup();
        String location = readString("Location: ");
        double lat = readDouble("Latitude: ");
        double lon = readDouble("Longitude: ");

        Donor donor = graphManager.registerDonor(
                name, group, age, location, lat, lon);

        System.out.println("\nDonor registered successfully.");
        donor.displayDetails();
        System.out.println("Donor ID: " + donor.getId());
    }

    private static void registerHospitalMenu() throws DuplicateNodeException {
        String name = readString("Hospital name: ");
        String location = readString("Location: ");
        double lat = readDouble("Latitude: ");
        double lon = readDouble("Longitude: ");

        Hospital h = graphManager.registerHospital(name, location, lat, lon);
        System.out.println("\nHospital registered successfully.");
        h.displayDetails();
        System.out.println("Hospital ID: " + h.getId());
    }

    private static void registerBloodBankMenu() throws DuplicateNodeException {
        String name = readString("Blood bank name: ");
        String location = readString("Location: ");
        double lat = readDouble("Latitude: ");
        double lon = readDouble("Longitude: ");

        BloodBank b = graphManager.registerBloodBank(name, location, lat, lon);
        System.out.println("\nBlood bank registered successfully.");
        b.displayDetails();
        System.out.println("Blood Bank ID: " + b.getId());
    }

    private static void registerPatientMenu() throws BloodConnectException {
        if (graphManager.getHospitals().isEmpty()) {
            System.out.println("No hospitals available.");
            return;
        }

        String name = readString("Patient name: ");
        BloodGroup group = readBloodGroup();
        showHospitals();
        String hospitalId = readString("Hospital ID: ");

        Patient p = graphManager.registerPatient(name, group, hospitalId);
        System.out.println("\nPatient registered successfully.");
        p.displayDetails();
        System.out.println("Patient ID: " + p.getId());
    }

    private static void connectHospitalBankMenu() throws BloodConnectException {
        showHospitals();
        String h = readString("Hospital ID: ");
        showBloodBanks();
        String b = readString("Blood Bank ID: ");

        graphManager.connectHospitalToBank(h, b);
        System.out.println("Hospital and Blood Bank connected successfully.");
    }

    private static void recordDonationMenu() throws BloodConnectException {
        showDonors();
        String donor = readString("Donor ID: ");
        showBloodBanks();
        String bank = readString("Blood Bank ID: ");

        graphManager.recordDonation(donor, bank);
        System.out.println("Donation recorded successfully.");
    }

    private static void createBloodRequestMenu() throws BloodConnectException {
        showHospitals();
        String hospital = readString("Hospital ID: ");

        String patient = null;
        if (!graphManager.getPatients().isEmpty()) {
            showPatients();
            patient = readString("Patient ID (or press Enter to skip): ");
            if (patient.isBlank()) patient = null;
        }

        BloodGroup group = readBloodGroup();
        int quantity = readInt("Required quantity: ");
        Urgency urgency = readUrgency();

        BloodRequest r = graphManager.createRequest(
                hospital, patient, group, quantity, urgency);

        System.out.println("\nBlood request created successfully.");
        r.displayDetails();
        System.out.println("Request ID: " + r.getId());
    }

    private static void matchDonorsMenu() throws InvalidRequestException {
        showRequests();
        String request = readString("Request ID: ");
        int topK = readInt("How many top matches? (0 = all): ");

        List<GraphManager.DonorMatch> matches =
                topK == 0 ? graphManager.matchDonors(request)
                        : graphManager.matchDonors(request, topK);

        if (matches.isEmpty()) {
            System.out.println("No eligible donors found.");
            return;
        }

        System.out.println("\nMatched Donors:");
        int n = 1;
        for (GraphManager.DonorMatch m : matches)
            System.out.println((n++) + ". " + m);
    }

    private static void locateBloodMenu() throws InvalidRequestException {
        showHospitals();
        String hospital = readString("Hospital ID: ");
        BloodGroup group = readBloodGroup();
        int minimum = readInt("Minimum units required: ");

        List<GraphManager.BankMatch> matches =
                graphManager.locateBlood(hospital, group, minimum);

        if (matches.isEmpty()) {
            System.out.println("No blood bank has enough stock.");
            return;
        }

        System.out.println("\nNearest Blood Banks:");
        for (GraphManager.BankMatch m : matches)
            System.out.println("  " + m);
    }

    private static void fulfillRequestMenu() throws BloodConnectException {
        showRequests();
        String request = readString("Request ID: ");
        showBloodBanks();
        String bank = readString("Blood Bank ID: ");
        int units = readInt("Units to provide: ");

        graphManager.fulfillFromBank(request, bank, units);
        System.out.println("Request updated successfully.");
    }

    private static void donorResponseMenu() throws BloodConnectException {
        showDonors();
        String donor = readString("Donor ID: ");
        showRequests();
        String request = readString("Request ID: ");

        System.out.println("1. Accept");
        System.out.println("2. Decline");
        int choice = readInt("Choice: ");

        if (choice != 1 && choice != 2) {
            System.out.println("Invalid response.");
            return;
        }

        graphManager.donorRespond(donor, request, choice == 1);
        System.out.println(choice == 1
                ? "Donor accepted request."
                : "Donor declined request.");
    }

    private static void transferBloodMenu() throws BloodConnectException {
        showBloodBanks();
        String source = readString("Source Bank ID: ");
        String destination = readString("Destination Bank ID: ");
        BloodGroup group = readBloodGroup();
        int quantity = readInt("Quantity: ");

        graphManager.transfer(source, destination, group, quantity);
        System.out.println("Blood transfer completed.");
    }

    private static void searchMenu() {
        String keyword = readString("Enter search keyword: ");
        List<Searchable> results = graphManager.search(keyword);

        if (results.isEmpty()) {
            System.out.println("No matching records.");
            return;
        }

        System.out.println("\nSearch Results:");
        for (Searchable result : results)
            System.out.println("  " + result);
    }

    private static void inventoryMenu() {
        System.out.println("----- SYSTEM BLOOD INVENTORY -----");
        Map<BloodGroup, Integer> inventory =
                graphManager.getSystemInventory();

        for (Map.Entry<BloodGroup, Integer> e : inventory.entrySet())
            System.out.printf("  %-4s : %d units%n", e.getKey(), e.getValue());

        System.out.println("\nCritical Groups (<= 5 units):");
        List<BloodGroup> critical = graphManager.getCriticalGroups(5);
        if (critical.isEmpty()) System.out.println("  None");
        else for (BloodGroup g : critical) System.out.println("  " + g);
    }


    private static void auditLogMenu() {
        if (graphManager.getAuditLog().isEmpty()) {
            System.out.println("No audit records.");
            return;
        }
        for (String entry : graphManager.getAuditLog())
            System.out.println("  " + entry);
    }

    private static void showDonors() {
        System.out.println("\nAvailable Donors:");
        if (graphManager.getDonors().isEmpty()) {
            System.out.println("  No donors registered.");
            return;
        }

        for (Donor d : graphManager.getDonors().values())
            System.out.printf("  ID=%s | %s | %s | Age=%d | Eligible=%s | Location=%s%n",
                    d.getId(), d.getName(), d.getBloodGroup(),
                    d.getAge(), d.isEligible(), d.getLocation());
    }

    private static void showHospitals() {
        System.out.println("\nAvailable Hospitals:");
        if (graphManager.getHospitals().isEmpty()) {
            System.out.println("  No hospitals registered.");
            return;
        }

        for (Hospital h : graphManager.getHospitals().values())
            System.out.printf("  ID=%s | %s | %s%n",
                    h.getId(), h.getName(), h.getLocation());
    }

    private static void showBloodBanks() {
        System.out.println("\nAvailable Blood Banks:");
        if (graphManager.getBloodBanks().isEmpty()) {
            System.out.println("  No blood banks registered.");
            return;
        }

        for (BloodBank b : graphManager.getBloodBanks().values())
            System.out.printf("  ID=%s | %s | %s | Total Units=%d%n",
                    b.getId(), b.getName(), b.getLocation(), b.getTotalUnits());
    }

    private static void showRequests() {
        System.out.println("\nBlood Requests:");
        if (graphManager.getRequests().isEmpty()) {
            System.out.println("  No blood requests.");
            return;
        }

        for (BloodRequest r : graphManager.getRequests().values())
            System.out.printf("  ID=%s | Blood=%s | Qty=%d | Urgency=%s | Status=%s | Hospital=%s%n",
                    r.getId(), r.getBloodGroup(), r.getQuantity(),
                    r.getUrgency(), r.getStatus(), r.getHospitalId());
    }

    /* ========================================================
       26. SHOW EMERGENCY REQUESTS
       ========================================================
       Displays only requests whose urgency is CRITICAL or
       EMERGENCY. BloodRequest determines this using the
       isEmergency() method.
       ======================================================== */
    private static void showEmergencyRequests() {
        System.out.println("\nEmergency Blood Requests:");

        List<BloodRequest> emergencyRequests =
                graphManager.getEmergencyRequests();

        if (emergencyRequests.isEmpty()) {
            System.out.println("  No emergency requests found.");
            return;
        }

        int number = 1;
        for (BloodRequest request : emergencyRequests) {
            System.out.printf(
                    "  %d. ID=%s | Blood=%s | Qty=%d | Urgency=%s | Status=%s | Hospital=%s%n",
                    number++,
                    request.getId(),
                    request.getBloodGroup(),
                    request.getQuantity(),
                    request.getUrgency(),
                    request.getStatus(),
                    request.getHospitalId()
            );
        }
    }

    private static void showPatients() {
        for (Patient p : graphManager.getPatients().values())
            System.out.printf("  ID=%s | %s | Blood=%s | Hospital=%s%n",
                    p.getId(), p.getName(), p.getBloodGroup(), p.getHospitalId());
    }

    /* ========================================================
       17. SHOW EMERGENCY REQUESTS
       ======================================================== */

    private static void showEmergencyRequestsMenu() {
        System.out.println("----- EMERGENCY BLOOD REQUESTS -----");

        boolean found = false;
        int number = 1;

        for (BloodRequest request :
                graphManager.getRequests().values()) {

            /*
             * EMERGENCY REQUEST FILTER:
             * CRITICAL and EMERGENCY requests are identified
             * by the BloodRequest emergency flag.
             */
            if (request.isEmergency()) {
                found = true;

                System.out.printf(
                        "%d. ID=%s | Blood=%s | Qty=%d | Urgency=%s | Status=%s | Hospital=%s%n",
                        number++,
                        request.getId(),
                        request.getBloodGroup(),
                        request.getQuantity(),
                        request.getUrgency(),
                        request.getStatus(),
                        request.getHospitalId()
                );
            }
        }

        if (!found)
            System.out.println("No emergency blood requests.");
    }


    private static String readString(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    /*
     * ========================================================
     * EXCEPTION HANDLING
     * ========================================================
     * NumberFormatException is a built-in exception handled here.
     */
    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static BloodGroup readBloodGroup() {
        BloodGroup[] groups = BloodGroup.values();

        while (true) {
            System.out.println("\nBlood Groups:");
            for (int i = 0; i < groups.length; i++)
                System.out.println("  " + (i + 1) + ". " + groups[i]);

            int choice = readInt("Choose blood group: ");
            if (choice >= 1 && choice <= groups.length)
                return groups[choice - 1];

            System.out.println("Invalid blood group choice.");
        }
    }

    private static Urgency readUrgency() {
        Urgency[] urgencies = Urgency.values();

        while (true) {
            System.out.println("\nUrgency:");
            for (int i = 0; i < urgencies.length; i++)
                System.out.println("  " + (i + 1) + ". " + urgencies[i]);

            int choice = readInt("Choose urgency: ");
            if (choice >= 1 && choice <= urgencies.length)
                return urgencies[choice - 1];

            System.out.println("Invalid urgency choice.");
        }
    }
}
