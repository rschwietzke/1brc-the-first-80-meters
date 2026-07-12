# Comparative Visualizations

## Purpose
TBD - Visualizing and comparing benchmark execution metrics.

## Requirements

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
