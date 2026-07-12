## Why

The benchmark harness currently generates static HTML reports per run (via FreeMarker templates in `benchmark-harness`), but all runtime values display as `0 ms` and there is no way to interactively explore, filter, or compare results across runs. Developers need to quickly slice benchmark data by JDK version, GC policy, heap size, and dataset — and drill down from a macro "Test Run" overview into individual execution telemetry — to diagnose performance regressions and validate optimizations. A Spring Boot application with Hibernate Search indexing, HTMX for dynamic navigation, and Bootstrap for styling will provide a rich, server-rendered benchmark explorer that can scale to large datasets and support advanced faceted search.

## What Changes

1. **New Spring Boot web application** — A `benchmark-viewer` Maven module with Spring Boot, Spring Data JPA, Hibernate Search (Lucene), Thymeleaf, HTMX, and Bootstrap 5.
2. **Data import pipeline** — On startup (or on demand), the application scans `data/benchmark-history/` and imports CSV + meta-JSON data into an embedded H2 database, indexed by Hibernate Search.
3. **Faceted filtering** — Hibernate Search faceted aggregations for JDK, GC, heap size, dataset, and class dimensions, rendered as a persistent HTMX sidebar.
4. **Hierarchical drill-down navigation** — Test Run list → Variations matrix → Variation timeline → Execution detail, all via HTMX partial swaps (no full page reloads).
5. **Comparative visualizations** — ECharts-powered bar charts and line charts rendered from JSON endpoints, embedded in Thymeleaf templates.
6. **Execution detail panel** — Full raw telemetry display (instructions, cycles, branches, branch misses, IPC, context switches, etc.) for a selected permutation.

## Capabilities

### New Capabilities
- `benchmark-explorer-app`: The Spring Boot application shell, data import pipeline, JPA entities, Hibernate Search indexing, and Thymeleaf layout.

### Modified Capabilities
- `benchmark-viewer`: Extending the core viewer spec with faceted search/navigation requirements and the hierarchical data views.
- `run-exploration`: Extending the run exploration spec with concrete HTMX navigation flows and drill-down behavior.
- `comparative-visualizations`: Extending the comparative visualizations spec with specific ECharts chart types, JSON data endpoints, and interaction patterns.

## Impact

- **New module**: `benchmark-viewer/` Maven module with Spring Boot 4, added to the parent `pom.xml`.
- **Root pom.xml**: Uncomment the `<module>benchmark-viewer</module>` entry (already present but commented out).
- **Data contract**: Reads existing `data/benchmark-history/*.csv`, `*-meta.json`, and `*-sysinfo.txt` files — no changes to the data format.
- **Dependencies**: Spring Boot 4, Spring Data JPA, H2, Hibernate Search 7.x (Lucene backend), Thymeleaf, HTMX (CDN), Bootstrap 5 (CDN), ECharts 6.x (CDN).
- **Existing code**: No changes to `benchmark-harness` source code. The static HTML reports remain as-is.
