package net.realact.pavlovstats.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.realact.pavlovstats.config.AppConfig;
import net.realact.pavlovstats.models.commands.Command;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Basically a 1:1 port of this python client: https://github.com/Oakraven79/pavlov_rcon_tcl/blob/main/pavlov_rcon_tcl/pavlovrcon.py
 * without the async
 */

@Service
public class RCONClientImpl implements RCONClient{

    private final static Logger logger = LoggerFactory.getLogger(RCONClientImpl.class);

    private final AppConfig appConfig;
    private long lastTimestamp;
    private String host;
    private int port;
    private String password;


    private Socket clientSocket;
    private PrintWriter writer;

    @Autowired
    private ObjectMapper objectMapper;

    public RCONClientImpl(AppConfig appConfig){
        this.appConfig = appConfig;
        this.host = appConfig.getRconHost();
        this.port = appConfig.getRconPort();
        this.password = this.getPasswordMD5();
    }

    @Override
    public void open() throws IOException {
        if(!this.isConnected()){
         this.connect();
        }
    }


    @Override
    public void close(){
        if(this.isConnected()){
            try {
                this.clientSocket.close();
                this.clientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isConnected() {
        if(this.clientSocket != null && this.clientSocket.isClosed() == false){
            return true;
        }
        return false;
    }


    @Override
    public <T extends Command> T send(Command command, Class<T> responseType) throws IOException {
        this.open();
        this.awaitNextCommand();
        this.writer.write(command.toCommand());
        this.writer.flush();
        String response = "";
        try{
            response = this.getResponse();
            logger.info("Reponse should be in {} format for {} command, yet it is this: {}",
                    responseType.getTypeName(), command.toCommand(), response);
            T responseObject = objectMapper.readValue(response, responseType);
            return responseObject;
        }
        catch(JsonProcessingException e){
            logger.error("Could not deserialize response, got this garbage instead: '" + response + "'");
            throw e;
        }
    }

    private String getResponse() throws IOException {
        long start = new Date().getTime();
        StringBuilder response = new StringBuilder();
        InputStream inputStream = this.clientSocket.getInputStream();
        int c;
        do{
            c = inputStream.read();
            if (c > -1){
                response.append((char) c);
            }
        }
        while (c > 0 && inputStream.available() > 0);
        String responseString = response.toString();
         logger.info("getResponse took " + (new Date().getTime() - start) + " ms");
        return responseString;
    }

    private void auth() throws IOException {
        this.writer.write(this.password);
        this.writer.flush();
        this.awaitNextCommand();
        String response = this.getResponse();
        if(response.contains("Authenticated=1") == false){
            throw new IOException("Could not authenticate - response: " + response);
        }
    }

    private void connect() throws IOException {
        this.clientSocket = new Socket(this.host, this.port);
        this.writer = new PrintWriter(this.clientSocket.getOutputStream(), true);
        String response = this.getResponse();

        if(response.contains("Password")){
            this.auth();
        }
        else{
            throw new IOException("Did not get expected greeting, got this instead: " + response);
        }
    }

    private String getPasswordMD5() {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(this.appConfig.getRconPassword().getBytes("UTF-8"));
            return HexUtils.toHexString(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Governor to allow configuration of pauses between commands if necessary
     */
    private void awaitNextCommand() {
        if(this.appConfig.getRconCommandSleeps() == 0){
            return;
        }
        long timespan = this.appConfig.getRconCommandSleeps();
        long currentTime = new Date().getTime();
        long tsDifference = currentTime - lastTimestamp;
        if(lastTimestamp == 0L){
            try {
                Thread.sleep(timespan);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        else if(timespan > tsDifference){
            try {
                Thread.sleep(timespan - tsDifference);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastTimestamp = new Date().getTime();

    }

}
