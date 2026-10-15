BLOODCONNECT - PACKAGED JAVA OOP PROJECT
========================================

MAIN MENU OPTIONS
-----------------
1  Register Donor
2  Register Hospital
3  Register Blood Bank
4  Register Patient
5  Connect Hospital to Blood Bank
6  Record Blood Donation
7  Create Blood Request
8  Match Donors
9  Locate Blood
10 Fulfill Blood Request
12 Transfer Blood Between Banks
13 Search
22 Show Donors
23 Show Hospitals
24 Show Blood Banks
25 Show Blood Requests
26 Show Emergency Requests
0  Exit

NOTE: The unused original menu options are intentionally removed.
The application still contains the required OOP concepts.

CONCEPTS INCLUDED
-----------------
1. Packages
2. Inheritance
3. Abstraction
4. Interfaces
5. Method overriding
6. Method overloading
7. Static members
8. User-defined exceptions
9. Exception handling
10. Generics
11. Collections
12. Maps
13. Multithreading

PACKAGE STRUCTURE
-----------------
src/bloodconnect/app          -> Main application
src/bloodconnect/model        -> Entities, enums, interfaces
src/bloodconnect/service      -> Business logic and background threads
src/bloodconnect/graph        -> Generic graph and algorithms
src/bloodconnect/exception    -> User-defined exceptions
src/bloodconnect/util         -> Utility methods

COMPILE (Linux)
---------------
javac -d out $(find src -name "*.java")

RUN
---
java -cp out bloodconnect.app.BloodConnectCore

EMERGENCY REQUESTS
------------------
Option 26 displays requests with CRITICAL or EMERGENCY urgency.

THREADS
-------
MonitorService uses ScheduledExecutorService with three background
threads. They run silently so the terminal is not flooded. The thread
implementation remains part of the project for the OOP requirement.
