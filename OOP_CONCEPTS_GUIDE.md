# 🎓 College Viva & OOP Concepts Guide

> **Campus Resource Allocation Simulator**
> *A quick reference cheat-sheet for oral examinations, viva voce, and professor code evaluations.*

---

## 📌 1. Object-Oriented Programming (OOP) Principles

### 1.1 Encapsulation
* **Definition**: Bundling data and methods that operate on that data within a single unit, and restricting direct external access to prevent unintended state corruption.
* **Where to find in Code**:
  - `src/com/campus/model/TimeSlot.java`
  - `src/com/campus/model/Resource.java`
  - `src/com/campus/model/AllocationRequest.java`
* **How It Is Implemented**:
  - Fields are declared `private` or `private final` (e.g. `TimeSlot.date`, `TimeSlot.startHour`).
  - Validation guards in constructors prevent illegal states (e.g., throwing `IllegalArgumentException` if `endHour <= startHour` or `capacity <= 0`).
  - Immutability: `TimeSlot` is an immutable value object with no setters.
  - Controlled state transitions: `AllocationRequest` only changes status via `markAllocated()`, `markWaitlisted()`, `markRejected()`, or `resetToPending()`.

---

### 1.2 Abstraction
* **Definition**: Hiding complex internal implementation details and exposing only essential interfaces and behaviors to the outside world.
* **Where to find in Code**:
  - `src/com/campus/model/Bookable.java` (Interface)
  - `src/com/campus/model/Resource.java` (Abstract Class)
  - `src/com/campus/model/Requester.java` (Abstract Class)
  - `src/com/campus/strategy/AllocationStrategy.java` (Interface)
* **How It Is Implemented**:
  - The `Bookable` interface exposes three essential lifecycle operations: `isAvailable()`, `book()`, and `release()`. Any class implementing `Bookable` can participate in reservations without client code knowing its internal mechanics.
  - The `Resource` abstract class defines the blueprint for physical assets and leaves concrete details to abstract methods:
    ```java
    public abstract String getDetails();
    public abstract double calculateOperatingCostPerHour();
    ```

---

### 1.3 Inheritance
* **Definition**: Mechanism where a new subclass derives attributes and behaviors from an existing superclass, facilitating code reuse and establishing an *"is-a"* relationship.
* **Where to find in Code**:
  - **Resource Hierarchy**:
    - `Resource` (Base Class)
      - ↳ `Classroom` (extends Resource)
      - ↳ `Laboratory` (extends Resource)
      - ↳ `Auditorium` (extends Resource)
      - ↳ `Equipment` (extends Resource)
  - **Requester Hierarchy**:
    - `Requester` (Base Class)
      - ↳ `Department` (extends Requester)
      - ↳ `FacultyMember` (extends Requester)
      - ↳ `StudentClub` (extends Requester)
* **How It Is Implemented**:
  - Subclasses invoke `super(id, name, ...)` in their constructors to reuse common validation and field initialization.
  - Subclasses add specialized domain attributes (e.g., `Laboratory` adds `requiresSafetyClearance`, `Auditorium` adds `hasProAudio`).

---

### 1.4 Polymorphism
* **Definition**: The ability of different objects to respond to the same method call in different ways based on their actual runtime type (Dynamic Method Dispatch).
* **Where to find in Code**:
  1. **Polymorphic Cost Calculation**:
     - `Classroom.calculateOperatingCostPerHour()`: returns `$20.00`
     - `Laboratory.calculateOperatingCostPerHour()`: returns `$75.00` (due to specialized fume hoods & equipment)
     - `Auditorium.calculateOperatingCostPerHour()`: returns `$150.00` (due to high-powered lighting & acoustics)
     - `Equipment.calculateOperatingCostPerHour()`: returns `$10.00`
  2. **Polymorphic Policy Clearance (`canBook`)**:
     - `Department.canBook(Resource r)`: returns `true` (departments have institutional authority to book any campus asset).
     - `StudentClub.canBook(Resource r)`: dynamically inspects if the resource is a `Laboratory` requiring safety clearance; if so, returns `false` (barring unescorted student access to hazardous chemical/bio labs).
  3. **Polymorphic Priority Scoring**:
     - `Department.getBasePriorityScore()`: returns `60`
     - `FacultyMember.getBasePriorityScore()`: returns `45`
     - `StudentClub.getBasePriorityScore()`: returns `25`

---

### 1.5 Composition over Inheritance
* **Definition**: *"Favor object composition over class inheritance"*: assembling complex objects by combining references to other objects (*"has-a"* relationship) rather than creating deep inheritance trees.
* **Where to find in Code**:
  - `src/com/campus/model/AllocationRequest.java`:
    - **Has-a** `Requester`
    - **Has-a** `TimeSlot`
    - **Has-a** `ResourceType`
    - **Has-a** `PriorityLevel`
  - `src/com/campus/model/Allocation.java`:
    - **Has-a** `Resource`
    - **Has-a** `AllocationRequest`
    - **Has-a** `TimeSlot`

---

## 🛠️ 2. Software Design Patterns

### 2.1 Strategy Pattern
* **Purpose**: Defines a family of interchangeable algorithms, encapsulates each one, and makes them swappable at runtime.
* **Where to find in Code**:
  - Interface: `src/com/campus/strategy/AllocationStrategy.java`
  - Concrete Strategy 1: `src/com/campus/strategy/PriorityStrategy.java`
  - Concrete Strategy 2: `src/com/campus/strategy/FairnessStrategy.java`
  - Concrete Strategy 3: `src/com/campus/strategy/CapacityStrategy.java`
  - Context / Consumer: `src/com/campus/service/SimulationEngine.java`
* **Viva Explanation**:
  > *"The `SimulationEngine` depends only on the `AllocationStrategy` interface, not concrete classes. In the GUI's Simulation tab, when a user selects 'Fairness-Aware Strategy', `engine.setStrategy(new FairnessStrategy())` is called dynamically without modifying the engine's core code. This honors the Open/Closed Principle."*

---

### 2.2 Factory Method Pattern
* **Purpose**: Creates objects without specifying the exact concrete class to be instantiated, delegating creation to a specialized method.
* **Where to find in Code**:
  - `src/com/campus/model/ResourceFactory.java`
* **Viva Explanation**:
  > *"When the user adds a new resource in the UI, `ResourceFactory.createResource(type, ...)` evaluates a `switch` on `ResourceType` to instantiate a `Classroom`, `Laboratory`, `Auditorium`, or `Equipment`. The UI layer never needs to import or couple directly to concrete resource constructors."*

---

### 2.3 Singleton Pattern
* **Purpose**: Ensures a class has only one instance and provides a global point of access to it.
* **Where to find in Code**:
  - `src/com/campus/service/CampusDatabase.java`
* **How It Is Implemented**:
  - Private constructor `private CampusDatabase()` prevents direct `new CampusDatabase()` calls.
  - Static instance `private static CampusDatabase instance`.
  - Thread-safe access via `public static synchronized CampusDatabase getInstance()`.

---

## ☕ 3. Core Java Concepts Demonstrated

| Concept | File / Location | Academic Explanation |
|---|---|---|
| **Collections Framework** | `CampusDatabase.java`, `SimulationEngine.java` | Uses generic `List<Resource>`, `ArrayList`, and `Collections.unmodifiableList(...)` for immutable query views. |
| **Streams & Lambdas** | `SimulationEngine.java`, `DashboardPanel.java` | Uses declarative filter, map, and count pipelines (e.g., `requests.stream().filter(r -> r.getStatus() == RequestStatus.ALLOCATED).count()`). |
| **Custom Exceptions** | `ResourceUnavailableException.java`, `InvalidBookingException.java` | Extends `RuntimeException` for clean domain error propagation without cluttered throws clauses. |
| **Multithreading** | `SimulationPanel.java` (via `SwingWorker`) | Background worker executes simulation calculations asynchronously while animating a `JProgressBar`, keeping the Swing Event Dispatch Thread (EDT) responsive. |
| **File I/O** | `FileReportService.java` | Uses `PrintWriter`, `FileWriter`, and Java 7+ **try-with-resources** (`try (PrintWriter w = ...)`), guaranteeing automatic file closure even if an exception occurs. |
| **Value Objects** | `TimeSlot.java` | Overrides `equals()`, `hashCode()`, and `toString()`. Implements interval intersection arithmetic: `(s1 < e2 && s2 < e1)`. |

---

## 🎤 4. Common Professor Questions & Ideal Answers

**Q: Why did you not use an external SQL database or framework like Spring Boot?**
> **A:** *"This project was specifically designed to demonstrate pure Java fundamental concepts and object-oriented architecture without hiding logic behind framework magic or annotations. All state, validation, and design patterns are implemented from scratch, making the code 100% transparent and educational."*

**Q: How does the Fairness-Aware strategy prevent starvation?**
> **A:** *"In a pure priority system, high-tier departments could monopolize all prime rooms, leaving student clubs starved of space. The `FairnessStrategy` applies a penalty based on `requester.getAllocationCount()`. As an entity receives more bookings, its effective bidding score temporarily decreases, giving unserviced clubs a fair opportunity to secure slots."*

**Q: What is the purpose of `TimeSlot.overlaps(...)`?**
> **A:** *"It solves the interval intersection problem. Two time slots on the same date overlap if and only if `start1 < end2` AND `start2 < end1`. We use this mathematical property to prevent double-booking of any facility."*

---
*Prepared for Campus Resource Allocation Simulator Project Submission.*
