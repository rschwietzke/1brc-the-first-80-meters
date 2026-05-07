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

// AI-generated file: Gemini 3.1 Pro (High)

package org.onebrc.benchmarkviewer.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

/**
 * Represents a single execution permutation inside a TestRun.
 * Maps exactly to one row in the CSV result file.
 */
@Entity
@Table(name = "measurements", indexes = {
    @Index(name = "idx_measurement_run_id", columnList = "test_run_id")
})
@Indexed
public class Measurement
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_run_id", nullable = false)
    private TestRun testRun;

    @org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField(name = "testRunId")
    @org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency(derivedFrom = @org.hibernate.search.mapper.pojo.mapping.definition.annotation.ObjectPath(@org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyValue(propertyName = "testRun")))
    public Long getTestRunId() {
        return testRun != null ? testRun.getId() : null;
    }

    @KeywordField(aggregable = org.hibernate.search.engine.backend.types.Aggregable.YES)
    private String jdk;

    @KeywordField(aggregable = org.hibernate.search.engine.backend.types.Aggregable.YES)
    private String gcOpts;

    @KeywordField(aggregable = org.hibernate.search.engine.backend.types.Aggregable.YES)
    private String vmOpts;

    @KeywordField(aggregable = org.hibernate.search.engine.backend.types.Aggregable.YES)
    private String progOpts;

    @KeywordField(aggregable = org.hibernate.search.engine.backend.types.Aggregable.YES)
    private String binding;

    @KeywordField(aggregable = org.hibernate.search.engine.backend.types.Aggregable.YES)
    private String dataset;

    @KeywordField(aggregable = org.hibernate.search.engine.backend.types.Aggregable.YES)
    private String className;

    private String runTimestamp;

    private boolean error;

    // Metrics
    private double medianRuntimeMs;
    private double perfRuntimeMs;
    private double jfrRuntimeMs;
    private long instructions;
    private long cycles;
    private long branches;
    private long branchMisses;
    private long l1Misses;
    private long llcMisses;
    private long pageFaults;
    private long taskClock;
    private long contextSwitches;
    private long cpuMigrations;
    private double ipc;
    private double secElapsed;
    private double secUser;
    private double secSys;

    // Getters and Setters

    public Long getId()
    {
        return this.id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public TestRun getTestRun()
    {
        return this.testRun;
    }

    public void setTestRun(final TestRun testRun)
    {
        this.testRun = testRun;
    }

    public String getJdk()
    {
        return this.jdk;
    }

    public void setJdk(final String jdk)
    {
        this.jdk = jdk;
    }

    public String getGcOpts()
    {
        return this.gcOpts;
    }

    public void setGcOpts(final String gcOpts)
    {
        this.gcOpts = gcOpts;
    }

    public String getVmOpts()
    {
        return this.vmOpts;
    }

    public void setVmOpts(final String vmOpts)
    {
        this.vmOpts = vmOpts;
    }

    public String getProgOpts()
    {
        return this.progOpts;
    }

    public void setProgOpts(final String progOpts)
    {
        this.progOpts = progOpts;
    }

    public String getBinding()
    {
        return this.binding;
    }

    public void setBinding(final String binding)
    {
        this.binding = binding;
    }

    public String getDataset()
    {
        return this.dataset;
    }

    public void setDataset(final String dataset)
    {
        this.dataset = dataset;
    }

    public String getClassName()
    {
        return this.className;
    }

    public void setClassName(final String className)
    {
        this.className = className;
    }

    public String getRunTimestamp()
    {
        return this.runTimestamp;
    }

    public void setRunTimestamp(final String runTimestamp)
    {
        this.runTimestamp = runTimestamp;
    }

    public boolean isError()
    {
        return this.error;
    }

    public void setError(final boolean error)
    {
        this.error = error;
    }

    public double getMedianRuntimeMs()
    {
        return this.medianRuntimeMs;
    }

    public void setMedianRuntimeMs(final double medianRuntimeMs)
    {
        this.medianRuntimeMs = medianRuntimeMs;
    }

    public double getPerfRuntimeMs()
    {
        return this.perfRuntimeMs;
    }

    public void setPerfRuntimeMs(final double perfRuntimeMs)
    {
        this.perfRuntimeMs = perfRuntimeMs;
    }

    public double getJfrRuntimeMs()
    {
        return this.jfrRuntimeMs;
    }

    public void setJfrRuntimeMs(final double jfrRuntimeMs)
    {
        this.jfrRuntimeMs = jfrRuntimeMs;
    }

    public long getInstructions()
    {
        return this.instructions;
    }

    public void setInstructions(final long instructions)
    {
        this.instructions = instructions;
    }

    public long getCycles()
    {
        return this.cycles;
    }

    public void setCycles(final long cycles)
    {
        this.cycles = cycles;
    }

    public long getBranches()
    {
        return this.branches;
    }

    public void setBranches(final long branches)
    {
        this.branches = branches;
    }

    public long getBranchMisses()
    {
        return this.branchMisses;
    }

    public void setBranchMisses(final long branchMisses)
    {
        this.branchMisses = branchMisses;
    }

    public long getL1Misses()
    {
        return this.l1Misses;
    }

    public void setL1Misses(final long l1Misses)
    {
        this.l1Misses = l1Misses;
    }

    public long getLlcMisses()
    {
        return this.llcMisses;
    }

    public void setLlcMisses(final long llcMisses)
    {
        this.llcMisses = llcMisses;
    }

    public long getPageFaults()
    {
        return this.pageFaults;
    }

    public void setPageFaults(final long pageFaults)
    {
        this.pageFaults = pageFaults;
    }

    public long getTaskClock()
    {
        return this.taskClock;
    }

    public void setTaskClock(final long taskClock)
    {
        this.taskClock = taskClock;
    }

    public long getContextSwitches()
    {
        return this.contextSwitches;
    }

    public void setContextSwitches(final long contextSwitches)
    {
        this.contextSwitches = contextSwitches;
    }

    public long getCpuMigrations()
    {
        return this.cpuMigrations;
    }

    public void setCpuMigrations(final long cpuMigrations)
    {
        this.cpuMigrations = cpuMigrations;
    }

    public double getIpc()
    {
        return this.ipc;
    }

    public void setIpc(final double ipc)
    {
        this.ipc = ipc;
    }

    public double getSecElapsed()
    {
        return this.secElapsed;
    }

    public void setSecElapsed(final double secElapsed)
    {
        this.secElapsed = secElapsed;
    }

    public double getSecUser()
    {
        return this.secUser;
    }

    public void setSecUser(final double secUser)
    {
        this.secUser = secUser;
    }

    public double getSecSys()
    {
        return this.secSys;
    }

    public void setSecSys(final double secSys)
    {
        this.secSys = secSys;
    }

    @Transient
    public Double getCpi()
    {
        if (this.instructions > 0)
        {
            return (double) this.cycles / this.instructions;
        }
        return null;
    }

    @Transient
    public Double getBranchMissRate()
    {
        if (this.branches > 0)
        {
            return ((double) this.branchMisses / this.branches) * 100.0;
        }
        return null;
    }
}
