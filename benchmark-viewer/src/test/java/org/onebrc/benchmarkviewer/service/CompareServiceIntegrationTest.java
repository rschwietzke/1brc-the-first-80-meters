package org.onebrc.benchmarkviewer.service;

import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.ComparisonCandidate;
import org.onebrc.benchmarkviewer.domain.ComparisonRow;
import org.onebrc.benchmarkviewer.domain.FilterState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

@SpringBootTest
public class CompareServiceIntegrationTest {

    @Autowired
    private CompareService compareService;

    @Test
    public void testCompare() {
        FilterState state = new FilterState();
        Set<ComparisonCandidate> candidates = compareService.getAllComparisonCandidates(state);
        System.out.println("Found " + candidates.size() + " candidates");
        if (candidates.size() >= 2) {
            ComparisonCandidate candA = candidates.iterator().next();
            ComparisonCandidate candB = candidates.stream().skip(1).findFirst().get();
            System.out.println("Comparing: " + candA + " vs " + candB);
            
            List<ComparisonRow> results = compareService.compareCandidates(state, candA, candB);
            System.out.println("Results size: " + results.size());
            if (results.isEmpty()) {
                System.err.println("RESULTS ARE EMPTY! SOMETHING IS WRONG");
            }
        }
    }
}
