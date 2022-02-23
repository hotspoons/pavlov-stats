package net.realact.pavlovstats.controllers;

import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.RequestResponse;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.services.PlayerService;
import net.realact.pavlovstats.services.ScoreboardService;

import java.util.ArrayList;
import java.util.List;

//@RestController
public class GameController {

    private final PlayerService playerService;
    private final ScoreboardService scoreboardService;

    public GameController(PlayerService playerService, ScoreboardService scoreboardService){
        this.playerService = playerService;
        this.scoreboardService = scoreboardService;

    }

    //@GetMapping("/scoreboard")
    //@CrossOrigin
    public Scoreboard getScoreboard(){
        return scoreboardService.getCurrentScoreboard();
    }

    //@CrossOrigin
    //@GetMapping("/leaderboard")
    public RequestResponse<Player> getPlayerStats(/*@RequestParam(required = false)*/ String steamId,
                                                  /*@RequestParam*/ int offset,
                                                  /*@RequestParam*/ int amount){
        RequestResponse<Player> response = new RequestResponse<>();
        response.setOffset(offset);
        response.setAmount(amount);
        List<Player> allPlayers = playerService.getAllPlayers();
        List<Player> filteredResults = new ArrayList<>();
        response.setResults(new ArrayList<>());
        if(steamId != null && !steamId.isEmpty()){
            for(Player player: allPlayers){
                if(player.getPlayerName().toLowerCase().contains(steamId.toLowerCase())){
                    response.getResults().add(player);
                }
            }
        }
        else{
            filteredResults = allPlayers;
        }
        if(offset < filteredResults.size()){
            int max = offset + amount > filteredResults.size() ? filteredResults.size() : offset + amount;
            for(int i = offset; i < max; i++){
                response.getResults().add(filteredResults.get(i));
            }
            response.setResultCount(filteredResults.size());
        }
        return response;
    }
}
