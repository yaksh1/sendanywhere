package com.p2p.yaksh.service;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class FileTransferHandler implements Runnable {
    private final Socket clientSocket;
    private final String filePath;
    public FileTransferHandler(Socket clientSocket,String filePath) {
        this.clientSocket = clientSocket;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try(FileInputStream fileInputStream = new FileInputStream(filePath)){
            OutputStream outputStream = clientSocket.getOutputStream();
            String fileName = new File(filePath).getName();
            String header = "Filename:" + fileName + "\n";
            outputStream.write(header.getBytes());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            log.info("File transfer completed for file: " + fileName + " to client: " + clientSocket.getInetAddress());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.error("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
