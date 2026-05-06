# Implementation Steps

Each step follows TDD: write tests first, then implement to make them pass.
Each step ends with a manual verification gate — **STOP and wait for user OK** before starting the next step.

---

## Step 1: Project Scaffold

**Goal:** A bare Spring Boot application that compiles, starts, and serves a styled page.

- [x] 1.1 Create `benchmark-viewer/` Maven module with `pom.xml`: Spring Boot 4 parent, dependencies (Spring Boot Web, Spring Data JPA, H2, Hibernate Search 8.x Lucene backend, Thymeleaf, Thymeleaf Layout Dialect, spring-boot-starter-test)
- [x] 1.2 Uncomment `<module>benchmark-viewer</module>` in root `pom.xml`
- [x] 1.3 Create Spring Boot main application class
- [x] 1.4 Configure `application.properties`: H2 file-mode datasource, Hibernate ddl-auto, Hibernate Search Lucene backend directory, server port 8080
- [x] 1.5 Create Thymeleaf layout template (`layout.html`) with Bootstrap 5 CDN (light theme), HTMX CDN, ECharts 6.x CDN, CSS Grid layout (fixed sidebar + main content area + breadcrumb bar)
- [x] 1.6 Create `static/css/style.css` with custom styling (clean light theme, Inter font, sidebar, cards)
- [x] 1.7 Create a placeholder `index.html` that uses the layout and shows "Benchmark Explorer" heading
- [x] 1.8 Write smoke test: Spring context loads, GET `/` returns 200

**🛑 MANUAL VERIFY then STOP:**
- `mvn spring-boot:run` starts without errors
- Browser at `localhost:8080` shows styled page with layout shell (sidebar placeholder, empty main content, breadcrumb bar)
- **→ Wait for user OK before Step 2**

---

## Step 2: Domain Model & Data Import

**Goal:** CSV and metadata files are parsed and persisted into H2 on startup.

### Tests first:
- [ ] 2.1 Write unit tests for `TestRun` entity: field mapping, constraints
- [ ] 2.2 Write unit tests for `Measurement` entity: field mapping, `error` flag derived from Checksum
- [ ] 2.3 Write unit tests for CSV parsing logic: quoted fields with commas/spaces, ERROR rows set `error = true`, column fallback (TASKSET → BINDING for old-format CSVs), all 18 metric fields parsed correctly
- [ ] 2.4 Write unit tests for sysinfo.txt parsing: `Key: Value` format, all 6 fields extracted
- [ ] 2.5 Write unit tests for meta.json parsing: timestamp, totalRuns, comment
- [ ] 2.6 Write integration test: import a test CSV fixture → verify row count, field values, ERROR flag

### Implementation:
- [ ] 2.7 Create `TestRun` JPA entity: id, timestamp, totalRuns, comment, hostname, kernelVersion, os, cpu, cpuCores, memory
- [ ] 2.8 Create `Measurement` JPA entity: id, `@ManyToOne` TestRun, runTimestamp (denormalized — pending Q2), 7 dimension fields, all 18 metric fields, `error` boolean
- [ ] 2.9 Add Hibernate Search `@Indexed` on Measurement, `@KeywordField` on all 7 dimension fields
- [ ] 2.10 Create `TestRunRepository` and `MeasurementRepository` (Spring Data JPA)
- [ ] 2.11 Implement `DataImportService`: scan for `*-meta.json`, parse `*-sysinfo.txt`, parse `*.csv` with proper CSV parser. Set `error = true` when `Checksum = "ERROR"`. Skip already-imported timestamps.
- [ ] 2.12 Create `StartupImporter` (`CommandLineRunner`) that runs `DataImportService`
- [ ] 2.13 Trigger Hibernate Search mass indexer after import completes
- [ ] 2.14 All tests pass

**🛑 MANUAL VERIFY then STOP:**
- Application starts, logs import of 1,224 measurements
- H2 console (`/h2-console`) shows `TEST_RUN` (1 row) and `MEASUREMENT` (1,224 rows)
- ERROR rows have `error = true`
- Restart → skips re-import (already imported)
- **→ Wait for user OK before Step 3**

---

## Step 3: Test Run List (Landing Page)

**Goal:** Landing page shows all imported test runs as clickable cards.

### Tests first:
- [ ] 3.1 Write controller test: GET `/` returns 200, model contains `testRuns` list
- [ ] 3.2 Write controller test: GET `/` with `HX-Request` header returns fragment (not full page)

### Implementation:
- [ ] 3.3 Create `DashboardController` with `@GetMapping("/")` — loads all TestRuns, renders full page or HTMX fragment based on `HX-Request` header
- [ ] 3.4 Create `fragments/run-list.html` — Bootstrap card list sorted newest-first: timestamp, hostname, CPU, memory, measurement count, dataset summary
- [ ] 3.5 Wire card click: `hx-get="/runs/{timestamp}"` targeting `#main-content` with `hx-push-url`
- [ ] 3.6 All tests pass

**🛑 MANUAL VERIFY then STOP:**
- `localhost:8080` shows card(s) for imported test runs
- Card displays: AMD Ryzen 7 PRO 4750U, 30Gi, Ubuntu 24.04, "1,224 measurements"
- **→ Wait for user OK before Step 4**

---

## Step 4: Variation Matrix

**Goal:** Clicking a test run shows environment × class matrix with heat coloring and ERROR flagging.

### Tests first:
- [ ] 4.1 Write controller test: GET `/runs/{timestamp}` returns 200, model contains grouped matrix data
- [ ] 4.2 Write unit test for heat coloring logic: per-column normalization produces correct color values for min/max/mid
- [ ] 4.3 Write unit test for heat coloring logic: global normalization uses single min/max across all cells
- [ ] 4.4 Write unit test: ERROR measurements produce distinct display model (not normal `0 ms`)

### Implementation:
- [ ] 4.5 Create `RunDetailController` with `@GetMapping("/runs/{timestamp}")` — loads Measurements, groups into matrix (rows = environment combos, columns = classes)
- [ ] 4.6 Create `fragments/variation-matrix.html` — Bootstrap table, environment rows (JDK + GC + heap), class columns, cells with formatted `MedianRuntimeMs`
- [ ] 4.7 Implement heat coloring with two modes (per-column, global). Default: per-column.
- [ ] 4.8 Add normalization toggle control (radio buttons or switch) above matrix
- [ ] 4.9 Flag ERROR cells: greyed out / strikethrough / error badge
- [ ] 4.10 Wire cell click: `hx-get="/runs/{timestamp}/detail/{measurementId}"` targeting `#main-content`
- [ ] 4.11 Create `fragments/breadcrumb.html` — Runs → [timestamp], each segment is HTMX link
- [ ] 4.12 All tests pass

**🛑 MANUAL VERIFY then STOP:**
- Click run card → matrix appears (18 env rows × 68 class columns)
- Heat coloring visible; toggle normalization → colors recompute
- ERROR cells (e.g. `BRC100_DirectTempWrite`) visually distinct
- Breadcrumb shows "Runs → 20260505-175402"
- **→ Wait for user OK before Step 5**

---

## Step 5: Sidebar Faceted Filtering

**Goal:** Sidebar with dimension checkboxes and counts. Toggling filters updates the matrix.

### Tests first:
- [ ] 5.1 Write unit test for `SearchService.getFacetCounts()`: returns correct counts for unfiltered state
- [ ] 5.2 Write unit test for `SearchService.getFacetCounts()`: applying a JDK filter reduces counts in other dimensions
- [ ] 5.3 Write unit test for `SearchService.searchMeasurements()`: returns only matching measurements
- [ ] 5.4 Write controller test: GET `/sidebar` returns fragment with facet data
- [ ] 5.5 Write controller test: GET `/sidebar?jdk=JDK_21_OPEN` returns updated counts

### Implementation:
- [ ] 5.6 Implement `SearchService` using Hibernate Search `SearchSession`
- [ ] 5.7 Implement `getFacetCounts(activeFilters)` — `Map<String, Map<String, Long>>` via terms aggregation
- [ ] 5.8 Implement `searchMeasurements(activeFilters)` — filtered Measurement list
- [ ] 5.9 Create `FilterState` model class
- [ ] 5.10 Create `fragments/sidebar.html` — collapsible accordion for each dimension, checkboxes + count badges
- [ ] 5.11 Create `SidebarController` with `@GetMapping("/sidebar")`
- [ ] 5.12 Wire filter changes: checkbox toggle → HTMX refresh of sidebar + main content
- [ ] 5.13 Implement "Clear All" button
- [ ] 5.14 Responsive sidebar: Bootstrap offcanvas for viewport < 768px
- [ ] 5.15 All tests pass

**🛑 MANUAL VERIFY then STOP:**
- Sidebar shows dimensions with counts (e.g. JDK_21_OPEN: 612, JDK_25_OPEN: 612)
- Check "ZGC" → counts update, matrix shrinks
- Add "JDK_21_OPEN" → narrows further
- "Clear All" → restores full view
- **→ Wait for user OK before Step 6**

---

## Step 6: Execution Detail View

**Goal:** Clicking a matrix cell shows full telemetry with raw and derived metrics.

### Tests first:
- [ ] 6.1 Write controller test: GET `/runs/{timestamp}/detail/{id}` returns 200, model contains Measurement
- [ ] 6.2 Write unit test for derived metrics: IPC, CPI, branch miss rate %, L1 miss rate — computed correctly from raw counters
- [ ] 6.3 Write unit test for derived metrics: returns null/absent when underlying counters are zero
- [ ] 6.4 Write unit test for JFR file detection: returns download path when `.jfr` exists, null when absent

### Implementation:
- [ ] 6.5 Create `DetailController` with `@GetMapping("/runs/{timestamp}/detail/{id}")`
- [ ] 6.6 Create `fragments/detail.html` — card grid:
  - Environment context (JDK, GC, VM opts, binding, dataset, full class name)
  - Runtime group (MedianRuntimeMs, PerfRuntimeMs, JfrRuntimeMs, SecElapsed, SecUser, SecSys)
  - Hardware counters (all raw values)
  - Derived metrics (conditional: IPC, CPI, branch miss rate, L1 miss rate)
  - Error indicator (badge + Checksum value when `error = true`)
- [ ] 6.7 Hide entire metric groups when all underlying values are zero
- [ ] 6.8 JFR download link if `.jfr` file exists for this configuration
- [ ] 6.9 Update breadcrumb: Runs → [timestamp] → [className]
- [ ] 6.10 All tests pass

**🛑 MANUAL VERIFY then STOP:**
- Click matrix cell → detail view with all metrics
- Derived metrics appear only when counters are non-zero
- ERROR measurement shows error badge
- Breadcrumb fully navigable (click "Runs" → back to list)
- **→ Wait for user OK before Step 7**

---

## Step 7: Side-by-Side Environment Comparison

**Goal:** Select two environments, see delta table with percentage changes color-coded.

### Tests first:
- [ ] 7.1 Write unit test for delta calculation: correct Δ ms and Δ % for two measurement sets
- [ ] 7.2 Write unit test for delta sorting: biggest regressions first by default
- [ ] 7.3 Write controller test: GET `/runs/{timestamp}/compare?envA=...&envB=...` returns comparison model

### Implementation:
- [ ] 7.4 Create `CompareController` with `@GetMapping("/runs/{timestamp}/compare")`
- [ ] 7.5 Create `fragments/compare.html` — environment selector (two dropdowns to pick envA, envB)
- [ ] 7.6 Create `fragments/compare-results.html` — table: Class, Runtime A, Runtime B, Δ ms, Δ %. Red = regression, green = improvement.
- [ ] 7.7 Sort by Δ % descending (biggest regressions first). Add column sort toggles.
- [ ] 7.8 Optional: ECharts scatter plot (X = env A, Y = env B, diagonal = parity)
- [ ] 7.9 Apply active sidebar filters to comparison query
- [ ] 7.10 Update breadcrumb: Runs → [timestamp] → Compare
- [ ] 7.11 All tests pass

**🛑 MANUAL VERIFY then STOP:**
- Select "JDK 21 + ZGC + 2G" vs "JDK 25 + ZGC + 2G"
- Delta table with 68 classes, % changes color-coded
- Regressions in red, improvements in green
- Sorting works
- **→ Wait for user OK before Step 8**

---

## Step 8: Timeline View (Cross-Run Trends)

**Goal:** See how a class + environment performed across all runs over time.

### Tests first:
- [ ] 8.1 Write unit test for timeline data assembly: correct ordering by timestamp, correct metric values per point
- [ ] 8.2 Write controller test: GET `/api/timeline?className=...&gcOpts=...` returns valid JSON
- [ ] 8.3 Write controller test: GET `/timeline` returns fragment with chart container

### Implementation:
- [ ] 8.4 Create `TimelineController` with `@GetMapping("/timeline")` — accepts dimension filters
- [ ] 8.5 Create `@GetMapping("/api/timeline")` — JSON endpoint for ECharts
- [ ] 8.6 Create `fragments/timeline.html` — ECharts line chart: X = timestamps, Y = MedianRuntimeMs
- [ ] 8.7 Create `static/js/charts.js` — ECharts init utilities (theme, tooltip, zoom, click handlers)
- [ ] 8.8 Wire ECharts initialization on `htmx:afterSwap`
- [ ] 8.9 Metric toggle: overlay hardware counters (Instructions, IPC) on secondary Y-axis
- [ ] 8.10 Wire data point click → execution detail navigation
- [ ] 8.11 Update breadcrumb: Runs → Timeline → [className]
- [ ] 8.12 All tests pass

**🛑 MANUAL VERIFY then STOP:**
- Timeline for `BRC035_NoLambda` + ZGC + 2G → chart with data point(s)
- Metric toggle adds counter overlay
- Click data point → execution detail
- (Single run = single point; view proves out as more runs are imported)
- **→ Wait for user OK before Step 9**

---

## Step 9: Hidden Signals, Summary Charts & Polish

**Goal:** Surface counter anomalies, add summary charts, and polish the full UX.

### Tests first:
- [ ] 9.1 Write unit test for hidden signal detection: stable runtime (< 5% delta) + large counter shift (> 20%) → flagged
- [ ] 9.2 Write unit test for hidden signal detection: both runtime and counters stable → not flagged
- [ ] 9.3 Write controller test: GET `/api/runs/{timestamp}/summary` returns valid chart JSON
- [ ] 9.4 Write controller test: POST `/admin/reimport` triggers re-import

### Implementation:
- [ ] 9.5 Implement hidden signal detection in comparison and timeline views. Show ⚠️ indicator on affected rows/points.
- [ ] 9.6 Create `/api/runs/{timestamp}/summary` — JSON for per-class bar chart
- [ ] 9.7 Create `/api/runs/{timestamp}/compare/jdk` — JSON for JDK comparison grouped bar chart
- [ ] 9.8 Create `/api/runs/{timestamp}/compare/gc` — JSON for GC comparison grouped bar chart
- [ ] 9.9 Embed summary ECharts in run detail page
- [ ] 9.10 Add "Reimport Data" button → `@PostMapping("/admin/reimport")`
- [ ] 9.11 Add loading spinners (Bootstrap spinner via HTMX events)
- [ ] 9.12 Add error notifications (Bootstrap toast/alert)
- [ ] 9.13 Verify HTMX navigation: back/forward buttons, deep links, breadcrumbs
- [ ] 9.14 Verify faceted filtering end-to-end
- [ ] 9.15 Add `README.md` to `benchmark-viewer/`
- [ ] 9.16 All tests pass

**🛑 MANUAL VERIFY (final):**
- Full walkthrough: landing → pick run → matrix → filter by ZGC → compare JDK 21 vs 25 → spot regressions → detail → derived metrics → timeline → reimport → browser back/forward
- Hidden signal indicators appear where applicable
- All error states handled gracefully
