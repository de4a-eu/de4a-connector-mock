package eu.de4a.connector.mock;

import com.helger.jaxb.GenericJAXBMarshaller;
import eu.de4a.connector.mock.exampledata.CanonicalEvidenceExamples;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


@Slf4j
public class CanonicalEvidenceExamplesTest {

    private static <T> void _testReadWrite (CanonicalEvidenceExamples example)
    {
        try {
            File aFile = example.getResource().getFile();
            GenericJAXBMarshaller aMarshaller = example.getMarshaller();
            Assertions.assertTrue(aFile.exists(), () -> "Test file does not exists " + aFile.getAbsolutePath());

            final T aRead = (T) aMarshaller.read(aFile);
            Assertions.assertNotNull(aRead, "Failed to read " + aFile.getAbsolutePath());

            final byte[] aBytes = aMarshaller.getAsBytes(aRead);
            Assertions.assertNotNull(aBytes, "Failed to re-write " + aFile.getAbsolutePath());

            if (true) {
                aMarshaller.setFormattedOutput(true);
                log.info(aMarshaller.getAsString(aRead));
            }
        } catch (IOException ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void testCanonicalEvidenceExamples()
    {
        Arrays.stream(CanonicalEvidenceExamples.values()).forEach(CanonicalEvidenceExamplesTest::_testReadWrite);
    }
}
