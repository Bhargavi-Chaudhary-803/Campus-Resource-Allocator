# Campus Resource Allocation Simulator

> **A Pure Java Desktop Project for College Evaluation & Viva Demonstration**
> Designed with crystal-clear **Object-Oriented Programming (OOP)** principles, modern software design patterns, and a clean, subtle **Java Swing** user interface in a blue & off-white aesthetic with tight-spaced Inter typography.
> **Zero build frameworks required** â€” no Maven, no Gradle, no external dependencies. Compiles and runs out-of-the-box with standard Java 17+.

---

## ðŸ“‘ Table of Contents
1. [Project Overview](#1-project-overview)
2. [Quick Start (Build & Run)](#2-quick-start-build--run)
3. [Key Features & Screens](#3-key-features--screens)
4. [Project Architecture & Package Structure](#4-project-architecture--package-structure)
5. [Explicit Demonstration of OOP Principles](#5-explicit-demonstration-of-oop-principles)
6. [Design Patterns Implemented](#6-design-patterns-implemented)
7. [Core Java Concepts Demonstrated](#7-core-java-concepts-demonstrated)
8. [Automated Verification Test Suite](#8-automated-verification-test-suite)
9. [Viva / Oral Examination Questions & Answers](#9-viva--oral-examination-questions--answers)

---

## 1. Project Overview

On any university campus, limited facilitiesâ€”lecture halls, advanced research laboratories, auditoriums, and portable audiovisual equipmentâ€”face intense competition from academic departments, tenured and junior faculty, student clubs, and event organizers.

The **Campus Resource Allocation Simulator** models this real-world operational challenge:
- Detects **double-booking** and **schedule collisions** across overlapping time slots.
- Enforces **institutional safety policies** (e.g., student clubs are barred from booking hazardous biosafety laboratories without faculty oversight).
- Allows campus administrators to switch between **three algorithmic allocation strategies** at runtime to see how different rules affect fairness and facility utilization.
- Generates and exports official **audit reports** to text files for administrative record-keeping.

---

## 2. Quick Start (Build & Run)

### System Requirements
- **Java Development Kit (JDK) 17 or higher** (tested on Java 17, 21, and 26).
- Standard terminal / command prompt.

### Windows (One-Click Scripts)
```cmd
# 1. Compile the application
compile.bat

# 2. Launch the desktop GUI
run.bat

# 3. Run the automated OOP test suite
test.bat
```

### Linux / macOS
```bash
# Make scripts executable
chmod +x compile.sh run.sh test.sh

# 1. Compile
./compile.sh

# 2. Launch GUI
./run.sh

# 3. Run test suite
./test.sh
```

### Manual Command-Line (Cross-Platform)
```bash
# Compile to 'bin' directory
javac -encoding UTF-8 -d bin -sourcepath src src/com/campus/Main.java src/com/campus/VerificationTest.java

# Run Desktop Application
java -cp bin com.campus.Main

# Run Automated Verification Suite
java -cp bin com.campus.VerificationTest
```

---

## 3. Key Features & Screens

1. **Dashboard (`DashboardPanel`)**:
   - Executive telemetry metrics: Total Resources, Registered Entities, Total Requests, Allocated Bookings, Waitlist/Conflicts, and Facility Utilization %.
   - Live table of approved allocations with calculated operating costs and time windows.

2. **Campus Resource Catalog (`ResourcePanel`)**:
   - Full inventory of Lecture Halls, Research Labs, Auditoriums, and AV Equipment.
   - Filter by type and real-time keyword search.
   - **Toggle Maintenance**: Instantly mark an asset under scheduled maintenance (blocking future bookings).
   - **Add Resource**: Dialog utilizing `ResourceFactory` to instantiate concrete assets polymorphically.

3. **Requester Directory (`RequesterPanel`)**:
   - View departments, faculty members, and student clubs with their institutional priority tiers and active allocation loads.
   - **Register Requester**: Dialog with category-specific attributes (e.g., Department Code, Faculty Academic Rank, Club Advisor).

4. **Request Queue (`RequestPanel`)**:
   - Inspect competing facility reservations.
   - **Submit Request**: Form validating date, start/end hours, seat capacity, urgency, and event purpose.
   - **Reset Queue to Pending**: Resets requests back to pending status for fresh evaluation.

5. **Simulation Engine & Strategy Lab (`SimulationPanel`)**:
   - Runtime strategy switcher (`Priority-First`, `Fairness-Aware`, `Capacity-Optimized`).
   - Detailed algorithm explanation card explaining the mathematics and logic.
   - **Background Multithreaded Execution**: Runs on a background worker with an animated progress bar to prevent UI freezing.
   - Comprehensive conflict log detailing exact collision reasons (schedule clash, maintenance block, capacity deficiency, or safety clearance violation).

6. **Audit & Reporting (`ReportPanel`)**:
   - Summary statistics (operating expenses, allocation success rate).
   - Monospace formatted audit log preview.
   - **Export to File**: Writes official audit records to `reports/simulation_audit_report.txt` via Java File I/O.

---

## 4. Project Architecture & Package Structure

```
campus-resource-simulator/
â”œâ”€â”€ compile.bat                  # Windows one-click compile script
â”œâ”€â”€ run.bat                      # Windows one-click run script
â”œâ”€â”€ test.bat                     # Windows one-click test script
â”œâ”€â”€ compile.sh                   # Linux/macOS compile script
â”œâ”€â”€ run.sh                       # Linux/macOS run script
â”œâ”€â”€ test.sh                      # Linux/macOS test script
â”œâ”€â”€ resources/
â”‚   â””â”€â”€ Inter.ttf                # Bundled clean typography
â”œâ”€â”€ reports/                     # Output directory for exported audit files
â””â”€â”€ src/
    â””â”€â”€ com/
        â””â”€â”€ campus/
            â”œâ”€â”€ Main.java                        # GUI Desktop Bootstrap Entrypoint
            â”œâ”€â”€ VerificationTest.java            # Automated 5-part OOP Test Suite
            â”‚
            â”œâ”€â”€ model/                           # PURE DOMAIN LAYER
            â”‚   â”œâ”€â”€ Bookable.java                # Capability interface
            â”‚   â”œâ”€â”€ ResourceType.java            # Enum: CLASSROOM, LAB, AUDITORIUM, EQUIPMENT
            â”‚   â”œâ”€â”€ PriorityLevel.java           # Enum: LOW, NORMAL, HIGH, URGENT
            â”‚   â”œâ”€â”€ RequestStatus.java           # Enum: PENDING, ALLOCATED, WAITLISTED, REJECTED
            â”‚   â”œâ”€â”€ RequesterType.java           # Enum: DEPARTMENT, FACULTY, STUDENT_CLUB
            â”‚   â”œâ”€â”€ TimeSlot.java                # Immutable interval value object (encapsulation)
            â”‚   â”œâ”€â”€ Resource.java                # Abstract base class for physical assets
            â”‚   â”œâ”€â”€ Classroom.java               # Subclass: Whiteboards, projectors, room no.
            â”‚   â”œâ”€â”€ Laboratory.java              # Subclass: Lab domain, safety clearance flag
            â”‚   â”œâ”€â”€ Auditorium.java              # Subclass: Pro-audio, stage dimensions
            â”‚   â”œâ”€â”€ Equipment.java               # Subclass: Portable projectors, laptop carts
            â”‚   â”œâ”€â”€ Requester.java               # Abstract base class for campus actors
            â”‚   â”œâ”€â”€ Department.java              # Subclass: Institutional rank (Base score 60)
            â”‚   â”œâ”€â”€ FacultyMember.java           # Subclass: Academic rank (Base score 45)
            â”‚   â”œâ”€â”€ StudentClub.java             # Subclass: Safety restriction (Base score 25)
            â”‚   â”œâ”€â”€ AllocationRequest.java       # Composition: Requester, TimeSlot, specs
            â”‚   â”œâ”€â”€ Allocation.java              # Composition: Resource + Request + TimeSlot
            â”‚   â””â”€â”€ ResourceFactory.java         # Factory Method pattern
            â”‚
            â”œâ”€â”€ strategy/                        # STRATEGY PATTERN ALGORITHMS
            â”‚   â”œâ”€â”€ AllocationStrategy.java      # Strategy interface
            â”‚   â”œâ”€â”€ PriorityStrategy.java        # Strategy 1: Institutional priority & urgency
            â”‚   â”œâ”€â”€ FairnessStrategy.java        # Strategy 2: Balances load across requesters
            â”‚   â””â”€â”€ CapacityStrategy.java        # Strategy 3: Best-Fit seat capacity optimizer
            â”‚
            â”œâ”€â”€ service/                         # APPLICATION & DATA SERVICES
            â”‚   â”œâ”€â”€ CampusDatabase.java          # Singleton in-memory database with seed data
            â”‚   â”œâ”€â”€ SimulationEngine.java        # Collision detection & policy coordinator
            â”‚   â”œâ”€â”€ FileReportService.java       # File I/O report generator (PrintWriter)
            â”‚   â”œâ”€â”€ ResourceUnavailableException.java # Custom domain exception
            â”‚   â””â”€â”€ InvalidBookingException.java      # Custom domain exception
            â”‚
            â””â”€â”€ gui/                             # PRESENTATION LAYER (JAVA SWING)
                â”œâ”€â”€ UITheme.java                 # Blue & Off-White palette + Inter font
                â”œâ”€â”€ DashboardPanel.java          # Metric cards and confirmed bookings
                â”œâ”€â”€ ResourcePanel.java           # Resource catalog & maintenance toggle
                â”œâ”€â”€ RequesterPanel.java          # Requester directory & priority listing
                â”œâ”€â”€ RequestPanel.java            # Request submission queue
                â”œâ”€â”€ SimulationPanel.java         # Strategy selection & execution lab
                â”œâ”€â”€ ReportPanel.java             # System audit & file export panel
                â””â”€â”€ MainFrame.java               # Modern shell window with left sidebar
```

---

## 5. Explicit Demonstration of OOP Principles

Every core Object-Oriented principle is visibly demonstrated in the code and marked with comments:

| OOP Principle | Where It Is Implemented | How It Works |
|---|---|---|
| **Encapsulation** | `TimeSlot.java`, `Resource.java`, `AllocationRequest.java` | All fields are `private final` or protected. Setters enforce validation (e.g., `endHour > startHour`, `capacity > 0`). Immutable state where appropriate. |
| **Abstraction** | `Bookable.java`, `Resource.java`, `Requester.java` | Abstract classes define common interfaces (`isAvailable()`, `book()`, `release()`) and declare abstract methods (`calculateOperatingCostPerHour()`, `canBook()`) without exposing internal mechanics. |
| **Inheritance** | `Classroom`, `Laboratory`, `Auditorium`, `Equipment` extending `Resource`<br>`Department`, `FacultyMember`, `StudentClub` extending `Requester` | Hierarchical code reuse through `super(...)`. Shared invariants (ID, name, capacity, email) are managed uniformly by the base class. |
| **Polymorphism** | `Resource.calculateOperatingCostPerHour()`<br>`Requester.canBook(Resource)`<br>`Requester.getBasePriorityScore()` | **Dynamic Method Dispatch**: Each resource calculates energy/operational costs differently (e.g. Lab charges \$75/hr due to chemicals, Classroom \$20/hr). `StudentClub.canBook()` forbids booking hazardous biosafety labs, whereas `Department.canBook()` permits it. |
| **Composition** | `AllocationRequest.java`, `Allocation.java` | *"Favor Composition Over Inheritance"*: `AllocationRequest` **has-a** `Requester`, **has-a** `TimeSlot`, and **has-a** `PriorityLevel`. `Allocation` binds a `Resource` and `AllocationRequest`. |
| **Interface Segregation** | `Bookable.java` | Small, focused contract containing only `isAvailable()`, `book()`, and `release()`. |

---

## 6. Design Patterns Implemented

### 1. Strategy Pattern (`com.campus.strategy`)
- **Interface**: `AllocationStrategy` defines `prioritizeRequests(...)` and `selectBestResource(...)`.
- **Concrete Strategies**:
  - `PriorityStrategy`: Sorts by combined institutional score (Department: 60, Faculty: 45, Club: 25 + urgency weight).
  - `FairnessStrategy`: Applies a penalty to entities holding prior allocations to prevent starvation of smaller clubs.
  - `CapacityStrategy`: Selects the "Best-Fit" resource minimizing empty seats.
- **Engine**: `SimulationEngine` holds a reference to `AllocationStrategy` and can switch strategies dynamically at runtime (`engine.setStrategy(...)`).

### 2. Factory Method Pattern (`ResourceFactory.java`)
- Centralizes creation of polymorphic `Resource` subclasses (`Classroom`, `Laboratory`, `Auditorium`, `Equipment`).
- Client UI code never directly calls concrete constructors, ensuring loose coupling.

### 3. Singleton Pattern (`CampusDatabase.java`)
- `getInstance()` guarantees exactly one central in-memory repository instance.
- Pre-loaded with realistic university seed data (11 resources, 7 requesters, 8 requests with intentional conflicts).

---

## 7. Core Java Concepts Demonstrated

1. **Java Collections Framework**: `List<Resource>`, `ArrayList`, `Collections.unmodifiableList(...)` preventing unauthorized list modification.
2. **Streams & Lambdas**: Used extensively for filtering candidate resources, computing totals, checking schedule overlaps, and sorting.
3. **Custom Exceptions**: `ResourceUnavailableException` and `InvalidBookingException` extending `RuntimeException` for clean error signaling.
4. **Multithreading & Concurrency**: Simulation execution runs in a background thread via `SwingWorker` with animated progress feedback, keeping the Swing UI 100% responsive without freezing.
5. **File Input/Output (I/O)**: `FileReportService` exports audit logs to disk using `FileWriter`, `PrintWriter`, and **try-with-resources** to prevent file descriptor leaks.
6. **Value Objects**: `TimeSlot` overrides `equals()`, `hashCode()`, and `toString()` with interval arithmetic (`overlaps(...)`).

---

## 8. Automated Verification Test Suite

A standalone test suite (`VerificationTest.java`) is included to verify all functionality without needing external dependencies:

```cmd
test.bat
```

### Test Suite Execution Output:
```
================================================================================
       CAMPUS RESOURCE ALLOCATION SIMULATOR - AUTOMATED TEST SUITE
================================================================================
[TEST 1/5] Verifying Singleton Database & Seed Inventory... PASSED! (11 resources, 7 requesters, 8 requests loaded)
[TEST 2/5] Verifying OOP Polymorphism (Operating Costs & Policy Clearance)... PASSED! (Polymorphic cost & policy clearance functioning)
[TEST 3/5] Verifying Priority-First Strategy Execution & Collision Detection... PASSED! (Allocated: 4, Waitlisted: 3, Rejected: 1)
[TEST 4/5] Verifying Strategy Pattern Runtime Switching (Fairness & Capacity)... PASSED! (All strategies executed cleanly)
[TEST 5/5] Verifying File I/O Audit Report Generation... PASSED! (File generated at: reports\test_verification_report.txt, size: 2623 bytes)
================================================================================
SUMMARY: 5 / 5 TESTS PASSED SUCCESSFULLY (100% GREEN)
================================================================================
```

---

## 9. Viva / Oral Examination Questions & Answers

**Q1: How is Polymorphism demonstrated in this project?**
> *Answer:* Through dynamic method dispatch. For instance, `Resource.calculateOperatingCostPerHour()` is overridden by `Classroom`, `Laboratory`, `Auditorium`, and `Equipment` to compute distinct operating costs. Also, `Requester.canBook(Resource)` allows `StudentClub` to reject bookings for hazardous laboratories, while `Department` permits it.

**Q2: What is the benefit of the Strategy Pattern here?**
> *Answer:* It encapsulates different allocation algorithms (`PriorityStrategy`, `FairnessStrategy`, `CapacityStrategy`) behind the `AllocationStrategy` interface. The `SimulationEngine` depends only on the abstraction, allowing new algorithms to be added without modifying the engine (Open/Closed Principle).

**Q3: How do you prevent UI freezing when running simulations?**
> *Answer:* We use Java's `SwingWorker` to execute `SimulationEngine.runSimulation()` on a background worker thread while updating the `JProgressBar` on the Event Dispatch Thread (EDT).

**Q4: How does the application detect schedule collisions?**
> *Answer:* The `TimeSlot` value object implements interval arithmetic: two slots on the same date overlap if `(start1 < end2) && (start2 < end1)`. Before confirming any booking, the engine checks all active allocations for the same physical asset.

---
*Created for academic demonstration and object-oriented software engineering evaluation.*
