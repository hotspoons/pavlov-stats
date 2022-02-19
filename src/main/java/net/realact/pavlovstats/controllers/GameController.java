package net.realact.pavlovstats.controllers;

import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.services.PlayerService;
import net.realact.pavlovstats.services.ScoreboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameController {

    private final PlayerService playerService;
    private final ScoreboardService scoreboardService;

    public GameController(PlayerService playerService, ScoreboardService scoreboardService){
        this.playerService = playerService;
        this.scoreboardService = scoreboardService;

    }

    @GetMapping("/scoreboard")
    public Scoreboard getScoreboard(){
        return scoreboardService.getCurrentScoreboard();
    }

    @GetMapping("/leaderboard")
    public List<Player> getPlayerStats(){
        return playerService.getAllPlayers();
    }
}
