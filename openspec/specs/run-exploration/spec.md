# Run Exploration

## Purpose
TBD - Navigating and drilling down into benchmark execution data.
## Requirements
### Requirement: Hierarchical Test Run Navigation
The application SHALL dynamically group individual benchmark measurements into "Test Runs" based on their timestamp and provide HTMX-driven navigation to drill down into them.

#### Scenario: Test Run list view
- **WHEN** the user opens the application at the root URL
- **THEN** the main content area renders a Thymeleaf fragment with Bootstrap cards for each Test Run sorted by timestamp (newest first), showing: formatted timestamp, machine info (CPU, RAM), total permutation count, and dataset summary.

#### Scenario: Drilling down into a Test Run
- **WHEN** the user clicks a Test Run card
- **THEN** HTMX fetches the Variation Matrix fragment for that run via `hx-get="/runs/{timestamp}"` and swaps `#main-content`, updating the breadcrumb and URL.

#### Scenario: Viewing a Variation Timeline
- **WHEN** the user selects a specific test variation (a unique combination of class + environment)
- **THEN** HTMX fetches a timeline fragment that includes an ECharts line chart showing the historical median runtime of that variation across all Test Runs.

#### Scenario: Deep Dive into Execution Detail
- **WHEN** the user clicks a matrix cell or a data point on the timeline
- **THEN** HTMX fetches a detail fragment displaying the complete raw telemetry for that single `Measurement` entity, including all metric fields.

### Requirement: Breadcrumb Navigation
The application SHALL display a Bootstrap breadcrumb bar above the main content area showing the current navigation path.

#### Scenario: Breadcrumb display
- **WHEN** the user is viewing an Execution Detail
- **THEN** the breadcrumb renders: "All Runs → [timestamp] → [class] → Detail" using Bootstrap's breadcrumb component, with each segment as a clickable HTMX link.

#### Scenario: Breadcrumb back-navigation
- **WHEN** the user clicks "All Runs" in the breadcrumb while viewing a Variation Timeline
- **THEN** HTMX navigates back to the Test Run list view, preserving any active sidebar filters via request parameters.

