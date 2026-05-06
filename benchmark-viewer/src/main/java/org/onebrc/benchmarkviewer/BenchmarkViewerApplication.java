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

package org.onebrc.benchmarkviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Benchmark Viewer application.
 *
 * <p>This Spring Boot application provides an interactive web-based explorer
 * for benchmark results. It imports CSV performance data into an H2 database,
 * indexes it with Hibernate Search (Lucene backend) for faceted navigation,
 * and renders an HTMX-driven UI with Thymeleaf and Bootstrap 5.</p>
 */
@SpringBootApplication
public class BenchmarkViewerApplication
{
    /**
     * Launch the Benchmark Viewer application.
     *
     * @param args command-line arguments passed to Spring Boot
     */
    public static void main(final String[] args)
    {
        SpringApplication.run(BenchmarkViewerApplication.class, args);
    }
}
