package net.realact.pavlovstats.models.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.realact.pavlovstats.models.dtos.rcon.PlayerDto;

import java.util.List;

public class RefreshListCommand extends Command {
    private List<PlayerDto> playerList;

    public RefreshListCommand(){
        super.setCommand("RefreshList");
    }

    @JsonProperty("PlayerList")
    public List<PlayerDto> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<PlayerDto> playerList) {
        this.playerList = playerList;
    }
}
