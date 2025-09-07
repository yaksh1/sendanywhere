package com.p2p.yaksh.utils;

/**
 * Utility class for handling upload-related operations.
 */
public class UploadUtils {

    /**
     * Generates a random port number within the dynamic/private port range.
     *
     * @return A randomly generated port number between 49152 and 65535 (inclusive).
     */
    public static int generateCode() {
        // Define the starting port number for the dynamic/private port range.
        int DYNAMIC_STARTING_PORT = 49152;

        // Define the ending port number for the dynamic/private port range.
        int DYNAMIC_ENDING_PORT = 65535;

        // Generate a random port number within the specified range.
        return (int) (Math.random() * (DYNAMIC_ENDING_PORT - DYNAMIC_STARTING_PORT + 1) + DYNAMIC_STARTING_PORT);
    }
}