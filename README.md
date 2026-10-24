# Campus Resource Allocation Simulator

A clean, production-grade Java desktop application simulating the automated allocation of limited university campus resources (such as lecture halls, computer laboratories, BSL-2 biochemistry facilities, portable 4K projectors, laptop carts, sports stadiums, and quiet study pods) to competing academic departments, faculty researchers, student clubs, and event organizers.

Built strictly with **Java 17+ (tested on Java 26)**, **JUnit 5**, and a subtle, functional **Java Swing** desktop user interface featuring tightly spaced **Inter typography**.

---

## Table of Contents
1. [Project Purpose & Vision](#project-purpose--vision)
2. [How to Build & Run](#how-to-build--run)
3. [Core Features](#core-features)
4. [Package Architecture](#package-architecture)
5. [Demonstration of OOP & SOLID Principles](#demonstration-of-oop--solid-principles)
6. [Design Patterns Implemented](#design-patterns-implemented)
7. [Allocation Engine Strategies](#allocation-engine-strategies)
8. [Testing & Verification](#testing--verification)
9. [Future Extensions](#future-extensions)

---

## 1. Project Purpose & Vision

University campuses possess diverse physical assets with varying capacities, safety clearance requirements, and operational readiness. Simultaneously, diverse campus entities—ranging from accredited academic departments to student cultural clubs—compete for overlapping time slots.

This project simulates how campus resource dispatching can be resolved deterministically using swappable algorithmic strategies:
- **Priority-First**: Respects institutional seniority and immediate request urgency with FIFO tie-breaking.
- **Fairness-Aware**: Applies a progressive fairness penalty to entities that have already secured substantial campus resources, allowing unserviced clubs and junior faculty an equitable share.
- **Capacity-Optimized**: Implements a Best-Fit heuristic to minimize seat and space waste, ensuring a 150-seat lecture hall is not squandered on a 20-person review session when a 25-person seminar room is available.

The system is designed as an extensible simulator running entirely in-memory behind repository and capability abstractions, allowing persistent SQL, JPA, or document repositories to be plugged in without changing domain business logic.

---

## 2. How to Build & Run

### Prerequisites
- **JDK 17 or later** (JDK 17, 21, or 26)
- **Apache Maven 3.8+** (or use the bundled `./mvnw` / `mvnw.cmd` wrapper)

### Command-Line Instructions

#### Compile & Run Tests
```bash
# Using standard Maven
mvn clean test

# Or using the Maven Wrapper (Windows PowerShell)
.\mvnw.cmd clean test

# Linux / macOS
./mvnw clean test
```

#### Package Standalone Executable JAR
```bash
mvn clean package
# Or: .\mvnw.cmd clean package
```

#### Launch Desktop Simulator
```bash
java -jar target/campus-resource-simulator.jar
```

---

## 3. Core Features

- **Executive Telemetry Dashboard**: Real-time metric cards reporting total campus assets, available operational units, active reservations, waitlisted demands, and resource utilization percentages alongside recent allocations.
- **Interactive Asset Management**: Manage Classrooms, Laboratories, Projectors, Laptop Sets, Sports Facilities, and Study Rooms. Dynamically mark assets under scheduled maintenance or return them to active service.
- **University Requester Registry**: Manage Academic Departments, Tenured/Assistant Faculty, Student Organizations, and Event Committees. Toggle active/inactive eligibility or adjust institutional priority tiers.
- **Request Submission & State Machine**: Submit reservations with date, time, duration, required capacity, purpose, and urgency. Controlled lifecycle transitions (`PENDING` → `APPROVED`, `WAITLISTED`, `REJECTED`, `CANCELLED`, `COMPLETED`).
- **Multi-Strategy Simulation Engine**: Compare `Priority-First`, `Fairness-Aware`, and `Capacity-Optimized` algorithms. Non-blocking asynchronous execution via `SwingWorker` with full conflict diagnostics and candidate evaluation logs.
- **Analytical Capacity Reports**: Demand breakdowns by requester classification, asset category demand indices, and top peak congestion time intervals.
- **Instant Data Reset**: One-click re-seeding with realistic demo assets, requesters, and deliberate scheduling collisions.

---

## 4. Package Architecture

```
com.campus.simulator
├── App.java                              # Main bootstrap entrypoint
├── SimulationContext.java                # Top-level DI and service assembly
│
├── model/                                # Pure domain entities and rules
│   ├── schedule/
│   │   └── TimeSlot.java                 # Immutable interval value object
│   ├── resource/
│   │   ├── Bookable.java                 # Capability interface
│   │   ├── Maintainable.java             # Capability interface
│   │   ├── CapacityProvider.java         # Capability interface
│   │   ├── PortableResource.java         # Capability interface
│   │   ├── Resource.java                 # Abstract base class
│   │   ├── ResourceStatus.java           # AVAILABLE, MAINTENANCE, RETIRED
│   │   ├── ResourceType.java             # 6 Campus asset types
│   │   ├── Classroom.java                # Concrete resource
│   │   ├── Laboratory.java               # Concrete resource (safety clearances)
│   │   ├── Projector.java                # Concrete resource (portable)
│   │   ├── LaptopSet.java                # Concrete resource (portable)
│   │   ├── SportsFacility.java           # Concrete resource
│   │   ├── StudyRoom.java                # Concrete resource
│   │   └── ResourceFactory.java          # Factory Method pattern
│   ├── requester/
│   │   ├── Requester.java                # Abstract base class
│   │   ├── PriorityTier.java             # CRITICAL, HIGH, MEDIUM, LOW
│   │   ├── RequesterType.java            # DEPARTMENT, FACULTY, CLUB, EVENT
│   │   ├── Department.java               # High institutional priority
│   │   ├── FacultyMember.java            # Academic priority
│   │   ├── StudentClub.java              # Standard priority (safety restrictions)
│   │   ├── EventOrganizer.java           # Attendance-scaled priority
│   │   └── RequesterFactory.java         # Factory Method pattern
│   └── allocation/
│       ├── RequestPriority.java          # LOW, NORMAL, HIGH, URGENT
│       ├── RequestStatus.java            # Lifecycle state machine enum
│       ├── AllocationStatus.java         # ACTIVE, RELEASED, COMPLETED, CANCELLED
│       ├── AllocationOutcome.java        # APPROVED, WAITLISTED, REJECTED
│       ├── ResourceRequest.java          # Domain request entity
│       ├── ResourceRequestBuilder.java   # Builder pattern
│       ├── Allocation.java               # Reservation binding entity
│       ├── AllocationResult.java         # Diagnostic result value object
│       └── AllocationResultBuilder.java  # Builder pattern
│
├── service/                              # Application use cases & orchestration
│   ├── ResourceService.java              # Resource inventory use cases
│   ├── RequesterService.java             # Requester enrollment use cases
│   ├── ResourceRequestService.java       # Scheduling request use cases
│   ├── AllocationService.java            # Strategy execution & release use cases
│   ├── SimulationService.java            # Batch simulation orchestrator
│   ├── ReportingService.java             # Analytics & capacity metrics
│   ├── SampleDataSeeder.java             # Baseline demo data generator
│   └── allocation/
│       ├── AllocationStrategy.java       # Strategy interface
│       ├── AllocationStrategyType.java   # Enum of strategies
│       ├── AbstractAllocationStrategy.java # Shared validation & collision detection
│       ├── PriorityFirstAllocationStrategy.java
│       ├── FairnessAwareAllocationStrategy.java
│       ├── CapacityOptimizedAllocationStrategy.java
│       └── AllocationStrategyFactory.java # Factory Method pattern
│
├── repository/                           # Data access abstractions & storage
│   ├── ResourceRepository.java
│   ├── RequesterRepository.java
│   ├── ResourceRequestRepository.java
│   ├── AllocationRepository.java
│   ├── InMemoryResourceRepository.java   # Thread-safe ConcurrentHashMap storage
│   ├── InMemoryRequesterRepository.java
│   ├── InMemoryResourceRequestRepository.java
│   └── InMemoryAllocationRepository.java
│
├── observer/                             # Domain event pub-sub mechanism
│   ├── DomainEvent.java                  # Event interface
│   ├── DomainEventListener.java          # Functional subscriber interface
│   ├── DomainEventPublisher.java         # Thread-safe event dispatcher
│   ├── ResourceAddedEvent.java
│   ├── ResourceStatusChangedEvent.java
│   ├── ResourceRemovedEvent.java
│   ├── RequesterAddedEvent.java
│   ├── RequestSubmittedEvent.java
│   ├── RequestStatusChangedEvent.java
│   ├── AllocationApprovedEvent.java
│   ├── AllocationReleasedEvent.java
│   ├── SimulationCompletedEvent.java
│   └── SimulationResetEvent.java
│
├── exception/                            # Custom domain unchecked exceptions
│   ├── CampusSimulatorException.java
│   ├── ValidationException.java
│   ├── InvalidRequestException.java
│   ├── ResourceUnavailableException.java
│   ├── DuplicateResourceException.java
│   ├── AllocationConflictException.java
│   └── EntityNotFoundException.java
│
├── util/                                 # Domain utilities
│   ├── ValidationUtils.java              # Defensive programming guards
│   ├── DateTimeUtils.java                # Interval formatting
│   └── IdGenerator.java                  # Thread-safe sequential IDs
│
└── ui/                                   # Presentation layer (Java Swing)
    ├── component/
    │   ├── UITheme.java                  # Neutral styling, Inter typography
    │   ├── CardPanel.java                # Clean container component
    │   └── StatusBadgeRenderer.java      # Rounded badge table cell renderer
    ├── controller/
    │   ├── DashboardController.java
    │   ├── ResourceController.java
    │   ├── RequesterController.java
    │   ├── RequestController.java
    │   ├── SimulationController.java     # SwingWorker asynchronous runner
    │   └── ReportController.java
    └── view/
        ├── MainFrame.java                # Shell window with sidebar navigation
        ├── DashboardPanel.java           # Overview screen
        ├── ResourcesPanel.java           # Asset management screen
        ├── RequestersPanel.java          # Requester management screen
        ├── RequestsPanel.java            # Request submission screen
        ├── SimulationPanel.java          # Strategy simulation & audit screen
        └── ReportsPanel.java             # Capacity & demand analytics screen
```

---

## 5. Demonstration of OOP & SOLID Principles

### 1. Encapsulation
- All fields in domain models are private and final wherever possible.
- Identity fields (IDs, creation timestamps) expose no public setters.
- State modifications occur exclusively through business methods (`markUnderMaintenance()`, `retire()`, `approve()`, `release()`, `waitlist()`).
- Collections returned from domain objects and repositories are defensively copied (`List.copyOf()`).

### 2. Abstraction
- Repositories (`ResourceRepository`, `RequesterRepository`, `AllocationRepository`) hide in-memory `ConcurrentHashMap` details behind clean interfaces.
- Capability interfaces (`Bookable`, `Maintainable`, `CapacityProvider`, `PortableResource`) decouple consumption from concrete types.
- The UI layer communicates strictly with application controllers, never interacting with storage structures.

### 3. Inheritance
- Meaningful "is-a" hierarchy rooted in `Resource` (with `Classroom`, `Laboratory`, `Projector`, `LaptopSet`, `SportsFacility`, `StudyRoom`).
- Requester hierarchy rooted in `Requester` (with `Department`, `FacultyMember`, `StudentClub`, `EventOrganizer`).
- Constructors invoke `super(...)` to enforce consistent validation of universal invariants.

### 4. Polymorphism
- Dynamic method dispatch via `Requester.getAllocationPriority()` produces distinct institutional rankings.
- Policy enforcement via `Requester.canRequestResource(resource)` allows `StudentClub` to automatically forbid booking BSL-2 laboratories without hardcoded type checks in the engine.
- Allocation strategies (`AllocationStrategy`) are swapped dynamically at runtime without restarting or recompiling.

### 5. Composition over Inheritance
- `ResourceRequest` composes `Requester`, `TimeSlot`, and `ResourceType`.
- `Allocation` composes `Resource`, `ResourceRequest`, and `TimeSlot`.
- `PortableResource` capability is composed onto specific resources (`Projector`, `LaptopSet`) rather than forcing a complex multiple-inheritance hierarchy.

### 6. SOLID Principles
- **Single Responsibility Principle (SRP)**: Each class has one clear concern. `TimeSlot` handles time intervals; `AllocationService` coordinates reservations; `ReportingService` computes metrics; UI views handle presentation.
- **Open/Closed Principle (OCP)**: New allocation strategies, resource types, or requesters can be added without modifying the core allocation engine.
- **Liskov Substitution Principle (LSP)**: All subclasses honor parent contracts. Any `Resource` subclass can be booked, maintained, or evaluated without violating expected behavior.
- **Interface Segregation Principle (ISP)**: Capabilities are segregated into small interfaces (`Maintainable`, `CapacityProvider`, `PortableResource`, `Bookable`).
- **Dependency Inversion Principle (DIP)**: High-level services depend on abstractions (`ResourceRepository`, `AllocationStrategy`), not concrete implementations.

---

## 6. Design Patterns Implemented

1. **Strategy Pattern**: `AllocationStrategy` interface with `PriorityFirstAllocationStrategy`, `FairnessAwareAllocationStrategy`, and `CapacityOptimizedAllocationStrategy`.
2. **Factory Method / Simple Factory**: `ResourceFactory`, `RequesterFactory`, and `AllocationStrategyFactory`.
3. **Repository Pattern**: `ResourceRepository`, `RequesterRepository`, `ResourceRequestRepository`, `AllocationRepository` with thread-safe in-memory implementations.
4. **Observer Pattern**: `DomainEvent`, `DomainEventListener`, and `DomainEventPublisher` notifying UI views of background mutations.
5. **Builder Pattern**: `ResourceRequestBuilder` and `AllocationResultBuilder` for readable, expressive object construction.
6. **Singleton Pattern**: Application-level `SimulationContext` and `DomainEventPublisher` providing structured service discovery with constructor injection.

---

## 7. Allocation Engine Strategies

| Strategy | Selection Algorithm | Best Used For |
|---|---|---|
| **Priority-First** | Evaluates requests sorted by effective priority (institutional rank + request urgency) with chronological tie-breaker (FIFO). | Academic peak scheduling, exam halls, critical accreditation events. |
| **Fairness-Aware** | Calculates a dynamic fairness score that progressively penalizes requesters holding prior allocations: `score = priority / (1 + (activeAllocations * weight))`. | Broad campus equity, student activity spaces, balancing multi-club access. |
| **Capacity-Optimized** | Evaluates viable candidate resources and selects the best-fit unit minimizing `(capacity - requiredCapacity) >= 0`. | Preventing resource squandering (e.g., preserving large halls for large cohorts). |

---

## 8. Testing & Verification

A comprehensive JUnit 5 test suite with **27 automated tests** covers:
- Value object boundaries (`TimeSlotTest`: interval overlaps, abutting boundaries, negative durations).
- Resource model polymorphism & capability lifecycle (`ResourceModelTest`).
- Requester priority hierarchy and safety clearance rules (`RequesterModelTest`).
- State machine transition guards (`ResourceRequestTest`).
- In-memory repository concurrency & interval queries (`RepositoryTest`).
- Collision detection, double-booking prevention, and maintenance blocks (`AllocationConflictDetectionTest`).
- Proving distinct allocation decisions across different strategies (`AllocationStrategyTest`).
- End-to-end service orchestration, reporting, and event dispatch (`AllocationServiceWorkflowTest`).

Run all tests via:
```bash
mvn test
# or
.\mvnw.cmd test
```

---

## 9. Future Extensions

- **Database Persistence**: Drop-in PostgreSQL / SQLite JDBC repository implementations without modifying any domain model or service class.
- **Calendar Visualization**: Week-grid visual schedule view showing room allocations across time blocks.
- **Recurring Bookings**: Support for weekly recurring semester schedules.
- **Export Capabilities**: Export allocation audit reports and conflict diagnostics to CSV or PDF format.
