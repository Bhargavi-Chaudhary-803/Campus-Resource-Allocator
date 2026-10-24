# AGENTS.md

This document defines architectural standards, development guidelines, and operational constraints for AI agents interacting with or extending the **Campus Resource Allocation Simulator** repository.

---

## 1. Environment & Tooling Prerequisites

- **Java Version**: Java 17 or later (LTS 17, 21, or modern Java 26 supported). Use standard Java APIs; do not introduce external framework annotations (no Spring, Lombok, Hibernate, or third-party DI).
- **Build System**: Apache Maven 3.8+. A Maven Wrapper (`mvnw` and `mvnw.cmd`) is included in the project root.
- **Testing Framework**: JUnit 5 (Jupiter API & Engine).
- **GUI Framework**: Java Swing (`javax.swing.*`, `java.awt.*`).

---

## 2. Standard Build & Test Commands

Always verify changes by executing tests and packaging the application before completing tasks:

```powershell
# Run the entire test suite
.\mvnw.cmd test

# Compile and package the standalone executable JAR
.\mvnw.cmd clean package

# Test-run the executable JAR in headless verification mode
java "-Djava.awt.headless=true" -jar target/campus-resource-simulator.jar

# Run the desktop UI application
java -jar target/campus-resource-simulator.jar
```

---

## 3. Package Structure & Architectural Boundaries

```
com.campus.simulator
├── App.java                              # Bootstrap entrypoint (must remain lean)
├── SimulationContext.java                # Dependency Injection and singleton assembly
├── model/                                # Pure domain entities (NO Swing imports)
│   ├── schedule/                         # TimeSlot and time intervals
│   ├── resource/                         # Resource hierarchy and capability interfaces
│   ├── requester/                        # Requester hierarchy and priority rules
│   └── allocation/                       # ResourceRequest, Allocation, AllocationResult
├── service/                              # Application services and use cases
│   └── allocation/                       # Strategy pattern engine and implementations
├── repository/                           # Data access interfaces and in-memory stores
├── observer/                             # DomainEvent publisher and subscriber mechanism
├── exception/                            # Domain-specific unchecked runtime exceptions
├── util/                                 # General defensive guards and formatting
└── ui/                                   # Presentation layer (Java Swing only)
    ├── component/                        # Reusable widgets (UITheme, CardPanel, StatusBadgeRenderer)
    ├── controller/                       # Presentation controllers mediating between UI and services
    └── view/                             # Panels and MainFrame layout
```

### Architectural Guardrails
1. **Model Independence**: Classes in `com.campus.simulator.model` and its subpackages **must NEVER import any Swing or AWT classes** (except pure date/time or value abstractions).
2. **Layered Separation**: The UI must never directly query or mutate repositories. All UI actions must route through controllers and services.
3. **Threading & EDT**: Simulation execution and heavy computations must not run on the Swing Event Dispatch Thread (EDT). Use `SwingWorker` or asynchronous executors, updating UI components via `SwingUtilities.invokeLater`.
4. **Dependency Inversion**: Services must depend on interfaces (`ResourceRepository`, `AllocationStrategy`), not concrete in-memory or Swing classes.

---

## 4. Naming Conventions & Code Style

- **Classes**: PascalCase (`AllocationService`, `PriorityFirstAllocationStrategy`, `TimeSlot`).
- **Interfaces**: PascalCase, named as capabilities or repository abstractions (`Bookable`, `Maintainable`, `ResourceRepository`).
- **Methods**: camelCase, intention-revealing business terms (`markUnderMaintenance()`, `calculateEffectivePriority()`, `isActiveDuring()`). Avoid generic getter/setter mutations for domain transitions.
- **Constants & Enums**: UPPER_SNAKE_CASE (`AVAILABLE`, `PRIORITY_FIRST`, `CRITICAL`).
- **Typography & UI**: The application standardizes on the bundled **Inter** font with tight spacing and clean, neutral, subtle styling. Do not introduce bright neon palettes or heavy third-party UI dependencies.

---

## 5. Protected Files & Folders

Do not modify or delete the following without explicit user instructions:
- `src/main/resources/fonts/Inter.ttf`: The official bundled Inter TrueType font.
- `.mvn/wrapper/`: Maven wrapper binary and property configurations.
- `pom.xml`: Plugin configurations for `maven-compiler-plugin`, `maven-surefire-plugin`, and `maven-shade-plugin` ensuring standalone JAR generation.
