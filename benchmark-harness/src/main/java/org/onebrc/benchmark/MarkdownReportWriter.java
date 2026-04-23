package org.onebrc.benchmark;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MarkdownReportWriter {

    public static void write(String timestamp, ResultMatrix matrix) throws IOException {
        Path outPath = Paths.get("data", "benchmark-history", timestamp + ".md");

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(MarkdownReportWriter.class, "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Map<String, Object> root = new HashMap<>();
        root.put("timestamp", timestamp);

        Set<String> datasets = matrix.getDatasets();
        Set<String> classes = matrix.getClasses();
        Set<String> environments = matrix.getEnvironments();

        root.put("datasets", datasets);
        root.put("classes", classes);
        root.put("environments", environments);

        Map<String, ResultMatrix.RowData> flatMatrix = new HashMap<>();
        for (String ds : datasets) {
            for (String cls : classes) {
                for (String env : environments) {
                    String[] parts = env.split(" \\| ", -1);
                    if (parts.length < 5) continue; // Safety check
                    ResultMatrix.Key k = new ResultMatrix.Key(parts[0], parts[1], parts[2], parts[3], parts[4], ds, cls);
                    ResultMatrix.RowData rd = matrix.get(k);
                    if (rd != null) {
                        flatMatrix.put(env + " | " + ds + " | " + cls, rd);
                    }
                }
            }
        }
        root.put("matrix", flatMatrix);

        Template template = cfg.getTemplate("report.md.ftl");

        try (FileWriter out = new FileWriter(outPath.toFile())) {
            template.process(root, out);
            System.out.println("Generated Markdown report: " + outPath);
        } catch (Exception e) {
            throw new IOException("Failed to process Freemarker template", e);
        }
    }
}
