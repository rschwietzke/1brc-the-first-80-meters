## ADDED Requirements

### Requirement: Variation Diffing
The viewer SHALL provide side-by-side or overlaid visualizations to compare metrics between different test variations.

#### Scenario: Comparing JDKs
- **WHEN** the user selects a test case and chooses two different JDK dimensions
- **THEN** an ECharts bar chart or scatter plot renders the delta between runtime, IPC, and branch misses.

### Requirement: Trend Visualization
The viewer SHALL render historical trend lines for a given execution configuration.

#### Scenario: Spotting a regression
- **WHEN** the user opens the Timeline View for a variation
- **THEN** a line chart plots the median runtime over previous Test Runs, clearly visualizing any sudden spikes.

### Requirement: Heatmap Matrix
The viewer SHALL render heatmap visualizations where one axis represents Test Cases, the other axis represents Test Runs (ordered by time), and the color intensity encodes a user-selectable metric (e.g., median runtime, IPC, cache misses). The user can switch between Dimensions to produce different heatmap layers over the same grid.

#### Scenario: Spotting Regressions at a Glance
- **WHEN** the user opens the Heatmap view and selects a Dimension (e.g., `JDK 25`)
- **THEN** a color-coded grid is rendered with Test Cases on the Y-axis and Test Runs on the X-axis, where hot spots indicate performance anomalies.

#### Scenario: Drilling Down from a Hot Spot
- **WHEN** the user clicks on a specific cell in the heatmap
- **THEN** the system navigates to the corresponding Test Case Execution detail view.

#### Scenario: Comparing Dimension Layers
- **WHEN** the user switches the Dimension selector (e.g., from `JDK 21` to `JDK 25`)
- **THEN** the heatmap re-renders with data filtered to that Dimension, allowing visual comparison of the two layers.

