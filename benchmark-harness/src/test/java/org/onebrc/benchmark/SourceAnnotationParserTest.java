package org.onebrc.benchmark;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class SourceAnnotationParserTest {

    @Test
    public void testParseAnnotations() throws Exception
    {
        Path tempFile = Path.of(System.getProperty("java.io.tmpdir")).resolve("TestClass.java");
        try
        {
            Files.writeString(tempFile, 
                "package org.onebrc.test;\n" +
                "// ignore\n" +
                "// status: baseline\n" +
                "// -JDK:21\n" +
                "public class TestClass {}"
            );
        
            ClassConfig config = SourceAnnotationParser.parseFile(tempFile);
            
            assertTrue(config.ignore);
            assertEquals("baseline", config.status);
            assertTrue(config.exclusions.contains("JDK:21"));
            assertEquals("TestClass", config.className);
            assertEquals("org.onebrc.test.TestClass", config.fqcn);
        }
        finally
        {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testParseUnifiedAnnotations() throws Exception
    {
        Path tempFile = Path.of(System.getProperty("java.io.tmpdir")).resolve("TestClassUnified.java");
        try
        {
            Files.writeString(tempFile, 
                "package org.onebrc.test;\n" +
                "// -GC: SGC\n" +
                "// exclude-gc_opts: ZGC\n" +
                "// -VM_OPTS: MEM_1G\n" +
                "// exclude-vm: MEM_2G\n" +
                "// -DATASETS: DATASET_10k\n" +
                "// exclude-dataset: DATASET_10M\n" +
                "// -JDKS: JDK_21_OPEN\n" +
                "// -RUNS: NIGHTLY_ZGC\n" +
                "// GC: G1\n" +
                "// VM: MEM_4G\n" +
                "public class TestClassUnified {}"
            );

            ClassConfig config = SourceAnnotationParser.parseFile(tempFile);

            assertTrue(config.exclusions.contains("GC:SGC"));
            assertTrue(config.exclusions.contains("GC:ZGC"));
            assertTrue(config.exclusions.contains("VM:MEM_1G"));
            assertTrue(config.exclusions.contains("VM:MEM_2G"));
            assertTrue(config.exclusions.contains("DATA:DATASET_10k"));
            assertTrue(config.exclusions.contains("DATA:DATASET_10M"));
            assertTrue(config.exclusions.contains("JDK:JDK_21_OPEN"));
            assertTrue(config.exclusions.contains("RUN:NIGHTLY_ZGC"));

            assertTrue(config.inclusions.contains("GC:G1"));
            assertTrue(config.inclusions.contains("VM:MEM_4G"));
        }
        finally
        {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testScriptGeneratorRespectsExclusions() throws Exception
    {
        List<ClassConfig> classes = new ArrayList<>();
        ClassConfig cls1 = new ClassConfig("TestClassUnified", "org.onebrc.test.TestClassUnified");
        cls1.exclusions.add("GC:ZGC");
        cls1.exclusions.add("VM:MEM_2G");
        classes.add(cls1);

        BenchmarkConfig config = new BenchmarkConfig();
        config.jdks.put("JDK_21", new JdkConfig("JDK_21", "sdkman:21.0.6-tem"));
        config.gcOpts.put("G1", "-XX:+UseG1GC");
        config.gcOpts.put("ZGC", "-XX:+UseZGC");
        config.vmOpts.put("MEM_1G", "-Xms1g -Xmx1g");
        config.vmOpts.put("MEM_2G", "-Xms2g -Xmx2g");
        config.progOpts.put("DEFAULT", "-wc 0 -mc 1");
        config.bindings.put("CORES_8", "taskset -c 0-7");
        config.datasets.put("10k", new DatasetConfig("10k", "10000.txt"));

        Map<String, String> runProps = new LinkedHashMap<>();
        runProps.put("JDK_FILTER", "*");
        runProps.put("GC_FILTER", "*");
        runProps.put("VM_FILTER", "*");
        runProps.put("BINDING_FILTER", "CORES_8");
        runProps.put("PROG_FILTER", "DEFAULT");
        runProps.put("DATA_FILTER", "*");
        runProps.put("CLASS_FILTER", "*");
        config.runs.add(new RunDefinition("ALL", runProps));

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));
        try
        {
            ScriptGenerator.generate(classes, config, false, true, true, "");
        }
        finally
        {
            System.setOut(originalOut);
        }

        String output = outContent.toString();
        // Should compile with G1 and MEM_1G
        assertTrue(output.contains("-XX:+UseG1GC"));
        assertTrue(output.contains("-Xms1g"));
        
        // Should NOT compile with ZGC or MEM_2G due to exclusions
        assertFalse(output.contains("-XX:+UseZGC"));
        assertFalse(output.contains("-Xms2g"));
    }
}
