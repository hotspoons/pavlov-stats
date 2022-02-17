package net.realact.pavlovstats.models.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.realact.pavlovstats.models.dtos.ServerInfoDto;
import org.springframework.stereotype.Service;

@Service
public class ServerInfoCommand extends Command {
    private ServerInfoDto serverInfo;
    public ServerInfoCommand(){
        super.setCommand("ServerInfo");
    }

    @JsonProperty("ServerInfo")
    public ServerInfoDto getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfoDto serverInfo) {
        this.serverInfo = serverInfo;
    }
}
