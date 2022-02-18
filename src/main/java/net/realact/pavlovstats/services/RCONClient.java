package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.commands.Command;

import java.io.IOException;

public interface RCONClient {
    void open() throws IOException;

    <T extends Command> T send(Command command, Class<T> responseType) throws IOException;

    void close() throws IOException;
}
