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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.onebrc.benchmarkviewer.domain.FilterState;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Full-text search service backed by Hibernate Search (Lucene).
 *
 * <p>Provides filtered measurement queries and facet-count aggregation
 * for the sidebar filter UI. Each facet field's counts are computed
 * independently of its own active filter to allow "drill-down" semantics.</p>
 */
@Service
@Transactional(readOnly = true)
public class SearchService
{
    /** All facetable dimension fields on the {@link Measurement} entity. */
    private static final List<String> FACET_FIELDS = List.of(
        "jdk", "gcOpts", "vmOpts", "progOpts", "binding", "dataset"
    );

    /** Maximum number of hits to fetch per search (safety cap). */
    private static final int MAX_HITS = 10_000;

    private final EntityManager entityManager;

    /**
     * Construct the search service.
     *
     * @param entityManager JPA entity manager used to obtain the Hibernate Search session
     */
    public SearchService(final EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    /**
     * Search for measurements within a test run, applying any active filters.
     *
     * @param state     the current filter state from the UI (may be {@code null})
     * @param testRunId the ID of the test run to scope the search to
     * @return a list of matching measurements, up to {@value #MAX_HITS}
     */
    public List<Measurement> searchMeasurements(final FilterState state, final Long testRunId)
    {
        final SearchSession searchSession = Search.session(this.entityManager);

        return searchSession.search(Measurement.class)
            .where(f -> f.bool(b ->
            {
                b.must(f.match().field("testRunId").matching(testRunId));

                if (state != null)
                {
                    final Map<String, List<String>> activeFilters = state.getActiveFilters();
                    for (final Map.Entry<String, List<String>> entry : activeFilters.entrySet())
                    {
                        final String field = entry.getKey();
                        final List<String> values = entry.getValue();
                        if (values != null && !values.isEmpty())
                        {
                            b.must(f.terms().field(field).matchingAny(values));
                        }
                    }
                }
            }))
            .fetchHits(MAX_HITS);
    }

    /**
     * Compute facet counts for all filter dimensions.
     *
     * <p>When a filter is active on one dimension, it constrains the counts
     * of <em>other</em> dimensions but not its own. This gives the user an
     * accurate picture of available values within each facet group.</p>
     *
     * @param state     the current filter state from the UI (may be {@code null})
     * @param testRunId the ID of the test run to scope the aggregation to
     * @return a map from field name to (term → count) pairs
     */
    public Map<String, Map<String, Long>> getFacetCounts(final FilterState state, final Long testRunId)
    {
        final SearchSession searchSession = Search.session(this.entityManager);
        final Map<String, Map<String, Long>> allFacets = new HashMap<>();

        for (final String field : FACET_FIELDS)
        {
            final AggregationKey<Map<String, Long>> facetKey = AggregationKey.of(field);

            final SearchResult<Measurement> result = searchSession.search(Measurement.class)
                .where(f -> f.bool(b ->
                {
                    b.must(f.match().field("testRunId").matching(testRunId));

                    if (state != null)
                    {
                        final Map<String, List<String>> activeFilters = state.getActiveFilters();
                        for (final Map.Entry<String, List<String>> entry : activeFilters.entrySet())
                        {
                            final String filterField = entry.getKey();
                            final List<String> values = entry.getValue();
                            // Exclude the current facet field's own filter to allow drill-down
                            if (values != null && !values.isEmpty() && !filterField.equals(field))
                            {
                                b.must(f.terms().field(filterField).matchingAny(values));
                            }
                        }
                    }
                }))
                .aggregation(facetKey, f -> f.terms().field(field, String.class).maxTermCount(100))
                .fetch(0); // only aggregations needed, no hits

            allFacets.put(field, result.aggregation(facetKey));
        }

        return allFacets;
    }
}
