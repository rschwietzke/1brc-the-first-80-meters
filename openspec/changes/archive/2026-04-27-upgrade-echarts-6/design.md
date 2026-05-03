## Context

The 1BRC Benchmark Viewer currently utilizes Apache ECharts 5.5.0 for visualizing complex benchmarking metrics (such as the Median Runtime bar chart and Instructions vs. IPC scatter plot). The user has requested an upgrade to Apache ECharts 6 to leverage the latest performance optimizations, modern API features, and visual enhancements.

## Goals / Non-Goals

**Goals:**
- Successfully bump the ECharts dependency from 5.x to 6.x.
- Ensure existing chart configurations (axes, tooltips, series data) remain fully functional under the ECharts 6 rendering engine.
- Verify that responsive resizing and HTMX live re-rendering continue to work seamlessly.

**Non-Goals:**
- Completely rewriting the chart types (e.g., swapping bar charts for entirely different visualizations).
- Modifying the Java backend API payloads (e.g., `/api/metrics` JSON structure remains identical).

## Decisions

**1. Dependency Upgrade via CDN**
- *Decision*: Update the `<script>` tag in `base.html` to point to ECharts 6.
- *Rationale*: The frontend relies on a CDN script import for ECharts. Bumping the version string directly in the template is the fastest and most reliable path.

**2. API Compatibility Verification**
- *Decision*: Maintain the existing `echarts.init()` and `.setOption()` calls.
- *Rationale*: ECharts 6 maintains high backward compatibility with ECharts 5 option definitions. We will retain the current logic, only adjusting syntax if ECharts 6 logs deprecation warnings or breaking changes for specific tooltip formatters.

## Risks / Trade-offs

- **Risk: Breaking Changes in ECharts 6 API** → *Mitigation*: Extensive manual testing of the dashboard after the upgrade. Any broken chart logic (such as missing tooltips or misaligned axes) will be refactored to comply with ECharts 6 conventions.
- **Risk: Caching Issues** → *Mitigation*: Ensure developers perform a hard refresh or use cache-busting on the script tag to ensure the browser loads the new v6 bundle instead of the cached v5 bundle.
