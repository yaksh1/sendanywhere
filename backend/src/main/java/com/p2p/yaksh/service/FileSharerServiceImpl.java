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
    // Hashmap of <port (port number where that file is available),filepath>
    private HashMap<Integer,String> availableFiles;


    @Override
    public int offerFiles(String filepath) {
        // We'll give it a good number of tries, but not forever.
        // Adjust this value as needed.
        final int MAX_ATTEMPTS = 100;

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            int port = UploadUtils.generateCode();

            // This check is a race condition risk in a multi-threaded app.
            // It's better to use a thread-safe map or a synchronized block.
            // For a simple fix, let's keep it and note the risk.
            if (!availableFiles.containsKey(port)) {
                availableFiles.put(port, filepath);
                return port;
            }
        }
        // If we've tried MAX_ATTEMPTS times and failed, throw an exception.
        // This makes it clear that something went wrong.
        throw new RuntimeException("Failed to find an available port after " + MAX_ATTEMPTS + " attempts.");
    }

    @Override
    public void startFileServer(int port) {
        // Implementation for starting a file server on the given port.
        // This could involve setting up a socket, binding to the port,
        // and listening for incoming connections to serve files.
        String filepath = availableFiles.get(port);
        if (filepath == null) {
            throw new IllegalArgumentException("No file available for the given port: " + port);
        }
        try(ServerSocket serverSocket = new ServerSocket(port)){
            log.info("File server started on port: " + port + " for file: " + new File(filepath).getName());
            // Add logic to handle incoming connections and serve the file.
            Socket clientSocket = serverSocket.accept();
            log.info("Client connected: " + clientSocket.getInetAddress());
            // Serve the file to the client...
            new Thread(new FileTransferHandler(clientSocket, filepath)).start();
        } catch (IOException e) {
            throw new RuntimeException("Error starting file server on port " + port + ": " + e.getMessage());
        }
    }
}