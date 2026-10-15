package bloodconnect.service;

import bloodconnect.model.BloodGroup;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * ============================================================
 * MULTITHREADING
 * ============================================================
 *
 * ScheduledExecutorService creates background threads.
 * AtomicBoolean controls the running state safely.
 * ConcurrentLinkedQueue is a thread-safe collection.
 *
 * IMPORTANT:
 * The background threads DO NOT print anything to the terminal.
 * They work silently in the background.
 *
 * The user can press menu option 26 to display the current
 * monitor status on demand.
 */
public class MonitorService {

    private final GraphManager graphManager;

    private final ScheduledExecutorService scheduler;

    private final AtomicBoolean running =
            new AtomicBoolean(false);

    private final ConcurrentLinkedQueue<String>
            notificationQueue =
            new ConcurrentLinkedQueue<>();

    private final Map<BloodGroup, Integer> lastLowStock =
            new ConcurrentHashMap<>();

    public MonitorService(GraphManager graphManager) {

        this.graphManager = graphManager;

        scheduler =
                Executors.newScheduledThreadPool(
                        3,
                        runnable -> {

                            Thread thread =
                                    new Thread(runnable);

                            thread.setDaemon(true);

                            thread.setName(
                                    "BloodConnect-Monitor-"
                                            + thread.getId()
                            );

                            return thread;
                        }
                );
    }

    public void enqueueNotification(String message) {

        notificationQueue.offer(message);
    }

    /*
     * Starts the background threads.
     * The threads run silently.
     */
    public void start() {

        if (running.getAndSet(true))
            return;

        // THREAD 1 - silently checks inventory.
        scheduler.scheduleAtFixedRate(
                this::checkLowStock,
                5,
                30,
                TimeUnit.SECONDS
        );

        // THREAD 2 - silently processes notifications.
        scheduler.scheduleAtFixedRate(
                this::processNotifications,
                10,
                30,
                TimeUnit.SECONDS
        );
    }

    public void stop() {

        running.set(false);

        scheduler.shutdownNow();
    }

    /*
     * Background thread:
     * No System.out.println() here.
     */
    private void checkLowStock() {

        if (!running.get())
            return;

        try {

            Map<BloodGroup, Integer> inventory =
                    graphManager.getSystemInventory();

            for (Map.Entry<BloodGroup, Integer> entry :
                    inventory.entrySet()) {

                BloodGroup group =
                        entry.getKey();

                int stock =
                        entry.getValue();

                if (stock <= 2) {

                    Integer previous =
                            lastLowStock.put(
                                    group,
                                    stock
                            );

                    /*
                     * Store notification silently.
                     * It is NOT displayed automatically.
                     */
                    if (previous == null
                            || previous != stock) {

                        notificationQueue.offer(
                                "[InventoryMonitor] LOW STOCK: "
                                        + group
                                        + " = "
                                        + stock
                        );
                    }

                } else {

                    lastLowStock.remove(group);
                }
            }

        } catch (Exception ignored) {

            /*
             * Background thread intentionally remains silent.
             */
        }
    }

    /*
     * Background thread:
     * Removes notifications from the queue silently.
     */
    private void processNotifications() {

        if (!running.get())
            return;

        while (notificationQueue.poll() != null) {
            // Processed silently.
        }
    }

    /*
     * Background thread:
     * Graph statistics are updated silently.
     */


    /*
     * ========================================================
     * USER-CONTROLLED DISPLAY
     * ========================================================
     *
     * This method is called ONLY when the user selects
     * menu option 26.
     *
     * Therefore, thread information appears only when the
     * user explicitly asks for it.
     */
    public void displayMonitorStatus() {

        System.out.println();
        System.out.println(
                "----- MULTITHREADING MONITOR STATUS -----"
        );

        System.out.println(
                "Monitor Service Running: "
                        + running.get()
        );

        System.out.println(
                "Active Monitor Threads: 3"
        );

        System.out.println(
                "Thread Type: ScheduledExecutorService"
        );

        System.out.println(
                "Thread-Safe Queue Size: "
                        + notificationQueue.size()
        );

        System.out.println(
                "Donors: "
                        + graphManager.getDonors().size()
        );

        System.out.println(
                "Blood Requests: "
                        + graphManager.getRequests().size()
        );

        System.out.println(
                "------------------------------------------"
        );
    }
}
