# 1BRC Benchmark Viewer

An interactive, responsive dashboard application built to view, analyze, and compare performance results from the 1 Billion Rows Challenge (1BRC) test runs.

## Technology Stack

- **Backend:** Spring Boot 3.x, Java 21/25, JPA / Hibernate, H2 Database (embedded)
- **Search & Indexing:** Hibernate Search 7.x with Apache Lucene (embedded)
- **Frontend UI:** HTML5, Thymeleaf, Bootstrap 5.3, Bootstrap Icons
- **Dynamic Interactions:** HTMX 2.x (AJAX partial swaps, loading states, OOB updates)
- **Visualizations:** Apache ECharts 5.5 (median runtime distribution, JDK comparison, GC comparison, timeline trends)

## Architecture & Design Patterns

1. **Variation Matrix:** Grouped display of implementation class median runtimes across configurations. Normalization toggles between per-class column scaling and global absolute scaling.
2. **Side-by-Side Comparison:** Compares performance deltas between any two test run executions or environment configurations.
3. **Hidden Signal Anomaly Detection:** Flags situations of compensated degradation where absolute runtime change is stable ($< 5\%$) but underlying hardware performance counters (e.g., instructions, cycles, cache/branch misses) shift significantly ($> 20\%$), displaying a warning `⚠️` badge with tooltip feedback.
4. **Faceted Search Sidebar:** Real-time filter selection by JDK, GC, VM Options, Dataset, Class name, and Hardware Binding, dynamically refreshing the dashboard using HTMX partial swaps.

## API Endpoints

### ECharts JSON API
- `GET /api/runs/{timestamp}/summary`: Returns per-class median runtime distribution grouped by environment.
- `GET /api/runs/{timestamp}/compare/jdk`: Returns class runtimes grouped and averaged by JDK version.
- `GET /api/runs/{timestamp}/compare/gc`: Returns class runtimes grouped and averaged by GC configuration.

### Administrative commands
- `POST /admin/reimport`: Programmatically clears the H2 database, re-scans the history logs directory, imports all data, and rebuilds the Lucene search index.

## Setup & Local Run

1. **Prerequisites:** Ensure you have JDK 21+ and Maven installed.
2. **Run Server:**
   ```bash
   mvn spring-boot:run -pl benchmark-viewer
   ```
3. **Access UI:** Open [http://localhost:8080/](http://localhost:8080/) in your web browser.
