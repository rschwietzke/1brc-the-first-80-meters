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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Represents a single batch execution of benchmarks, parsed from the meta.json
 * and sysinfo.txt files.
 */
@Entity
@Table(name = "test_runs")
public class TestRun
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private int totalRuns;

    private String comment;

    private String hostname;
    private String kernelVersion;
    private String os;
    private String cpu;
    private Integer cpuCores;
    private String memory;

    public Long getId()
    {
        return this.id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public LocalDateTime getTimestamp()
    {
        return this.timestamp;
    }

    public void setTimestamp(final LocalDateTime timestamp)
    {
        this.timestamp = timestamp;
    }

    public int getTotalRuns()
    {
        return this.totalRuns;
    }

    public void setTotalRuns(final int totalRuns)
    {
        this.totalRuns = totalRuns;
    }

    public String getComment()
    {
        return this.comment;
    }

    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    public String getHostname()
    {
        return this.hostname;
    }

    public void setHostname(final String hostname)
    {
        this.hostname = hostname;
    }

    public String getKernelVersion()
    {
        return this.kernelVersion;
    }

    public void setKernelVersion(final String kernelVersion)
    {
        this.kernelVersion = kernelVersion;
    }

    public String getOs()
    {
        return this.os;
    }

    public void setOs(final String os)
    {
        this.os = os;
    }

    public String getCpu()
    {
        return this.cpu;
    }

    public void setCpu(final String cpu)
    {
        this.cpu = cpu;
    }

    public Integer getCpuCores()
    {
        return this.cpuCores;
    }

    public void setCpuCores(final Integer cpuCores)
    {
        this.cpuCores = cpuCores;
    }

    public String getMemory()
    {
        return this.memory;
    }

    public void setMemory(final String memory)
    {
        this.memory = memory;
    }
}
