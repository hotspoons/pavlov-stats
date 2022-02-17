package net.realact.pavlovstats.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.realact.pavlovstats.config.AppConfig;
import net.realact.pavlovstats.lib.Rcon;
import net.realact.pavlovstats.lib.ex.AuthenticationException;
import net.realact.pavlovstats.models.commands.Command;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class RCONClientImpl implements RCONClient{

    private final AppConfig appConfig;
    private Rcon rcon;
    private long lastTimestamp;

    @Autowired
    private ObjectMapper objectMapper;

    public RCONClientImpl(AppConfig appConfig){
        this.appConfig = appConfig;
    }

    @Override
    public void connect() throws IOException {
        if(this.rcon == null){
            try {
                this.rcon = new Rcon(appConfig.getRconHost(),
                        appConfig.getRconPort(),
                        new byte[0]);
                this.awaitNextCommand();
                this.rcon.command(new String(this.getPasswordBytes()));
            } catch (AuthenticationException e) {
                throw new IOException("Authentication failed");
            }
        }
    }

    private byte[] getPasswordBytes() throws IOException {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(this.appConfig.getRconPassword().getBytes("UTF-8"));
            String hex = HexUtils.toHexString(bytes);
            return hex.getBytes();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        throw new IOException();
    }

    @Override
    public <T extends Command> T executeCommand(Command command, Class<T> responseType) throws IOException {
        this.connect();
        this.awaitNextCommand();
        String response = this.rcon.command(command.toCommand());
        return objectMapper.readValue(response, responseType);
    }

    private void awaitNextCommand() {

        long timespan = this.appConfig.getRconCommandSleeps();
        long currentTime = new Date().getTime();
        long tsDifference = currentTime - lastTimestamp;
        if(tsDifference > timespan){
            try {
                Thread.sleep(tsDifference - timespan);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void disconnect() throws IOException {
        this.rcon.disconnect();
    }

}
