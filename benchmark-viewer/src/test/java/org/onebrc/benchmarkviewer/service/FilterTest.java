package org.onebrc.benchmarkviewer.service;

import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@Transactional
public class FilterTest {
    @Autowired private EntityManager entityManager;

    @Test
    public void testFiltering() throws Exception {
        TestRun run = new TestRun();
        run.setTimestamp(java.time.LocalDateTime.now());
        entityManager.persist(run);
        
        Measurement m1 = new Measurement();
        m1.setTestRun(run);
        m1.setGcOpts("-XX:+UseG1GC");
        entityManager.persist(m1);
        
        entityManager.flush();
        org.hibernate.search.mapper.orm.Search.session(entityManager).workspace(Measurement.class).flush();

        List<Measurement> hits1 = org.hibernate.search.mapper.orm.Search.session(entityManager)
            .search(Measurement.class)
            .where(f -> f.matchAll())
            .fetchHits(100);
        System.out.println("ALL HITS: " + hits1.size());
        
        List<Measurement> hits2 = org.hibernate.search.mapper.orm.Search.session(entityManager)
            .search(Measurement.class)
            .where(f -> f.terms().field("gcOpts").matchingAny(List.of("-XX:+UseG1GC")))
            .fetchHits(100);
        System.out.println("TERMS HITS: " + hits2.size());
    }
}
