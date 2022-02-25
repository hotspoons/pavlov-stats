package net.realact.pavlovstats.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.RequestResponse;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.services.PlayerService;
import net.realact.pavlovstats.services.RconDataConverter;
import net.realact.pavlovstats.services.ScoreboardService;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("api")
public class GameController {
    private final static SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);


    private final PlayerService playerService;
    private final ScoreboardService scoreboardService;
    private final RconDataConverter rconDataConverter;
    private final ObjectMapper objectMapper;

    public GameController(PlayerService playerService,
                          ScoreboardService scoreboardService,
                          RconDataConverter rconDataConverter,
                          ObjectMapper objectMapper){
        this.playerService = playerService;
        this.scoreboardService = scoreboardService;
        this.rconDataConverter = rconDataConverter;
        this.objectMapper = objectMapper;
    }

    // TODO scoreboard history route with date filters, pagination, and maybe username search

    @GetMapping("scoreboard")
    @CrossOrigin
    public RequestResponse<Scoreboard> getScoreboard(@RequestParam(required = false) String q,
                                    @RequestParam(required = false) int offset,
                                    @RequestParam(required = false) int amount,
                                    @RequestParam(required = false) RequestResponse.Sort sort,
                                    @RequestParam(required = false) boolean ascending){
        RequestResponse<Scoreboard> response = getResponse(q, offset, amount, sort, ascending);
        List<Scoreboard> allResults = new ArrayList<>();
        if((q != null && !q.trim().isEmpty())){
            try{
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Map<String, Object> argMap = this.objectMapper.readValue(q, Map.class);
                String playerName = argMap.get("playerName") != null ? (String) argMap.get("playerName"): null;
                Date start = argMap.get("start") != null ? format.parse((String) argMap.get("start")) : null;
                Date end = argMap.get("end") != null ? format.parse((String) argMap.get("end")) : null;
                allResults.addAll(
                        scoreboardService.searchByNameAndDate(playerName, start, end));
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            Scoreboard currentScoreboard = scoreboardService.getCurrentScoreboard();
            if(currentScoreboard != null){
                allResults.add(currentScoreboard);
            }
        }
        response.setResultCount(allResults.size());
        paginate(offset, amount, response, allResults);
        return response;
    }

    @CrossOrigin
    @GetMapping("leaderboard")
    public RequestResponse<Player> getPlayerStats(@RequestParam(required = false) String q,
                                                  @RequestParam int offset,
                                                  @RequestParam int amount,
                                                  @RequestParam(required = false) RequestResponse.Sort sort,
                                                  @RequestParam(required = false) boolean ascending){
        RequestResponse<Player> response = getResponse(q, offset, amount, sort, ascending);
        List<Player> allPlayers = playerService.getAllPlayers();
        List<Player> filteredResults = new ArrayList<>();
        response.setResults(new ArrayList<>());
        if(q != null && !q.isEmpty()){
            for(Player player: allPlayers){
                if(player.getPlayerName().toLowerCase().contains(q.toLowerCase())){
                    filteredResults.add(player);
                }
            }
        }
        else{
            filteredResults = allPlayers;
        }
        playerService.sortPlayers(sort, ascending, filteredResults);
        paginate(offset, amount, response, filteredResults);
        return response;
    }

    private <T> RequestResponse<T> getResponse(String q, int offset, int amount, RequestResponse.Sort sort, boolean ascending) {
        RequestResponse<T> response = new RequestResponse<>();
        response.setOffset(offset);
        response.setAmount(amount);
        response.setSort(sort);
        response.setAscending(ascending);
        response.setQ(q);
        return response;
    }

    private <T> void paginate(int offset, int amount, RequestResponse<T> response, List<T> filteredResults) {
        response.setResults(new ArrayList<>());
        if(offset < filteredResults.size()){
            int max = offset + amount > filteredResults.size() ? filteredResults.size() : offset + amount;
            for(int i = offset; i < max; i++){
                response.getResults().add(filteredResults.get(i));
            }
            response.setResultCount(filteredResults.size());
        }
        if(response.getResults() == null){
            response.setResults(new ArrayList<>());
        }
    }


}
