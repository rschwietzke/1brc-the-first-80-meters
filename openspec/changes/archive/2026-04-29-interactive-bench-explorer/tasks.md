## 1. Data Layer Enhancements

- [x] 1.1 Update `MeasurementRepository` or Hibernate Search aggregations to group data by `timestamp` (Test Runs).
- [x] 1.2 Implement faceted search aggregations to extract available dimensions (JDKs, Threads) for the filtering sidebar.

## 2. Web Layer (HTMX Endpoints)

- [x] 2.1 Create a new endpoint `/search/test-runs` to serve the macro-level Test Run list.
- [x] 2.2 Create a new endpoint `/search/variations` to serve the Variations matrix for a selected Test Run.
- [x] 2.3 Create a new endpoint `/search/timeline` to serve the historical data for a specific variation.

## 3. Frontend UI Components

- [x] 3.1 Build the global faceted search sidebar fragment.
- [x] 3.2 Build the navigation Breadcrumb component to track hierarchy depth.
- [x] 3.3 Wire up HTMX `hx-get` and `hx-target` attributes to swap hierarchy views seamlessly.

## 4. ECharts Visualizations

- [x] 4.1 Implement a side-by-side or overlaid bar chart configuration for comparing metrics between two variations.
- [x] 4.2 Implement a historical line chart configuration for the Timeline view (Median Runtime over time).
