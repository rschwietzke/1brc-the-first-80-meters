## MODIFIED Requirements

### Requirement: Faceted Search & Navigation
The core dashboard SHALL support a persistent sidebar for filtering dimensions across the entire application state.

#### Scenario: Global Dimensional Filtering
- **WHEN** the user is viewing the Variations matrix and toggles the "JDK 25" facet
- **THEN** the view instantly updates via HTMX to only display variations that used JDK 25, retaining the current hierarchical level.

## ADDED Requirements

### Requirement: Deep Search
The system SHALL provide full-text and structured search across all indexed data, including measurement values, dimension key-value pairs, and diagnostic metadata.

#### Scenario: Structured Query
- **WHEN** the user enters a query such as `branch-misses > 5% AND jdk:25`
- **THEN** the system returns all matching Test Case Executions, regardless of which Test Run or Test Case they belong to.

#### Scenario: Free-Text Search
- **WHEN** the user enters free-text terms (e.g., a class name fragment or a JVM flag)
- **THEN** the system searches across all indexed fields and returns ranked results.

### Requirement: Baseline Marking
The system SHALL allow the user to designate a specific Test Run (or a specific Test Case Execution) as a "baseline." All subsequent comparisons and visualizations can then be rendered relative to this baseline.

#### Scenario: Setting a Baseline
- **WHEN** the user marks a Test Run or Test Case Execution as the baseline
- **THEN** the system persists this designation (e.g., via a marker file in the result directory).

#### Scenario: Comparing Against Baseline
- **WHEN** the user views any other Test Run or Execution while a baseline is set
- **THEN** metrics are displayed as deltas relative to the baseline (e.g., "+3.2% slower", "-12% fewer cache misses").
