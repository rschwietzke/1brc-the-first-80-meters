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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Dashboard controller serving the main landing page.
 *
 * <p>Currently renders a placeholder page using the Thymeleaf layout.
 * Will be extended in later steps to display the list of imported test runs.</p>
 */
@Controller
public class DashboardController
{
    /**
     * Render the landing page (test run list or placeholder).
     *
     * @return the Thymeleaf view name for the index page
     */
    @GetMapping("/")
    public String index()
    {
        return "index";
    }
}
