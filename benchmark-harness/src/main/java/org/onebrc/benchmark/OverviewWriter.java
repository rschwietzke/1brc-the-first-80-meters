package org.onebrc.benchmark;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewWriter {

    public static void write() throws IOException {
        Path outPath = Paths.get("data", "benchmark-history", "index.html");

        List<HistoryAggregator.RunSummary> summaries = HistoryAggregator.aggregate();

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(OverviewWriter.class, "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Map<String, Object> root = new HashMap<>();
        root.put("summaries", summaries);
        root.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // To feed ECharts, we want chronological order (oldest to newest)
        List<HistoryAggregator.RunSummary> chronological = new java.util.ArrayList<>(summaries);
        java.util.Collections.reverse(chronological);
        root.put("chronological", chronological);

        Template template = cfg.getTemplate("index.html.ftl");

        try (FileWriter out = new FileWriter(outPath.toFile())) {
            template.process(root, out);
            System.out.println("Generated Overview Dashboard: " + outPath);
        } catch (Exception e) {
            throw new IOException("Failed to process Freemarker template", e);
        }
    }
}
