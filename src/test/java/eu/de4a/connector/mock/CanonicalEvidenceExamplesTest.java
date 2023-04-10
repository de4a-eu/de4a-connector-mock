/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        log.info("testing canonical evidence example {}", example);
        try {
            File aFile = example.getResource().getFile();
            GenericJAXBMarshaller aMarshaller = example.getMarshaller();
            Assertions.assertTrue(aFile.exists(), () -> "Test file does not exists " + aFile.getAbsolutePath());

            final T aRead = (T) aMarshaller.read(aFile);
            Assertions.assertNotNull(aRead, "Failed to read " + aFile.getAbsolutePath());

            final byte[] aBytes = aMarshaller.getAsBytes(aRead);
            Assertions.assertNotNull(aBytes, "Failed to re-write " + aFile.getAbsolutePath());

            if (false) {
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
