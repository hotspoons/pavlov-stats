package net.realact.pavlovstats.models.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.realact.pavlovstats.models.dtos.PlayerInfoDto;

import java.util.Arrays;

public class InspectPlayerCommand extends Command {
    private PlayerInfoDto playerInfo;

    public InspectPlayerCommand(){
        super.setCommand("InspectPlayer");
    }
    public InspectPlayerCommand(String uniqueId){
        super.setCommand("InspectPlayer");
        super.args = Arrays.asList(uniqueId);
    }

    @JsonProperty("PlayerInfo")
    public PlayerInfoDto getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfoDto playerInfo) {
        this.playerInfo = playerInfo;
    }
}
