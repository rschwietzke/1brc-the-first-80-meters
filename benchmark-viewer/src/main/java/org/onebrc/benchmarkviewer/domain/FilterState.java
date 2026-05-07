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

package org.onebrc.benchmarkviewer.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents the current active filters selected by the user in the sidebar.
 *
 * <p>Each field corresponds to a facetable dimension on the {@link Measurement}
 * entity. Spring MVC binds multi-valued query parameters (e.g.
 * {@code ?jdk=21&jdk=25}) directly into the list fields.</p>
 */
public class FilterState
{
    private List<String> jdk = Collections.emptyList();
    private List<String> gcOpts = Collections.emptyList();
    private List<String> vmOpts = Collections.emptyList();
    private List<String> progOpts = Collections.emptyList();
    private List<String> binding = Collections.emptyList();
    private List<String> dataset = Collections.emptyList();

    /** @return the active JDK version filters */
    public List<String> getJdk()
    {
        return this.jdk;
    }

    /** @param jdk the JDK version filters to set */
    public void setJdk(final List<String> jdk)
    {
        this.jdk = jdk != null ? jdk : Collections.emptyList();
    }

    /** @return the active GC option filters */
    public List<String> getGcOpts()
    {
        return this.gcOpts;
    }

    /** @param gcOpts the GC option filters to set */
    public void setGcOpts(final List<String> gcOpts)
    {
        this.gcOpts = gcOpts != null ? gcOpts : Collections.emptyList();
    }

    /** @return the active VM option filters */
    public List<String> getVmOpts()
    {
        return this.vmOpts;
    }

    /** @param vmOpts the VM option filters to set */
    public void setVmOpts(final List<String> vmOpts)
    {
        this.vmOpts = vmOpts != null ? vmOpts : Collections.emptyList();
    }

    /** @return the active program option filters */
    public List<String> getProgOpts()
    {
        return this.progOpts;
    }

    /** @param progOpts the program option filters to set */
    public void setProgOpts(final List<String> progOpts)
    {
        this.progOpts = progOpts != null ? progOpts : Collections.emptyList();
    }

    /** @return the active CPU binding filters */
    public List<String> getBinding()
    {
        return this.binding;
    }

    /** @param binding the CPU binding filters to set */
    public void setBinding(final List<String> binding)
    {
        this.binding = binding != null ? binding : Collections.emptyList();
    }

    /** @return the active dataset size filters */
    public List<String> getDataset()
    {
        return this.dataset;
    }

    /** @param dataset the dataset size filters to set */
    public void setDataset(final List<String> dataset)
    {
        this.dataset = dataset != null ? dataset : Collections.emptyList();
    }

    /**
     * Returns a map of field name to the list of active filters for that field.
     *
     * @return an unmodifiable map of all active filter dimensions
     */
    public Map<String, List<String>> getActiveFilters()
    {
        return Map.of(
            "jdk", this.jdk,
            "gcOpts", this.gcOpts,
            "vmOpts", this.vmOpts,
            "progOpts", this.progOpts,
            "binding", this.binding,
            "dataset", this.dataset
        );
    }

    /**
     * Check whether all filter dimensions are empty.
     *
     * @return {@code true} if no filters are active
     */
    public boolean isEmpty()
    {
        return this.jdk.isEmpty()
            && this.gcOpts.isEmpty()
            && this.vmOpts.isEmpty()
            && this.progOpts.isEmpty()
            && this.binding.isEmpty()
            && this.dataset.isEmpty();
    }
}
