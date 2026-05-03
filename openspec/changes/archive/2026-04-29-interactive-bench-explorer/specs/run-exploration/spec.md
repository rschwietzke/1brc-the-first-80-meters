## ADDED Requirements

### Requirement: Multi-Pivot Navigation
The application SHALL provide three primary entry paths (pivots) into the multidimensional data cube.

#### Scenario: Pivot by Test Case (The Vertical Slice)
- **WHEN** the user selects a specific Test Case
- **THEN** the system displays all historical executions of that Test Case across all time.
- **AND** the user can pick specific Dimensions (e.g., JDK 25) to filter the view and correlate performance over time.

#### Scenario: Pivot by Test Run (The Horizontal Slice)
- **WHEN** the user selects a specific Test Run (timestamp batch)
- **THEN** the system displays all Test Cases and Dimensions executed in that specific batch.
- **AND** the user can sort and correlate the data to identify bottlenecks within that specific execution window.

#### Scenario: Pivot by Dimension (The Cross-Section)
- **WHEN** the user selects a specific Dimension (e.g., `JDK 25`)
- **THEN** the system displays every Test Case and Test Run that utilized that Dimension.
- **AND** the user can pick specific data subsets to correlate the impact of that dimension across the entire test suite.

#### Scenario: Deep Dive into Execution Detail
- **WHEN** the user isolates a specific `TestCaseExecution` via any of the three pivots above
- **THEN** the system displays the complete, raw telemetry, and diagnostic data (including JFR and Perf stat data) for that single execution row.
