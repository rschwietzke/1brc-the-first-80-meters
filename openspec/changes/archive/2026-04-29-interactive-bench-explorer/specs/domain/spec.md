## ADDED Requirements

### Requirement: Domain Terminology
To ensure consistency across the UI and data layer, the application SHALL strictly adhere to the following domain terminology:

#### Concept: Test Run
- **Definition:** The execution of a full set of test cases (a single class) in many dimensions, as configured, at a given point in time. 
- **Key:** Timestamp

#### Concept: Test Case
- **Definition:** A single Java class that can be executed in many dimensions (e.g., JDK, dataset, GC, CLI parameters).

#### Concept: Test Case Execution
- **Definition:** Exactly one run of a test case in one specific combination of dimensions.
- **Components:** Contains several measurements, plus a JFR run, plus a perf stat run.

#### Concept: Dimension
- **Definition:** One factor in the execution (e.g., JDK version, data size, command line params).

#### Concept: Diagnostic Data
- **Definition:** The raw artifacts generated during a Test Case Execution, including JFR (Java Flight Recorder) outputs and perf stat data.

### Requirement: Diagnostic Extraction
The system SHALL support extracting extra telemetry data from JFR diagnostics (either pre-processed during ingest or on-the-fly via the viewer) without requiring database migrations.

### Requirement: Database as Cache
The system SHALL treat the database purely as a high-speed read-model. The Source of Truth (SoT) remains the raw CSV, JFR, and perf files.
- **Rule:** No database schema migration tools (like Flyway) are required.
- **Rule:** The system is not an online hosted tool; it is local and has no accounts or permissions.
