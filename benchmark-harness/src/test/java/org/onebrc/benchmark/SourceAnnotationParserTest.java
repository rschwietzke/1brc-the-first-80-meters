package org.onebrc.benchmark;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class SourceAnnotationParserTest {

    @Test
    public void testParseAnnotations() throws Exception {
        Path tempFile = Files.createTempFile("TestClass", ".java");
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
}
