package net.realact.pavlovstats.services;

import net.realact.pavlovstats.lib.ex.AuthenticationException;
import net.realact.pavlovstats.models.commands.Command;

import java.io.IOException;

public interface RCONClient {
    void connect() throws IOException, AuthenticationException;

    <T extends Command> T executeCommand(Command command, Class<T> responseType) throws IOException;

    void disconnect() throws IOException;
}
