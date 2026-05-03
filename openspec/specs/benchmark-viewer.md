# Benchmark Viewer

## Purpose
TBD - Core benchmark viewing and navigation capabilities.

## Requirements

### Requirement: Faceted Search & Navigation
The core dashboard SHALL support a persistent sidebar for filtering dimensions across the entire application state.

#### Scenario: Global Dimensional Filtering
- **WHEN** the user is viewing the Variations matrix and toggles the "JDK 25" facet
- **THEN** the view instantly updates via HTMX to only display variations that used JDK 25, retaining the current hierarchical level.
