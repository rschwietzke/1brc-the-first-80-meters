/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// AI-generated file: Claude Opus 4.6 (Thinking)

package org.onebrc.benchmarkviewer.controller;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.MeasurementRepository;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.onebrc.benchmarkviewer.util.ViewerDataLocator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for the individual measurement detail view and JFR file downloads.
 *
 * <p>Serves the drill-down page when a user clicks a cell in the variation matrix.
 * Also provides a secure download endpoint for JFR (Java Flight Recorder) files
 * that are associated with a given measurement.</p>
 */
@Controller
@Transactional(readOnly = true)
public class DetailController
{
    /** Formatter used to build the JFR file name from a timestamp. */
    private static final DateTimeFormatter JFR_TIMESTAMP_FORMATTER =
        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final TestRunRepository testRunRepository;
    private final MeasurementRepository measurementRepository;
    private final String benchmarkDataDirectory;

    /**
     * Construct the detail controller.
     *
     * @param testRunRepository      repository for looking up test runs
     * @param measurementRepository  repository for looking up measurements
     * @param benchmarkDataDirectory configured path to the benchmark data directory
     */
    public DetailController(final TestRunRepository testRunRepository,
                            final MeasurementRepository measurementRepository,
                            @Value("${benchmark.data.directory:}") final String benchmarkDataDirectory)
    {
        this.testRunRepository = testRunRepository;
        this.measurementRepository = measurementRepository;
        this.benchmarkDataDirectory = benchmarkDataDirectory;
    }

    /**
     * Display the detail view for a single measurement within a test run.
     *
     * <p>Parses the ISO-8601 timestamp to locate the parent {@link TestRun},
     * fetches the {@link Measurement} by ID, and verifies the measurement
     * belongs to the specified run. Also resolves whether a matching JFR
     * file exists on disk.</p>
     *
     * @param timestampStr ISO-8601 formatted timestamp identifying the test run
     * @param id           the measurement entity ID
     * @param hxRequest    HTMX request header, present when the call is an HTMX partial
     * @param model        the Spring MVC model
     * @return the Thymeleaf view name — a fragment for HTMX or a full page fallback
     */
    @GetMapping("/runs/{timestampStr}/detail/{id}")
    public String getDetail(
        @PathVariable("timestampStr") final String timestampStr,
        @PathVariable("id") final Long id,
        @RequestHeader(value = "HX-Request", required = false) final String hxRequest,
        final Model model)
    {
        final LocalDateTime timestamp;
        try
        {
            timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        catch (final DateTimeParseException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timestamp format");
        }

        final TestRun testRun = this.testRunRepository.findByTimestamp(timestamp)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test run not found"));

        final Measurement measurement = this.measurementRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Measurement not found"));

        // Confirm measurement belongs to the test run
        if (!measurement.getTestRun().getId().equals(testRun.getId()))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Measurement does not belong to specified run");
        }

        final String jfrFileName = this.getExpectedJfrFileName(measurement, testRun);
        final boolean hasJfrFile = this.resolveJfrPath(jfrFileName) != null
            && Files.exists(this.resolveJfrPath(jfrFileName));

        final List<Measurement> history = this.measurementRepository.findByClassNameAndJdkAndGcOptsAndVmOptsAndProgOptsAndBindingAndDatasetOrderByTestRunTimestampDesc(
            measurement.getClassName(), measurement.getJdk(), measurement.getGcOpts(), measurement.getVmOpts(),
            measurement.getProgOpts(), measurement.getBinding(), measurement.getDataset()
        );

        model.addAttribute("testRun", testRun);
        model.addAttribute("measurement", measurement);
        model.addAttribute("history", history);
        model.addAttribute("hasJfrFile", hasJfrFile);
        model.addAttribute("jfrFileName", jfrFileName);

        if ("true".equals(hxRequest))
        {
            return "fragments/detail :: htmx-response";
        }

        // Full-page fallback: render inside the layout by forwarding to a
        // dedicated template that decorates the layout and includes the fragment.
        return "detail";
    }

    /**
     * Download a JFR (Java Flight Recorder) file by file name.
     *
     * <p>Performs path-traversal protection by normalizing the resolved path
     * and verifying it still falls within the expected {@code benchmark-jfr}
     * directory.</p>
     *
     * @param fileName the JFR file name to download
     * @return the binary JFR file as a download attachment
     */
    @GetMapping("/jfr/download/{fileName}")
    public ResponseEntity<Resource> downloadJfr(@PathVariable("fileName") final String fileName)
    {
        final Path jfrPath = this.resolveJfrPath(fileName);

        if (jfrPath == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "JFR directory not configured");
        }

        // Path traversal protection
        final Path jfrDir = jfrPath.getParent();
        if (!jfrPath.normalize().startsWith(jfrDir))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid path");
        }

        if (!Files.exists(jfrPath))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "JFR file not found");
        }

        try
        {
            final Resource resource = new UrlResource(jfrPath.toUri());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        }
        catch (final MalformedURLException e)
        {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error resolving JFR file", e);
        }
    }

    /**
     * Resolve the absolute path to a JFR file within the {@code benchmark-jfr}
     * sibling directory of the configured data directory.
     *
     * @param fileName the JFR file name
     * @return the resolved absolute path, or {@code null} if the data directory is not configured
     */
    private Path resolveJfrPath(final String fileName)
    {
        if (this.benchmarkDataDirectory == null || this.benchmarkDataDirectory.isBlank())
        {
            return null;
        }

        if (fileName == null || fileName.length() < 16)
        {
            return null;
        }

        final String timestamp = fileName.substring(0, 15);
        final Path dataDir = Paths.get(this.benchmarkDataDirectory).toAbsolutePath();
        return ViewerDataLocator.getJfrDir(dataDir, timestamp).resolve(fileName).normalize();
    }

    /**
     * Construct the expected JFR file name from measurement and test-run metadata.
     *
     * <p>The naming convention mirrors the bash test-runner scripts:
     * {@code timestamp-ClassName-JDK-GCOpts_VMOpts_Binding-Dataset.jfr}.
     * Special characters ({@code +}, {@code :}, spaces) are replaced with underscores.</p>
     *
     * @param m the measurement providing class name and JVM configuration
     * @param t the test run providing the timestamp
     * @return the expected JFR file name
     */
    private String getExpectedJfrFileName(final Measurement m, final TestRun t)
    {
        String classSimpleName = m.getClassName();
        if (classSimpleName != null && classSimpleName.contains("."))
        {
            classSimpleName = classSimpleName.substring(classSimpleName.lastIndexOf('.') + 1);
        }

        final String gcOpts = this.sanitizeOpts(m.getGcOpts());
        final String vmOpts = this.sanitizeOpts(m.getVmOpts());
        final String binding = this.sanitizeOpts(m.getBinding());
        final String tsStr = t.getTimestamp().format(JFR_TIMESTAMP_FORMATTER);

        return tsStr + "-"
            + classSimpleName + "-"
            + m.getJdk() + "-"
            + gcOpts + "_"
            + vmOpts + "_"
            + binding + "-"
            + m.getDataset() + ".jfr";
    }

    /**
     * Replace special characters in JVM option strings with underscores
     * to match the bash test-runner's file naming convention.
     *
     * @param opts the raw option string (may be {@code null})
     * @return a sanitized string safe for use in file names
     */
    private String sanitizeOpts(final String opts)
    {
        if (opts == null)
        {
            return "";
        }
        return opts.replace("+", "_").replace(":", "_").replace(" ", "_");
    }
}
