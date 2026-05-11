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

package org.onebrc.benchmarkviewer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.FilterState;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.MeasurementRepository;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SearchService}.
 *
 * <p>Verifies faceted search and aggregation behaviour using an in-memory
 * H2 database and Lucene local-heap index.</p>
 */
@SpringBootTest
@Transactional
class SearchServiceTest
{
    @Autowired
    private SearchService searchService;

    @Autowired
    private TestRunRepository testRunRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private EntityManager entityManager;

    private TestRun testRun;

    @BeforeEach
    void setup() throws InterruptedException
    {
        this.measurementRepository.deleteAll();
        this.testRunRepository.deleteAll();

        this.testRun = new TestRun();
        this.testRun.setTimestamp(LocalDateTime.now());
        this.testRun.setTotalRuns(1);
        this.testRunRepository.save(this.testRun);

        final Measurement m1 = new Measurement();
        m1.setTestRun(this.testRun);
        m1.setJdk("21");
        m1.setGcOpts("-XX:+UseZGC");
        m1.setVmOpts("");
        m1.setProgOpts("");
        m1.setBinding("none");
        m1.setDataset("1M");
        m1.setClassName("BRC01");
        this.measurementRepository.save(m1);

        final Measurement m2 = new Measurement();
        m2.setTestRun(this.testRun);
        m2.setJdk("21");
        m2.setGcOpts("-XX:+UseG1GC");
        m2.setVmOpts("");
        m2.setProgOpts("");
        m2.setBinding("none");
        m2.setDataset("1M");
        m2.setClassName("BRC01");
        this.measurementRepository.save(m2);

        final Measurement m3 = new Measurement();
        m3.setTestRun(this.testRun);
        m3.setJdk("25");
        m3.setGcOpts("-XX:+UseZGC");
        m3.setVmOpts("");
        m3.setProgOpts("");
        m3.setBinding("none");
        m3.setDataset("1M");
        m3.setClassName("BRC01");
        this.measurementRepository.save(m3);

        this.entityManager.flush();
        Search.session(this.entityManager).indexingPlan().execute();
    }

    @Test
    @DisplayName("5.1: getFacetCounts() returns correct counts for unfiltered state")
    void testGetFacetCountsUnfiltered()
    {
        final FilterState state = new FilterState();
        final Map<String, Map<String, Long>> counts =
            this.searchService.getFacetCounts(state, this.testRun.getId(), null);

        assertThat(counts).containsKey("jdk");
        assertThat(counts.get("jdk")).containsEntry("21", 2L);
        assertThat(counts.get("jdk")).containsEntry("25", 1L);

        assertThat(counts).containsKey("gcOpts");
        assertThat(counts.get("gcOpts")).containsEntry("-XX:+UseZGC", 2L);
        assertThat(counts.get("gcOpts")).containsEntry("-XX:+UseG1GC", 1L);
    }

    @Test
    @DisplayName("5.2: getFacetCounts() applying a JDK filter reduces counts in other dimensions")
    void testGetFacetCountsFiltered()
    {
        final FilterState state = new FilterState();
        state.setJdk(List.of("25")); // only m3 matches

        final Map<String, Map<String, Long>> counts =
            this.searchService.getFacetCounts(state, this.testRun.getId(), null);

        // The facet for the *filtered* field (jdk) should still show all options
        // to allow selecting others, or at least the selection itself.
        // In typical faceted search, filtering on X recalculates Y, but X facets
        // are either preserved or show 0 for others.
        // Let's just check the other dimensions.
        assertThat(counts.get("gcOpts")).containsEntry("-XX:+UseZGC", 1L);
        assertThat(counts.get("gcOpts")).doesNotContainEntry("-XX:+UseG1GC", 1L);
    }

    @Test
    @DisplayName("5.3: searchMeasurements() returns only matching measurements")
    void testSearchMeasurementsFiltered()
    {
        final FilterState state = new FilterState();
        state.setJdk(List.of("21"));
        state.setGcOpts(List.of("-XX:+UseZGC"));

        final List<Measurement> results =
            this.searchService.searchMeasurements(state, this.testRun.getId(), null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getJdk()).isEqualTo("21");
        assertThat(results.get(0).getGcOpts()).isEqualTo("-XX:+UseZGC");
    }
}
