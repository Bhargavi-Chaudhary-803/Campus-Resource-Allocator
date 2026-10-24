# Architecture & Design Blueprint

This document details the software architecture, design patterns, object-oriented principles, and allocation workflow of the **Campus Resource Allocation Simulator**.

---

## 1. Package Diagram (Text Form)

```
+---------------------------------------------------------------------------------------+
|                                    PRESENTATION LAYER                                 |
|  +--------------------+   +---------------------+   +-------------------------------+ |
|  |     MainFrame      |-->|     UI Views        |-->|        UI Controllers         | |
|  | (Shell Navigation) |   | (Dashboard, Resource|   | (Mediates requests & events)  | |
|  |                    |   |  Requests, Sim, etc)|   |                               | |
|  +--------------------+   +---------------------+   +-------------------------------+ |
|                             |                  |                                      |
|                             v                  v                                      |
|                     +---------------+  +-------------------+                          |
|                     |    UITheme    |  |StatusBadgeRenderer|                          |
|                     +---------------+  +-------------------+                          |
+---------------------------------------------------|-----------------------------------+
                                                    | (Invokes Service Use Cases)
                                                    v
+---------------------------------------------------------------------------------------+
|                                    APPLICATION LAYER                                  |
|  +---------------------------------------------------------------------------------+  |
|  |                     SimulationContext (DI & Service Assembly)                   |  |
|  +---------------------------------------------------------------------------------+  |
|       |                     |                     |                    |              |
|       v                     v                     v                    v              |
|  +-------------+     +---------------+    +----------------+    +-------------------+ |
|  |ResourceSvc  |     | RequesterSvc  |    |  AllocationSvc |    |   SimulationSvc   | |
|  +-------------+     +---------------+    +----------------+    +-------------------+ |
|       |                     |                     |                    |              |
|       |                     |                     v                    |              |
|       |                     |             +-----------------+          |              |
|       |                     |             |AllocationStrategy<---------+              |
|       |                     |             +-----------------+                         |
|       |                     |               |             |                           |
|       |                     |               v             v                           |
|       |                     |     [PriorityFirst]  [FairnessAware] [CapacityOptimized]|
|       v                     v                     v                    v              |
|  +---------------------------------------------------------------------------------+  |
|  |             DomainEventPublisher (Publishes to Observers & UI Views)            |  |
|  +---------------------------------------------------------------------------------+  |
+---------------------------------------|-----------------------------------------------+
                                        | (Coordinates Repositories & Domain Entities)
                                        v
+---------------------------------------------------------------------------------------+
|                                     DOMAIN LAYER                                      |
|  +------------------+   +-------------------+   +------------------+   +------------+ |
|  |     Resource     |   |     Requester     |   | ResourceRequest  |   | Allocation | |
|  | (Classroom, Lab, |   | (Dept, Faculty,   |   | (Urgency, State, |   | (Bound     | |
|  |  Projector, etc) |   |  Club, Organizer) |   |  TimeSlot)       |   |  interval) | |
|  +------------------+   +-------------------+   +------------------+   +------------+ |
|       ^                         ^                                                     |
|       | Capability Interfaces   | Polymorphic Rules                                   |
|  [Bookable, Maintainable,       [canRequestResource(),                                |
|   CapacityProvider, Portable]    getAllocationPriority()]                             |
+---------------------------------------|-----------------------------------------------+
                                        | (Persisted In-Memory)
                                        v
+---------------------------------------------------------------------------------------+
|                                    DATA ACCESS LAYER                                  |
|  +---------------------------------------------------------------------------------+  |
|  |                Repository Abstractions (ResourceRepository, etc)                |  |
|  +---------------------------------------------------------------------------------+  |
|       |                          |                        |                  |        |
|       v                          v                        v                  v        |
|  [InMemoryResourceRepo]   [InMemoryRequesterRepo]  [InMemoryRequestRepo] [InMemoryAlc]|
+---------------------------------------------------------------------------------------+
```

---

## 2. Class Responsibilities

| Layer | Class / Interface | Responsibility |
|---|---|---|
| **Domain** | `Resource` | Abstract root entity modeling campus physical assets. Encapsulates status, capacity, and invariants. |
| **Domain** | `Classroom`, `Laboratory`, `Projector`, `LaptopSet`, `SportsFacility`, `StudyRoom` | Concrete resource types with specialized attributes (e.g. BSL safety levels, portability, display monitors). |
| **Domain** | `Bookable`, `Maintainable`, `CapacityProvider`, `PortableResource` | Segregated capability interfaces exposing specific operational contracts. |
| **Domain** | `Requester` | Abstract base entity for campus actors with polymorphic priority and permission checking. |
| **Domain** | `Department`, `FacultyMember`, `StudentClub`, `EventOrganizer` | Concrete requester types defining distinct institutional priorities and resource access rights. |
| **Domain** | `TimeSlot` | Immutable value object record enforcing `startTime < endTime` and providing overlap/containment arithmetic. |
| **Domain** | `ResourceRequest` | Mutable domain entity with a finite state machine (`PENDING` → `APPROVED`, `WAITLISTED`, `REJECTED`, `CANCELLED`, `COMPLETED`). |
| **Domain** | `Allocation` | Entity binding a `Resource` to a `ResourceRequest` during a confirmed `TimeSlot`. |
| **Domain** | `AllocationResult` | Rich outcome report containing diagnostic explanation, evaluated candidates, and conflict reasons. |
| **Engine** | `AllocationStrategy` | Core strategy abstraction defining the `allocate()` and `getRequestComparator()` contract. |
| **Engine** | `PriorityFirstAllocationStrategy` | Prioritizes requests by effective priority with chronological tie-breakers. |
| **Engine** | `FairnessAwareAllocationStrategy` | Progressively penalizes requesters with accumulated reservations to maintain campus equity. |
| **Engine** | `CapacityOptimizedAllocationStrategy` | Best-fit heuristic selecting the smallest viable resource to prevent capacity squandering. |
| **Repository** | `ResourceRepository`, etc. | Interface contracts for entity persistence and query filtering. |
| **Repository** | `InMemoryResourceRepository`, etc. | Thread-safe `ConcurrentHashMap` implementations of repository interfaces. |
| **Application** | `AllocationService` | Manages single allocation requests, cancellations, and early releases. |
| **Application** | `SimulationService` | Orchestrates batch multi-request simulations and environment resets. |
| **Application** | `ReportingService` | Computes utilization percentages, approval rates, and peak congestion time slots. |
| **Observer** | `DomainEventPublisher` | Thread-safe mediator dispatching events to registered UI listeners. |
| **Presentation**| `MainFrame` | Shell window managing sidebar navigation and card-layout screen switching. |
| **Presentation**| `SimulationController` | Dispatches asynchronous simulation runs via `SwingWorker` off the EDT. |

---

## 3. OOP Principle Mapping

### Encapsulation
- **Private/Final State**: All identity attributes (`id`, `createdAt`, `requester`, `timeSlot`) are declared `private final`.
- **Guarded Mutations**: Status changes (`markUnderMaintenance()`, `retire()`, `approve()`, `release()`) validate lifecycle preconditions and prohibit illegal transitions (e.g., cannot release a completed allocation).
- **Defensive Copying**: Collections returned by repositories and result builders use `List.copyOf()`.

### Abstraction
- Repositories (`ResourceRepository`, `RequesterRepository`) present clear query interfaces hiding in-memory data structures.
- Capability interfaces (`Maintainable`, `CapacityProvider`) allow services to interact with behaviors without coupling to concrete implementations.

### Inheritance
- Clean, shallow class hierarchies:
  - `Resource` → `Classroom`, `Laboratory`, `Projector`, `LaptopSet`, `SportsFacility`, `StudyRoom`.
  - `Requester` → `Department`, `FacultyMember`, `StudentClub`, `EventOrganizer`.
- Subclasses reuse common attributes and validation via `super(...)`.

### Polymorphism
- `Requester.getAllocationPriority()` returns dynamically computed priority weights based on institutional role.
- `Requester.canRequestResource(resource)` allows `StudentClub` to block unescorted access to BSL-2 laboratories while allowing `Department` access.
- `AllocationStrategy.allocate()` is dispatched polymorphically based on user selection.

### Composition
- `ResourceRequest` composes `Requester`, `TimeSlot`, and `ResourceType`.
- `Allocation` composes `Resource`, `ResourceRequest`, and `TimeSlot`.
- `PortableResource` capability is composed on hardware devices without forcing an unnatural inheritance tree.

### SOLID Principles
- **S (Single Responsibility)**: `TimeSlot` handles interval arithmetic; `AllocationService` coordinates reservations; `ReportingService` aggregates statistics; views handle rendering.
- **O (Open/Closed)**: Adding a new resource type, requester subclass, or allocation strategy requires zero modifications to existing allocation engine classes.
- **L (Liskov Substitution)**: Any concrete `Resource` or `Requester` can substitute for its base class in all engine algorithms.
- **I (Interface Segregation)**: Capabilities are partitioned into distinct interfaces (`Bookable`, `Maintainable`, `CapacityProvider`, `PortableResource`).
- **D (Dependency Inversion)**: High-level services depend on abstract repositories and strategies, not concrete implementations.

---

## 4. Design Pattern Mapping

| Pattern | Classes | Purpose |
|---|---|---|
| **Strategy** | `AllocationStrategy`, `PriorityFirstAllocationStrategy`, `FairnessAwareAllocationStrategy`, `CapacityOptimizedAllocationStrategy` | Encapsulates swappable resource matching algorithms. |
| **Factory Method** | `ResourceFactory`, `RequesterFactory`, `AllocationStrategyFactory` | Centralizes polymorphic instantiation logic. |
| **Repository** | `ResourceRepository`, `InMemoryResourceRepository`, etc. | Decouples business logic from physical storage mechanisms. |
| **Observer** | `DomainEventPublisher`, `DomainEventListener`, `ResourceAddedEvent`, etc. | Propagates domain state mutations to decoupled Swing views. |
| **Builder** | `ResourceRequestBuilder`, `AllocationResultBuilder` | Enables fluent, readable construction of complex objects. |
| **Singleton / DI Context** | `SimulationContext`, `DomainEventPublisher` | Manages shared application dependencies with constructor injection. |

---

## 5. Allocation Flow Explanation

The step-by-step lifecycle of an allocation run proceeds as follows:

```
[User Clicks "Run Allocation Simulation"]
                  │
                  ▼
[SimulationController (EDT)]
  │ Spawns background SwingWorker task
  ▼
[SimulationService (Worker Thread)]
  │
  ├─ 1. Retrieves all PENDING requests from ResourceRequestRepository.
  ├─ 2. Retrieves existing allocations from AllocationRepository.
  ├─ 3. Queries active AllocationStrategy for its custom Comparator.
  │     (e.g., FairnessAware penalizes requesters with heavy prior load).
  ├─ 4. Sorts pending requests in priority/fairness sequence.
  │
  ├─ 5. For each request, delegates to AllocationService.processRequest():
  │     │
  │     ├─ A. Checks requester eligibility (fails if inactive).
  │     ├─ B. Checks requester schedule collisions (prohibits double-booking).
  │     ├─ C. Evaluates candidate resources matching type & optional target ID:
  │     │     • Status must be AVAILABLE (skips MAINTENANCE / RETIRED).
  │     │     • Capacity must be >= requiredCapacity.
  │     │     • Requester.canRequestResource() must return true (checks safety policy).
  │     │     • Resource must not have overlapping active allocations in TimeSlot.
  │     │
  │     ├─ D. If viable candidates exist:
  │     │     • Strategy selects optimal resource (e.g., Best-Fit capacity).
  │     │     • Creates new Allocation entity.
  │     │     • Sets request status to APPROVED.
  │     │     • Saves Allocation and updated Request to repositories.
  │     │     • Publishes AllocationApprovedEvent.
  │     │
  │     └─ E. If no viable candidates exist:
  │           • If barred by safety policy → sets status to REJECTED.
  │           • If barred by schedule collision/capacity → sets status to WAITLISTED.
  │           • Saves diagnostic explanation and conflict details.
  │           • Publishes RequestStatusChangedEvent.
  │
  ├─ 6. Collects all AllocationResult objects.
  ├─ 7. Publishes SimulationCompletedEvent.
  ▼
[SimulationController.done() (EDT)]
  │ Updates UI table, diagnostics panel, and summary status label.
```
