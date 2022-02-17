package net.realact.pavlovstats.lib;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * Binds to a specified port number on the local host and waits for a connection request from a client. Once a
 * connection is established, it operates in one of two modes: download and upload.
 *
 * @author tlmader.dev@gmail.com
 * @since 2017-02-27
 */
@SuppressWarnings("JavaDoc")
public class NetcatClient {

    private static Socket clientSocket;
    private static BufferedReader inFromServer;
    public static NetcatIOStub ioStub = new NetcatIOStub();

    /**
     * Creates client socket makes request to the server.
     *
     * @throws Exception
     */
    public static void start(String host, int port) throws Exception {
        if (clientSocket == null) {
            clientSocket = new Socket(host, port);
        }
        clientSocket.close();
    }

    /**
     * In download mode, client reads data from the socket and writes it to standard output.
     *
     * @throws Exception
     */
    public static void download() throws Exception {
        if (inFromServer == null) {
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        String line;
        while ((line = inFromServer.readLine()) != null) {
            ioStub.writeOutputLine(line);
        }
    }

    /**
     * In upload mode, client reads data from its standard input device and writes it to the socket.
     *
     * @throws Exception
     */
    @SuppressWarnings("Duplicates")
    public static void upload() throws Exception {
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        String output = ioStub.readOutputline();
        while(output != null){
            outToServer.writeBytes(output);
        }
    }

    public static NetcatIOStub getIoStub(){
        return ioStub;
    }
}