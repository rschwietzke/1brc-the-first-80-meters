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

package org.onebrc.benchmarkviewer.service;

import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;

@Component
public class StartupImporter implements CommandLineRunner
{
    private static final Logger log = LoggerFactory.getLogger(StartupImporter.class);

    private final DataImportService dataImportService;
    private final EntityManager entityManager;
    private final String dataDir;

    public StartupImporter(final DataImportService dataImportService, 
                           final EntityManager entityManager,
                           @Value("${benchmark.data.directory:data/benchmark-history}") final String dataDir)
    {
        this.dataImportService = dataImportService;
        this.entityManager = entityManager;
        this.dataDir = dataDir;
    }

    @Override
    @Transactional
    public void run(final String... args) throws Exception
    {
        log.info("Starting initial benchmark data import from {}", this.dataDir);
        this.dataImportService.importDirectory(Path.of(this.dataDir));
        log.info("Data import complete. Starting mass indexer.");

        final SearchSession searchSession = Search.session(this.entityManager);
        searchSession.massIndexer()
                .idFetchSize(150)
                .batchSizeToLoadObjects(25)
                .threadsToLoadObjects(4)
                .startAndWait();

        log.info("Mass indexer complete.");
    }
}
