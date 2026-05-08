## ADDED Requirements

### Requirement: Spring Boot Application Shell
The benchmark explorer SHALL be a Spring Boot 4 web application served on `localhost:8080`, with Thymeleaf templates, Bootstrap 5 styling, and HTMX for dynamic interactions.

#### Scenario: Application startup
- **WHEN** the user runs `mvn spring-boot:run` in the `benchmark-viewer` module
- **THEN** the application starts on port 8080 and displays the main dashboard with a sidebar and the Test Run list view.

#### Scenario: Thymeleaf layout with Bootstrap
- **WHEN** any page is rendered
- **THEN** the page uses a Thymeleaf layout template with Bootstrap 5 light theme, a fixed sidebar, breadcrumb navigation, and a swappable main content area.

### Requirement: Data Import Pipeline
The application SHALL import benchmark CSV and meta-JSON files from the `data/benchmark-history/` directory into an H2 database at startup.

#### Scenario: Startup import
- **WHEN** the application starts and the database is empty
- **THEN** the import service scans `data/benchmark-history/` for `*-meta.json` files, creates `TestRun` entities from the metadata, and parses the corresponding `.csv` files into `Measurement` entities linked to their `TestRun`.

#### Scenario: Incremental import
- **WHEN** the application starts and the database already contains data from previous runs
- **THEN** only new runs (timestamps not already in the database) are imported, skipping existing data.

#### Scenario: CSV column compatibility
- **WHEN** the import service parses a CSV file using the older column layout (e.g., `TASKSET` instead of `BINDING`, missing `PerfRuntimeMs`)
- **THEN** the parser SHALL handle both layouts, falling back to `TASKSET` when `BINDING` is absent and defaulting missing numeric columns to 0.

#### Scenario: Manual reimport
- **WHEN** the user triggers a "Reimport" action from the UI
- **THEN** the application clears all existing data, re-scans the history directory, and re-imports all CSV files.

### Requirement: JPA Domain Model
The application SHALL persist benchmark data using two JPA entities: `TestRun` and `Measurement`.

#### Scenario: TestRun entity
- **WHEN** a meta-JSON file is imported
- **THEN** a `TestRun` entity is created with fields: `id`, `timestamp`, `totalRuns`, `comment`, `kernelVersion`, `os`, `cpu`, `cpuCores`, and `memory` (from sysinfo).

#### Scenario: Measurement entity
- **WHEN** a CSV row is imported
- **THEN** a `Measurement` entity is created with dimension fields (`jdk`, `gcOpts`, `vmOpts`, `progOpts`, `binding`, `dataset`, `className`) and metric fields (`medianRuntimeMs`, `instructions`, `cycles`, `branches`, `branchMisses`, `l1Misses`, `llcMisses`, `pageFaults`, `contextSwitches`, `cpuMigrations`, `ipc`, `gcPauseMs`, `allocatedBytes`, `jitCompilationMs`), linked to its parent `TestRun` via a `@ManyToOne` relationship.

### Requirement: Hibernate Search Indexing
The application SHALL index `Measurement` entities with Hibernate Search 7.x (Lucene backend) for fast faceted search and aggregation.

#### Scenario: Indexed dimension fields
- **WHEN** the Hibernate Search index is built
- **THEN** the dimension fields (`jdk`, `gcOpts`, `vmOpts`, `progOpts`, `binding`, `dataset`, `className`) are indexed as keyword fields suitable for faceted aggregation.

#### Scenario: Faceted aggregation query
- **WHEN** the sidebar requests facet counts for a dimension
- **THEN** Hibernate Search returns the distinct values and their occurrence counts via a terms aggregation, filtered by any currently active constraints.

### Requirement: HTMX Navigation Pattern
The application SHALL use HTMX for all intra-page navigation, swapping Thymeleaf fragments into the main content area without full page reloads.

#### Scenario: Fragment-based navigation
- **WHEN** the user clicks a navigation element (run card, matrix cell, breadcrumb link)
- **THEN** an HTMX `hx-get` request fetches the corresponding Thymeleaf fragment and swaps the `#main-content` area.

#### Scenario: Deep link support
- **WHEN** the user accesses a deep link URL directly (e.g., `/runs/20260423-191300`)
- **THEN** the controller detects the absence of the `HX-Request` header and returns a full page (layout + content) instead of just the fragment.

#### Scenario: URL preservation
- **WHEN** HTMX swaps the main content area
- **THEN** the browser URL is updated via `hx-push-url` to enable back/forward navigation and bookmarking.
