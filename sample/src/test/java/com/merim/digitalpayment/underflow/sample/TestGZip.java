package com.merim.digitalpayment.underflow.sample;

import com.merim.digitalpayment.underflow.sample.tools.SimulatedSlowByteByByteInputStream;
import com.merim.digitalpayment.underflow.utils.SmartGZipBodyInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * TestGZip is a test class designed to validate the functionality of handling and processing GZip-compressed resources.
 * It provides methods to compare the content of original and test streams to ensure the GZip decompression process works correctly.
 */
public class TestGZip {

    /**
     * Tests the process of loading a GZip-compressed resource, decompressing it, and validating its contents
     * against the original uncompressed resource.
     * <p>
     * This test verifies the integrity of the `SmartGZipBodyInput` mechanism by comparing the decompressed
     * GZip input stream with the expected raw input stream. It ensures that the data content is consistent
     * before and after the GZip decompression process.
     *
     * @throws IOException if an I/O error occurs during the reading of the streams
     */
    @Test
    public void testLoadGzipResourceAndProcess() throws IOException {
        this.test(this.getClass().getClassLoader().getResourceAsStream("bigorder.json"),
                new SmartGZipBodyInput(
                        this.getClass().getClassLoader().getResourceAsStream("bigorder.json.gz")
                ).getInputStream());
    }

    /**
     * Tests the behavior of processing a GZip-compressed resource with simulated slow input.
     * <p>
     * This method verifies the integrity and performance of handling a GZip stream under the condition
     * where the input stream reads bytes slowly. It utilizes the SimulatedSlowByteByByteInputStream
     * to simulate a slow input and tests the consistency of the decompressed output when compared to
     * the original uncompressed resource.
     * <p>
     * The test ensures that the SmartGZipBodyInput correctly handles this scenario without compromising
     * data integrity or causing unexpected stream behavior. The comparison between the original and the
     * decompressed input ensures proper alignment of the data.
     *
     * @throws IOException if an I/O error occurs during the processing of streams
     */
    @Test
    public void testSlowInputSteam() throws IOException {
        // Run test on altered stream
        this.test(this.getClass().getClassLoader().getResourceAsStream("bigorder.json"),
                new SmartGZipBodyInput(
                        new SimulatedSlowByteByByteInputStream(
                                this.getClass().getClassLoader().getResourceAsStream("bigorder.json.gz")
                        )
                ).getInputStream());
    }

    /**
     * Compares the content of the original input stream with the test input stream to ensure they are identical.
     * <p>
     * This method reads both streams fully into memory and checks if their contents match.
     * The comparison ensures that the test stream produces the same data as the original stream.
     *
     * @param original   The original input stream to compare against.
     * @param testStream The input stream to test for content equality.
     * @throws IOException if an I/O error occurs while reading from the streams.
     */
    private void test(final InputStream original, final InputStream testStream) throws IOException {
        try (final InputStream originalInputStream = original; final InputStream testInputStream = testStream) {
            Assertions.assertNotNull(testInputStream, "The test stream should not be null.");
            Assertions.assertNotNull(originalInputStream, "The original stream should not be null.");

            final StringBuilder testContentBuilder = new StringBuilder();
            final StringBuilder originalContentBuilder = new StringBuilder();

            final byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = testInputStream.read(buffer)) != -1) {
                testContentBuilder.append(new String(buffer, 0, bytesRead));
            }

            while ((bytesRead = originalInputStream.read(buffer)) != -1) {
                originalContentBuilder.append(new String(buffer, 0, bytesRead));
            }

            Assertions.assertEquals(originalContentBuilder.toString(), testContentBuilder.toString(),
                    "The content of the streams should be equal.");
        }
    }
}
