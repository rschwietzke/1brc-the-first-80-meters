## MODIFIED Requirements

### Requirement: Variation Diffing
The viewer SHALL provide side-by-side or overlaid visualizations to compare metrics between different test variations, powered by JSON data endpoints.

#### Scenario: Comparing JDKs
- **WHEN** the user views a Test Run's variation matrix with at least two JDK versions present
- **THEN** an ECharts grouped bar chart renders below the matrix, initialized from a JSON endpoint (`/api/runs/{timestamp}/compare/jdk?class={class}`), showing median runtime, IPC, and branch misses per JDK for the selected class.

#### Scenario: Comparing GC policies
- **WHEN** the user filters to a single JDK and a single class
- **THEN** the grouped bar chart automatically pivots to compare GC policies (G1GC vs ZGC) across different heap sizes, fetched from `/api/runs/{timestamp}/compare/gc`.

### Requirement: Trend Visualization
The viewer SHALL render historical trend lines for a given execution configuration, powered by JSON data endpoints.

#### Scenario: Spotting a regression
- **WHEN** the user opens the Timeline View for a variation
- **THEN** a line chart is initialized from a JSON endpoint (`/api/timeline?class={class}&jdk={jdk}&gc={gc}&...`), plotting median runtime over Test Run timestamps with smooth line and area fill.

#### Scenario: Multi-metric overlay
- **WHEN** the user clicks the "Metrics" toggle in the timeline view
- **THEN** the chart fetches extended data from the same endpoint (with `?metrics=instructions,ipc`) and adds secondary y-axes for instructions and IPC trend lines.

## ADDED Requirements

### Requirement: Summary Bar Charts
The application SHALL display summary bar charts on the Test Run detail page showing the runtime distribution across all permutations.

#### Scenario: Per-class runtime overview
- **WHEN** the user views a Test Run detail
- **THEN** an ECharts bar chart renders from a JSON endpoint (`/api/runs/{timestamp}/summary`), showing one group per implementation class with bars for each environment, colored by JDK version.

### Requirement: Chart JSON Endpoints
The application SHALL expose REST endpoints that return data formatted for direct ECharts consumption.

#### Scenario: Summary endpoint
- **WHEN** a GET request is made to `/api/runs/{timestamp}/summary`
- **THEN** the endpoint returns JSON containing `categories` (class names), `series` (one per environment config, each with a `data` array of median runtimes), suitable for direct use in an ECharts bar chart option.

#### Scenario: Timeline endpoint
- **WHEN** a GET request is made to `/api/timeline` with dimension filter parameters
- **THEN** the endpoint returns JSON containing `timestamps` (x-axis) and `series` (one per metric, each with a `data` array), suitable for direct use in an ECharts line chart option.

### Requirement: Chart Interactivity
All ECharts visualizations SHALL support zoom, tooltip, and data selection interactions.

#### Scenario: Tooltip on hover
- **WHEN** the user hovers over any bar or data point in an ECharts chart
- **THEN** a tooltip displays the exact values for all metrics at that point.

#### Scenario: Zoom on timeline
- **WHEN** the user scrolls or pinch-zooms on a timeline chart
- **THEN** the chart zooms into the selected time range via ECharts' built-in dataZoom component.
