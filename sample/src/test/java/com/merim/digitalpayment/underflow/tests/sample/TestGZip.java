package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.tests.sample.tools.SimulatedSlowByteByByteInputStream;
import com.merim.digitalpayment.underflow.utils.SmartGZipBodyInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * TestGZip.
 * <p>
 * This class demonstrates how to test loading `.gz` files and processing them using SmartGZipBodyInput.
 *
 * @author Pierre Adam
 * @since 25.04.14
 */
public class TestGZip {

    /**
     * Test load gzip resource and process.
     *
     * @throws IOException the io exception
     */
    @Test
    public void testLoadGzipResourceAndProcess() throws IOException {
        // Load the .gz file from the resources folder
        try (final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bigorder.json.gz")) {
            // Ensure the file exists in the resources directory
            Assertions.assertNotNull(inputStream, "The .gz file should exist.");

            // Create an instance of SmartGZipBodyInput
            try (final InputStream unzippedInputStream = new SmartGZipBodyInput(inputStream).getInputStream()) {
                Assertions.assertNotNull(unzippedInputStream);

                System.out.println("Unzipped file content: " + new String(unzippedInputStream.readAllBytes()));
            }
        }
    }

    /**
     * Test slow input steam.
     *
     * @throws IOException the io exception
     */
    @Test
    public void testSlowInputSteam() throws IOException {
        // Load the .gz file from the resources folder
        try (final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bigorder.json.gz")) {
            // Ensure the file exists in the resources directory
            Assertions.assertNotNull(inputStream, "The .gz file should exist.");

            // Create an instance of SmartGZipBodyInput
            try (final InputStream unzippedInputStream = new SmartGZipBodyInput(new SimulatedSlowByteByByteInputStream(inputStream)).getInputStream()) {
                Assertions.assertNotNull(unzippedInputStream);

                System.out.println("Unzipped file content: " + new String(unzippedInputStream.readAllBytes()));
            }
        }
    }
}
