# Benchmark Viewer

## Purpose
TBD - Core benchmark viewing and navigation capabilities.
## Requirements
### Requirement: Faceted Search & Navigation
The core dashboard SHALL support a persistent sidebar for filtering dimensions across the entire application state.

#### Scenario: Global Dimensional Filtering
- **WHEN** the user is viewing the Variations matrix and toggles the "JDK 25" facet in the sidebar
- **THEN** an HTMX request sends the updated filter state to the server, which returns a re-rendered main content fragment filtered to only show variations that used JDK 25.

#### Scenario: Multi-dimensional filtering
- **WHEN** the user activates both "JDK 21" and "G1GC" facets simultaneously
- **THEN** the server applies AND logic across dimensions and returns only permutations matching both JDK 21 AND G1GC.

#### Scenario: Filter badge counts
- **WHEN** the sidebar filter panel is rendered
- **THEN** each facet value displays a Bootstrap badge with the count of matching permutations, computed via Hibernate Search terms aggregation within the current filter context.

#### Scenario: Clear all filters
- **WHEN** the user clicks the "Clear All" button in the sidebar
- **THEN** all active filters are removed via HTMX and both the sidebar facet counts and the main content view are re-rendered with unfiltered data.

### Requirement: Variation Matrix View
The benchmark viewer SHALL display a matrix view showing Environment configurations (rows) × Implementation classes (columns) with the median runtime in each cell, grouped by dataset.

#### Scenario: Matrix cell display
- **WHEN** the user navigates to a specific Test Run
- **THEN** the Thymeleaf template renders a Bootstrap table matrix for each dataset with environments as rows, classes as columns, and formatted median runtime values in cells.

#### Scenario: Matrix cell click for detail
- **WHEN** the user clicks a cell in the variation matrix
- **THEN** HTMX fetches the Execution Detail fragment for that specific permutation and swaps the main content area.

#### Scenario: Heat coloring
- **WHEN** the matrix is rendered
- **THEN** cells SHALL be color-coded using a green-to-red gradient based on relative performance within the same class column, computed server-side and applied as inline styles or CSS classes.

