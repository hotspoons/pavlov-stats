package net.realact.pavlovstats.controllers;

import net.realact.pavlovstats.config.AppConfig;
import net.realact.pavlovstats.models.commands.InspectPlayerCommand;
import net.realact.pavlovstats.models.commands.RefreshListCommand;
import net.realact.pavlovstats.models.commands.ServerInfoCommand;
import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.models.dtos.rcon.PlayerDto;
import net.realact.pavlovstats.models.dtos.rcon.PlayerInfoDto;
import net.realact.pavlovstats.models.dtos.rcon.ServerInfoDto;
import net.realact.pavlovstats.services.PlayerService;
import net.realact.pavlovstats.services.RCONClient;
import net.realact.pavlovstats.services.RconDataConverter;
import net.realact.pavlovstats.services.ScoreboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class CurrentGameController {

    private final PlayerService playerService;
    private final ScoreboardService scoreboardService;

    public CurrentGameController(PlayerService playerService, ScoreboardService scoreboardService){
        this.playerService = playerService;
        this.scoreboardService = scoreboardService;

    }

    @GetMapping("/current-players")
    public List<Player> getPlayers() throws IOException{
        return playerService.getPlayers();
    }

    @GetMapping("/scoreboard")
    public Scoreboard getScoreboard() throws IOException {
        return scoreboardService.getScoreboard();
    }

}
