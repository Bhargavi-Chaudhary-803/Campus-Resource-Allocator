# AGENTS.md

This document defines architectural standards, development guidelines, and operational constraints for AI agents interacting with or extending the **Campus Resource Allocation Simulator** repository.

---

## 1. Project Philosophy & Build Constraints

- **Zero Heavy Build Frameworks**: **No Maven, No Gradle, No Spring, No Lombok, No Hibernate**. The project is strictly designed as a lightweight, clean, standard Java desktop application for college project evaluation.
- **Java Version**: Java 17 or later (LTS 17, 21, or 26 supported). Use standard Java SE libraries (`java.base`, `java.desktop`).
- **Compilation**: Use standard `javac` targeting the `bin` directory.
- **Execution**: Use standard `java -cp bin com.campus.Main`.
- **Testing**: Standalone test suite in `com.campus.VerificationTest`.
- **GUI Framework**: Pure Java Swing (`javax.swing.*`, `java.awt.*`).

---

## 2. Standard Build & Test Commands

Agents should verify changes using the standard scripts or command-line commands:

### Windows
```powershell
# Compile the application
.\compile.bat

# Run the automated OOP test suite
.\test.bat

# Launch the desktop UI application
.\run.bat
```

### Direct Terminal Commands (Cross-Platform)
```bash
# Compile all source files
javac -encoding UTF-8 -d bin -sourcepath src src/com/campus/Main.java src/com/campus/VerificationTest.java

# Run test suite
java -cp bin com.campus.VerificationTest

# Launch desktop GUI
java -cp bin com.campus.Main
```

---

## 3. Package Structure & Boundaries

```
src/com/campus/
├── Main.java                        # GUI Desktop Bootstrap Entrypoint
├── VerificationTest.java            # Standalone 5-Suite Automated Tester
│
├── model/                           # Pure domain entities (NO Swing imports!)
│   ├── Bookable.java                # Capability interface
│   ├── Resource.java                # Abstract base class
│   ├── Classroom.java               # Concrete resource subclass
│   ├── Laboratory.java              # Concrete resource subclass
│   ├── Auditorium.java              # Concrete resource subclass
│   ├── Equipment.java               # Concrete resource subclass
│   ├── Requester.java               # Abstract base class
│   ├── Department.java              # Concrete requester subclass
│   ├── FacultyMember.java           # Concrete requester subclass
│   ├── StudentClub.java             # Concrete requester subclass
│   ├── TimeSlot.java                # Immutable interval value object
│   ├── AllocationRequest.java       # Request lifecycle entity
│   ├── Allocation.java              # Reservation binding entity
│   ├── ResourceFactory.java         # Factory Method pattern
│   └── (Enums: ResourceType, RequesterType, PriorityLevel, RequestStatus)
│
├── strategy/                        # Strategy Pattern implementations
│   ├── AllocationStrategy.java      # Strategy interface
│   ├── PriorityStrategy.java        # Institutional priority algorithm
│   ├── FairnessStrategy.java        # Anti-starvation fairness algorithm
│   └── CapacityStrategy.java        # Best-Fit space optimizer
│
├── service/                         # Application use cases & in-memory database
│   ├── CampusDatabase.java          # Singleton in-memory store with demo seed data
│   ├── SimulationEngine.java        # Core collision detection & policy orchestrator
│   ├── FileReportService.java       # Java File I/O audit exporter
│   ├── ResourceUnavailableException.java
│   └── InvalidBookingException.java
│
└── gui/                             # Presentation layer (Java Swing only)
    ├── UITheme.java                 # Blue & Off-White styling, Inter typography
    ├── DashboardPanel.java          # Overview metrics & confirmed bookings
    ├── ResourcePanel.java           # Asset catalog & maintenance toggle
    ├── RequesterPanel.java          # Requester directory & priority listing
    ├── RequestPanel.java            # Request submission queue
    ├── SimulationPanel.java         # Strategy selection & execution lab
    ├── ReportPanel.java             # System audit & file export panel
    └── MainFrame.java               # Frame layout with left sidebar
```

---

## 4. Architectural Guardrails for Agents

1. **Model Independence**: Classes in `com.campus.model` **must NEVER import any Swing or AWT classes**. Pure Java domain models only.
2. **Layered Separation**: The GUI panels must never contain allocation business rules or collision algorithms. All scheduling and collision logic resides in `SimulationEngine`.
3. **No External Libraries**: Do not add external `.jar` files or Maven `pom.xml` files unless explicitly requested by the user.
4. **Visual Aesthetics**: Adhere to the subtle Blue & Off-White color palette (`#F8FAFC`, `#FFFFFF`, `#2563EB`, `#1E3A8A`) and tightly spaced Inter font provided in `UITheme.java`.

---

## 5. Protected Files & Folders

Do not delete or overwrite without explicit reason:
- `resources/Inter.ttf`: The bundled Inter font.
- `compile.bat`, `run.bat`, `test.bat`: Windows helper scripts.
- `compile.sh`, `run.sh`, `test.sh`: Linux/macOS helper scripts.
