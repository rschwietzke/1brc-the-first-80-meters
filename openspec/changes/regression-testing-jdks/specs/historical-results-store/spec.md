## ADDED Requirements

### Requirement: Each matrix run is archived as a paired CSV and Markdown file
After a matrix run completes, the script SHALL write two files sharing the same timestamp prefix in `data/benchmark-history/`:
- `<YYYYMMDD_HHMMSS>.csv` — raw results in plain text CSV format
- `<YYYYMMDD_HHMMSS>.md` — the evaluation report in Markdown

Both files SHALL be created whenever at least one result row exists. Both are plain text and SHALL be committed to git. The `data/benchmark-history/` directory SHALL NOT be listed in `.gitignore`.

#### Scenario: Successful run produces a paired archive
- **WHEN** a matrix run completes with at least one result row
- **THEN** both `data/benchmark-history/20260422_143000.csv` and `data/benchmark-history/20260422_143000.md` are created

#### Scenario: Totally failed run is not archived
- **WHEN** a matrix run produces zero result rows (all combinations failed)
- **THEN** no archive files are created and the script prints a warning

---

### Requirement: Archive CSV includes comprehensive machine fingerprint
The archive CSV SHALL begin with comment lines (prefixed `#`) recording a full machine fingerprint so that later readers can assess whether cross-run differences reflect hardware/OS changes or genuine performance changes. The fingerprint SHALL include:
- Hostname (`hostname`)
- Kernel version (`uname -r`)
- OS name and version (`/etc/os-release`: `NAME` and `VERSION_ID`)
- CPU model and core count (`lscpu`: `Model name`, `CPU(s)`)
- CPU max frequency (`lscpu`: `CPU max MHz`)
- Total memory (`free -h` first data line)
- Run timestamp and JDKs/profiles tested

#### Scenario: Archive opened as text
- **WHEN** the archive CSV is opened in a text editor
- **THEN** the first lines show:
  ```
  # Host: mymachine
  # Kernel: 6.8.0-57-generic
  # OS: Ubuntu 24.04
  # CPU: Intel Core i9-13900K, 24 cores, 5500 MHz max
  # Memory: 64G
  # Run: 2026-04-22T14:30:00
  # JDKs: /jvm/jdk-21, /jvm/jdk-25
  # Profiles: DEFAULT, EPSILON, ZGC
  ```

---

### Requirement: Script detects machine fingerprint mismatches for cross-run comparisons
When loading a historical archive for comparison, the script SHALL compare the machine fingerprint of the archive against the current machine. If any of hostname, kernel version, or CPU model differ, the comparison SHALL be flagged as cross-machine.

#### Scenario: Same machine, different run
- **WHEN** hostname, kernel, and CPU match between current run and historical archive
- **THEN** cross-run deltas are treated as reliable and labeled as such in the report

#### Scenario: Different machine or kernel
- **WHEN** any fingerprint field differs between current run and archive
- **THEN** the report prominently notes the machine difference and labels cross-run deltas as **trends only**, not regressions

---

### Requirement: Previous run can be listed and selected for comparison
The script SHALL provide a `--list-runs` flag that prints the available archived run timestamps from `data/benchmark-history/` in reverse chronological order. The `--compare-run <timestamp>` flag SHALL load the archive matching that timestamp for use in the comparison report.

#### Scenario: Listing available runs
- **WHEN** the user runs `./benchmark-matrix.sh --list-runs`
- **THEN** the script prints a list of available timestamps with paired file status (CSV + MD), newest first, and exits

#### Scenario: Compare against a specific prior run
- **WHEN** the user provides `--compare-run 20260415_100000`
- **THEN** the script loads `data/benchmark-history/20260415_100000.csv` as the historical baseline for the report

#### Scenario: Compare against most recent prior run (default)
- **WHEN** `--compare-run` is omitted but `data/benchmark-history/` contains at least one previous archive
- **THEN** the script automatically uses the most recent archive as the historical baseline (and notes this in the report)

---

### Requirement: Output files are always plain text suitable for git
All output files written by the script SHALL be plain text: CSV for raw data, Markdown for reports. No binary formats (HTML, PDF, Excel) SHALL be produced by default. The `--output-report` flag SHALL write an additional Markdown copy to the specified path; it SHALL NOT replace the auto-archived copy in `data/benchmark-history/`.

#### Scenario: Additional report copy requested
- **WHEN** user provides `--output-report latest-report.md`
- **THEN** the Markdown evaluation is written both to `data/benchmark-history/<timestamp>.md` (archived) and to `latest-report.md` (convenience copy)

#### Scenario: No additional report path given
- **WHEN** `--output-report` is omitted
- **THEN** only the auto-archived `data/benchmark-history/<timestamp>.md` is written; no other report file is created
