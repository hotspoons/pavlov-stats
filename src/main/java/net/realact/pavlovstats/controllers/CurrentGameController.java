package net.realact.pavlovstats.controllers;

import net.realact.pavlovstats.config.AppConfig;
import net.realact.pavlovstats.models.commands.InspectPlayerCommand;
import net.realact.pavlovstats.models.commands.RefreshListCommand;
import net.realact.pavlovstats.models.dtos.PlayerDto;
import net.realact.pavlovstats.models.dtos.PlayerInfoDto;
import net.realact.pavlovstats.services.RCONClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CurrentGameController {

    private final RCONClient rconClient;
    private final AppConfig appConfig;

    public CurrentGameController(AppConfig appConfig, RCONClient rconClient){
        this.appConfig = appConfig;
        this.rconClient = rconClient;
    }

    @GetMapping("/current-users")
    public Object getCurrentUsers() throws IOException {
        RefreshListCommand rlc = rconClient.send(new RefreshListCommand(), RefreshListCommand.class);
        List<PlayerInfoDto> playerInfoList = new ArrayList<>();
        List<PlayerDto> players = rlc.getPlayerList();
        for(PlayerDto player: players){
            InspectPlayerCommand ipc = rconClient.send(new InspectPlayerCommand(player.getUniqueId()),
                    InspectPlayerCommand.class);
            playerInfoList.add(ipc.getPlayerInfo());
        }
        return playerInfoList;
    }
}
