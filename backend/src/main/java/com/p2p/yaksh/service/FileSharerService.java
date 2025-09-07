package com.p2p.yaksh.service;

/**
 * FileSharerService is an interface that defines the contract for file sharing operations.
 * Implementing classes should provide the logic for sharing files in a peer-to-peer system.
 */
public interface FileSharerService {

    /**
     * Offers files for sharing by specifying the file path.
     *
     * @param filepath the path of the file to be shared
     * @return an integer indicating the success or status of the file offering operation
     */
    public int offerFiles(String filepath);

    /**
     * Starts a file server to listen for incoming connections on the specified port.
     *
     * @param port the port number on which the file server will listen
     */
    public void startFileServer(int port);
}