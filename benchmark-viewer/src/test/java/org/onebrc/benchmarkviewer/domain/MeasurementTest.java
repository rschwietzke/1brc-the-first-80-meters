package org.onebrc.benchmarkviewer.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeasurementTest {

    @Test
    void testDerivedMetricsWithValidData() {
        Measurement m = new Measurement();
        m.setInstructions(1000L);
        m.setCycles(500L);
        m.setBranches(200L);
        m.setBranchMisses(10L);

        m.setIpc(2.0); // Now it's just a setter

        assertEquals(2.0, m.getIpc());
        assertEquals(0.5, m.getCpi());
        assertEquals(5.0, m.getBranchMissRate());
    }

    @Test
    void testDerivedMetricsWithZeroDenominators() {
        Measurement m = new Measurement();
        m.setInstructions(0L);
        m.setCycles(0L);
        m.setBranches(0L);
        m.setBranchMisses(0L);

        assertNull(m.getCpi());
        assertNull(m.getBranchMissRate());
    }

    @Test
    void testGcAndAllocationFields() {
        Measurement m = new Measurement();
        m.setGcPauseMs(45.2);
        m.setAllocatedBytes(21879136L);
        m.setJitCompilationMs(123.4);

        assertEquals(45.2, m.getGcPauseMs());
        assertEquals(21879136L, m.getAllocatedBytes());
        assertEquals(123.4, m.getJitCompilationMs());
    }

    @Test
    void testGcAndAllocationFieldsDefaultToZero() {
        Measurement m = new Measurement();

        assertEquals(0.0, m.getGcPauseMs());
        assertEquals(0L, m.getAllocatedBytes());
        assertEquals(0.0, m.getJitCompilationMs());
    }
}
