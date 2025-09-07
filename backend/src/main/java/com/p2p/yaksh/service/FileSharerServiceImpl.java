package com.p2p.yaksh.service;

import com.p2p.yaksh.utils.UploadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Implementation of the FileSharerService interface.
 * This class is responsible for providing the functionality 
 * required to share files in a peer-to-peer network.
 */
@RequiredArgsConstructor
@Slf4j
public class FileSharerServiceImpl implements FileSharerService {
    // A HashMap to store available files with their corresponding port numbers.
    // Key: Port number, Value: File path
    private HashMap<Integer, String> availableFiles;

    /**
     * Offers a file for sharing by associating it with an available port.
     * Tries to find an unused port and maps it to the provided file path.
     *
     * @param filepath the path of the file to be shared
     * @return the port number on which the file is being offered
     * @throws RuntimeException if an available port cannot be found after a maximum number of attempts
     */
    @Override
    public int offerFiles(String filepath) {
        // Maximum number of attempts to find an available port
        final int MAX_ATTEMPTS = 100;

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            int port = UploadUtils.generateCode(); // Generate a random port number

            // Check if the port is already in use
            if (!availableFiles.containsKey(port)) {
                availableFiles.put(port, filepath); // Map the port to the file path
                return port; // Return the available port
            }
        }
        // Throw an exception if no available port is found after MAX_ATTEMPTS
        throw new RuntimeException("Failed to find an available port after " + MAX_ATTEMPTS + " attempts.");
    }

    /**
     * Starts a file server on the specified port to allow clients to download the file.
     * The file to be served is determined based on the port.
     *
     * @param port the port number on which the file server will run
     * @throws IllegalArgumentException if no file is associated with the given port
     * @throws RuntimeException if an I/O error occurs while starting the server
     */
    @Override
    public void startFileServer(int port) {
        // Retrieve the file path associated with the given port
        String filepath = availableFiles.get(port);
        if (filepath == null) {
            // Throw an exception if no file is associated with the port
            throw new IllegalArgumentException("No file available for the given port: " + port);
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Log the start of the file server
            log.info("File server started on port: " + port + " for file: " + new File(filepath).getName());

            // Wait for a client to connect
            Socket clientSocket = serverSocket.accept();
            log.info("Client connected: " + clientSocket.getInetAddress());

            // Start a new thread to handle file transfer for the connected client
            new Thread(new FileTransferHandler(clientSocket, filepath)).start();
        } catch (IOException e) {
            // Throw a runtime exception if an I/O error occurs
            throw new RuntimeException("Error starting file server on port " + port + ": " + e.getMessage());
        }
    }
}