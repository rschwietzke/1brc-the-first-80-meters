## ADDED Requirements

### Requirement: Script produces a Markdown comparison report
After all JDK × profile combinations complete, the script SHALL generate a Markdown report showing performance results in a table grouped by implementation class. For each class, the table SHALL show one column per JDK+profile combination with the median runtime in milliseconds. The report is always auto-generated and archived alongside the CSV.

#### Scenario: Three JDKs compared for one implementation
- **WHEN** BRC021 was benchmarked under JDK 21, JDK 23, and JDK 25 with the same profile
- **THEN** the report shows a row for BRC021 with three runtime cells, one per JDK

#### Scenario: Report written to additional path
- **WHEN** user provides `--output-report comparison.md`
- **THEN** the Markdown is written both to the archive path and to `comparison.md`


---

### Requirement: Report flags performance regressions
The script SHALL accept a `--baseline-jdk <label>` argument designating one JDK+profile combination as the reference baseline. For each implementation, any combination whose median runtime exceeds the baseline by more than a configurable threshold (default: 5%, overridable via `--regression-threshold <percent>`) SHALL be marked as a regression in the report.

#### Scenario: Regression detected
- **WHEN** BRC021 runs in 1200 ms on JDK 25 but the baseline JDK 21 result is 1000 ms (20% slower)
- **THEN** the JDK 25 cell is flagged (e.g., with a ⚠️ marker and the delta percentage)

#### Scenario: Within threshold — no regression
- **WHEN** a result is within 5% of the baseline
- **THEN** no flag is shown for that cell

#### Scenario: No baseline specified
- **WHEN** `--baseline-jdk` is not provided
- **THEN** the report shows raw runtimes with no regression flags; regression comparison is skipped

---

### Requirement: JDK version is auto-detected for report labels
The script SHALL detect each JDK's version string (e.g., `21.0.3`, `25-ea`) by running `$JDK/bin/java -version` and use a short label (version + distribution hint if determinable) as the column header in the report table.

#### Scenario: Standard version output
- **WHEN** `java -version` outputs `openjdk version "25" 2025-09-16`
- **THEN** the column label is `JDK 25` (or the full version string)

#### Scenario: Version detection fails
- **WHEN** `java -version` produces unexpected output
- **THEN** the column label falls back to the JDK path basename

---

### Requirement: Report shows delta against a previous run, qualified by machine match
When a historical baseline is available (auto-detected as the most recent archive, or specified via `--compare-run`), the report SHALL include a delta column per JDK+profile showing % change in median runtime vs. the matching historical row. The report SHALL clearly distinguish whether the comparison is same-machine or different-machine based on the machine fingerprints.

- **Same machine** (hostname, kernel, CPU all match): deltas flagged as regressions ⚠️ or improvements ✅ with standard threshold.
- **Different machine** (any fingerprint field differs): deltas shown as trend indicators (📈/📉) with a prominent disclaimer: *"Machine or OS differs from historical run — interpret as trend only."*

#### Scenario: Cross-run regression, same machine
- **WHEN** BRC021 ran in 1000 ms previously and now runs in 1250 ms, on the same machine
- **THEN** the delta column shows `+25% ⚠️`

#### Scenario: Cross-run trend, different machine
- **WHEN** BRC021 ran in 1000 ms previously on machine A and now runs in 1250 ms on machine B
- **THEN** the delta column shows `+25% 📈` and the report header notes the machine difference

#### Scenario: Cross-run improvement detected, same machine
- **WHEN** BRC021 ran in 1000 ms previously and now runs in 900 ms on the same machine
- **THEN** the delta column shows `-10% ✅`

#### Scenario: No matching historical combination
- **WHEN** the current run includes a JDK+profile that does not appear in the historical archive
- **THEN** the delta cell shows `N/A (new)`

#### Scenario: No historical archive exists
- **WHEN** `data/benchmark-history/` is empty or does not exist
- **THEN** the cross-run delta column is omitted from the report entirely; a note is appended stating no historical baseline was found

---

### Requirement: Report handles sparse JDK+profile sets gracefully
The set of JDK+profile combinations evolves over time — new JDKs and profiles are added, old ones retired or commented out. The report SHALL never fail due to column set mismatches between the current run and historical archives. Instead it SHALL display sparse data clearly:

- **New combination** (in current run, not in history): delta cell shows `— (new)`
- **Retired combination** (in history, not in current run): shown as a greyed-out or italicised historical-only column with label `(retired)`, no current-run data
- All other columns render normally

#### Scenario: New JDK added since last run
- **WHEN** the current run includes `JDK_GRAAL25` which was not in the most recent archive
- **THEN** the report shows a `JDK_GRAAL25` column with current runtimes and `— (new)` in the historical delta cell

#### Scenario: JDK commented out since last run
- **WHEN** the most recent archive has data for `JDK_OLD21` but it is not in the current run
- **THEN** the report shows a `JDK_OLD21 (retired)` column with historical runtimes and no current data

#### Scenario: New profile combination added
- **WHEN** `PROFILE_SHENANDOAH` is added to the profiles config and run for the first time
- **THEN** the corresponding columns appear in the report with `— (new)` in the delta cells
