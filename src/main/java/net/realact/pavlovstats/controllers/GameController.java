package net.realact.pavlovstats.controllers;

import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.RequestResponse;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.services.PlayerService;
import net.realact.pavlovstats.services.RconDataConverter;
import net.realact.pavlovstats.services.ScoreboardService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {

    private final PlayerService playerService;
    private final ScoreboardService scoreboardService;
    private final RconDataConverter rconDataConverter;

    public GameController(PlayerService playerService,
                          ScoreboardService scoreboardService,
                          RconDataConverter rconDataConverter){
        this.playerService = playerService;
        this.scoreboardService = scoreboardService;
        this.rconDataConverter = rconDataConverter;

    }

    // TODO scoreboard history route with date filters, pagination, and maybe username search

    @GetMapping("/scoreboard")
    @CrossOrigin
    public Scoreboard getScoreboard(){
        return scoreboardService.getCurrentScoreboard();
    }

    @CrossOrigin
    @GetMapping("/leaderboard")
    public RequestResponse<Player> getPlayerStats(@RequestParam(required = false) String playerName,
                                                  @RequestParam int offset,
                                                  @RequestParam int amount,
                                                  @RequestParam(required = false) RequestResponse.Sort sort,
                                                  @RequestParam(required = false) boolean ascending){
        RequestResponse<Player> response = new RequestResponse<>();
        response.setOffset(offset);
        response.setAmount(amount);
        response.setSort(sort);
        response.setAscending(ascending);
        response.setQ(playerName);
        List<Player> allPlayers = playerService.getAllPlayers();
        List<Player> filteredResults = new ArrayList<>();
        response.setResults(new ArrayList<>());
        if(playerName != null && !playerName.isEmpty()){
            for(Player player: allPlayers){
                if(player.getPlayerName().toLowerCase().contains(playerName.toLowerCase())){
                    response.getResults().add(player);
                }
            }
        }
        else{
            filteredResults = allPlayers;
        }
        playerService.sortPlayers(sort, ascending, filteredResults);
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
