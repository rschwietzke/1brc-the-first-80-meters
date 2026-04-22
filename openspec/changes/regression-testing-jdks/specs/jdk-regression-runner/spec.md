## ADDED Requirements

### Requirement: Script accepts JDK paths, a profiles config, and a target
The script `benchmark-matrix.sh` SHALL accept JDK paths via `--jdk <path>` (repeatable) and/or a `--jdks <config-file>` pointing to a JDK config file, an optional `--profiles <config-file>`, an `--output <file.csv>` for the master CSV, and a positional target (source directory or single `.java` file) followed by passthrough args.

#### Scenario: Full invocation using a JDK config file
- **WHEN** the user runs `./benchmark-matrix.sh --jdks benchmark-jdks.conf --profiles benchmark-profiles.conf --output matrix.csv src/main/java/org/rschwietzke/parallel/ /data/1000M.txt`
- **THEN** the script reads JDK paths from `benchmark-jdks.conf` and runs a benchmark pass for each JDK × profile combination

#### Scenario: Full invocation with ad-hoc JDK flags
- **WHEN** the user runs `./benchmark-matrix.sh --jdk /jvm/jdk-21 --jdk /jvm/jdk-25 --profiles benchmark-profiles.conf --output matrix.csv src/main/java/org/rschwietzke/parallel/ /data/1000M.txt`

#### Scenario: Missing required arguments
- **WHEN** `--output` or the target directory/file is not supplied
- **THEN** the script prints usage and exits with a non-zero code

---

### Requirement: Each JDK path is validated before use
The script SHALL check that `$JDK/bin/java` and `$JDK/bin/javac` exist and are executable before including that JDK in the matrix. Invalid paths SHALL be reported and skipped.

#### Scenario: Valid JDK
- **WHEN** the path contains `bin/java` and `bin/javac`
- **THEN** the JDK is included in the matrix run

#### Scenario: Invalid JDK path
- **WHEN** the path does not contain `bin/java`
- **THEN** the script prints a warning and skips that JDK; remaining JDKs are still processed

---

### Requirement: JVM profiles are loaded from a config file
A profiles config file SHALL define named JVM profiles as shell variable assignments of the form `PROFILE_<NAME>="<jvm flags>"`. The script SHALL source this file and collect all `PROFILE_` variables as the set of profiles to iterate. A built-in default profile set SHALL be used when no `--profiles` flag is given.

#### Scenario: Config file with three profiles
- **WHEN** `benchmark-profiles.conf` contains `PROFILE_EPSILON="-XX:+UseEpsilonGC ..."`, `PROFILE_G1=""`, and `PROFILE_ZGC="-XX:+UseZGC"`
- **THEN** the matrix runs three profile passes per JDK

#### Scenario: No profiles file provided
- **WHEN** `--profiles` is omitted
- **THEN** the script uses a built-in default set covering at minimum: `DEFAULT` (no extra flags), `EPSILON` (Epsilon GC, low memory), `G1` (explicit G1 with 2g heap), `ZGC` (ZGC with 2g heap)

---

### Requirement: JDK installations are loaded from a config file
A JDK config file SHALL define named JDK installations as shell variable assignments of the form `JDK_<LABEL>="/path/to/jdk"`. The script SHALL source this file, collect all `JDK_` variables, and use them as the set of JDKs to iterate. Any `--jdk <path>` flags are merged with (and take precedence over) config-file entries. Lines prefixed with `#` are ignored, allowing JDKs to be temporarily disabled without deletion.

#### Scenario: Config file with two JDKs
- **WHEN** `benchmark-jdks.conf` contains `JDK_TEMURIN21="/jvm/temurin-21"` and `JDK_TEMURIN25="/jvm/temurin-25"`
- **THEN** the matrix runs both JDKs without requiring `--jdk` flags on the command line

#### Scenario: Commenting out a JDK
- **WHEN** a line is prefixed with `#` (e.g., `# JDK_OLD21="/jvm/old-21"`)
- **THEN** that JDK is skipped in the current run; historical results for it remain in archives and appear as `(retired)` in the comparison report

#### Scenario: Ad-hoc JDK added via flag alongside config file
- **WHEN** the user provides both `--jdks benchmark-jdks.conf` and `--jdk /jvm/graal-25`
- **THEN** the matrix runs all config-file JDKs plus the ad-hoc one; it appears as `(new)` in the comparison report if it has no historical data

---

### Requirement: Script compiles source with each JDK before running
Because the project uses `--enable-preview` tied to a specific major version, the script SHALL compile the source with each JDK's `javac` (via `mvn compile -Djava.home=$JDK`) before running benchmarks for that JDK. Compilation failure for a JDK SHALL be recorded and all profile runs for that JDK SHALL be skipped.

#### Scenario: Compilation succeeds
- **WHEN** `mvn compile` exits 0 with the given JDK
- **THEN** all profile runs for that JDK proceed

#### Scenario: Compilation fails (e.g., version mismatch)
- **WHEN** `mvn compile` exits non-zero for a JDK
- **THEN** all profile runs for that JDK are skipped and the failure is recorded as `COMPILE_FAILURE` in the report

---

### Requirement: Script invokes `benchmark.sh` per JDK × profile combination
For each valid JDK × profile pair the script SHALL invoke `benchmark.sh` with `JAVA_HOME` set to the JDK path, the profile's JVM flags merged into the benchmark environment, a unique temp output CSV, and the same target and additional args passed by the user.

#### Scenario: Successful benchmark run
- **WHEN** `benchmark.sh` exits 0 for a JDK × profile combination
- **THEN** the temp CSV rows are collected for merging

#### Scenario: Failed benchmark run
- **WHEN** `benchmark.sh` exits non-zero for a combination
- **THEN** the failure is noted in the summary; remaining combinations still run

---

### Requirement: Master CSV merges all results with JDK and Profile columns
The script SHALL merge all per-combination temp CSVs into a single master CSV. The master CSV header SHALL prepend `JDK,Profile,RunTimestamp` to the existing `benchmark.sh` schema columns. Each data row SHALL be prepended with the JDK label (from config or path basename), the profile name, and the run timestamp.

#### Scenario: Two JDKs × three profiles = six result sets
- **WHEN** six combination runs complete successfully
- **THEN** the master CSV contains six groups of rows, each correctly labeled with JDK, Profile, and RunTimestamp

#### Scenario: Master CSV column order
- **WHEN** the master CSV is opened
- **THEN** the first three columns are `JDK`, `Profile`, `RunTimestamp`, followed by the standard `benchmark.sh` columns: `Class,Name,JVM_OPTS,PARAMS,TASKSET,MedianRuntimeMs,...`
