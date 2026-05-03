## Why

Apache ECharts 6 brings enhanced rendering capabilities, better performance, and updated visual aesthetics. Upgrading the 1BRC Benchmark Viewer from ECharts 5.5.0 to version 6 ensures we are utilizing the latest features, improving our dashboard's interactive visualizations, and staying current with modern web standards.

## What Changes

- Upgrade Apache ECharts dependency in `base.html` from `5.5.0` to `6.0.0` (or the latest stable 6.x version).
- Verify and update any deprecated chart configurations, axis definitions, or tooltip formatters in `index.html` to align with the ECharts 6 API.

## Capabilities

### New Capabilities
None

### Modified Capabilities
- `benchmark-viewer`: The visualization rendering library dependency is being upgraded.

## Impact

- **Affected Code**: `benchmark-viewer/src/main/resources/templates/base.html`, `benchmark-viewer/src/main/resources/templates/index.html`
- **Dependencies**: Frontend ECharts CDN link.
- **Systems**: The dashboard visualization components in the browser.
