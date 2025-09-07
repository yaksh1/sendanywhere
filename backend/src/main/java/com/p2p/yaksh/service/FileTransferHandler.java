package com.p2p.yaksh.service;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * Handles file transfer operations for a given client socket.
 * This class implements the Runnable interface to allow execution in a separate thread.
 */
@Slf4j
public class FileTransferHandler implements Runnable {
    private final Socket clientSocket; // The socket connection to the client
    private final String filePath; // The path of the file to be transferred

    /**
     * Constructor to initialize the FileTransferHandler with a client socket and a file path.
     *
     * @param clientSocket The socket connection to the client
     * @param filePath     The path of the file to be transferred
     */
    public FileTransferHandler(Socket clientSocket, String filePath) {
        this.clientSocket = clientSocket;
        this.filePath = filePath;
    }

    /**
     * The main logic for handling the file transfer.
     * Reads the file from the specified path and sends it to the client over the socket connection.
     */
    @Override
    public void run() {
        // Try-with-resources to ensure the file input stream is closed automatically
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            OutputStream outputStream = clientSocket.getOutputStream(); // Get the output stream of the client socket

            // Extract the file name from the file path and send it as a header to the client
            String fileName = new File(filePath).getName();
            String header = "Filename:" + fileName + "\n";
            outputStream.write(header.getBytes()); // Send the header to the client

            // Buffer for reading the file in chunks
            byte[] buffer = new byte[4096];
            int bytesRead;

            // Read the file and send it to the client in chunks
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead); // Write the chunk to the client
            }

            // Log the successful completion of the file transfer
            log.info("File transfer completed for file: " + fileName + " to client: " + clientSocket.getInetAddress());

        } catch (FileNotFoundException e) {
            // Handle the case where the file is not found
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            // Handle other I/O exceptions during file transfer
            throw new RuntimeException(e.getMessage());
        } finally {
            // Ensure the client socket is closed to release resources
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Log an error if the socket fails to close
                log.error("Error closing client socket: " + e.getMessage());
            }
        }
    }
}