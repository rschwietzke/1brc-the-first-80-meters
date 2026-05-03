## MODIFIED Requirements

### Requirement: ECharts Visualization Engine
The benchmark viewer SHALL use Apache ECharts version 6 for rendering interactive charts.

#### Scenario: Dashboard Initialization
- **WHEN** the user visits the root `/` page
- **THEN** the browser downloads the ECharts 6 bundle from a CDN and successfully initializes the charts.
