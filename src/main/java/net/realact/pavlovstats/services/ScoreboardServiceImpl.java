package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.commands.ServerInfoCommand;
import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.models.dtos.rcon.PlayerInfoDto;
import net.realact.pavlovstats.models.dtos.rcon.ServerInfoDto;
import net.realact.pavlovstats.repositories.ScoreboardRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ScoreboardServiceImpl implements ScoreboardService {

    private final ScoreboardRepository scoreboardRepository;
    private final RconDataConverter rconDataConverter;
    private final PlayerService playerService;
    private final RCONClient rconClient;
    private Scoreboard currentScoreboard;
    private Scoreboard lastScoreboard;

    public ScoreboardServiceImpl(ScoreboardRepository scoreboardRepository, RCONClient rconClient,
                                 RconDataConverter rconDataConverter, PlayerService playerService){
        this.scoreboardRepository = scoreboardRepository;
        this.rconClient = rconClient;
        this.rconDataConverter = rconDataConverter;
        this.playerService = playerService;
    }

    @Override
    public Scoreboard getScoreboardFromRCON() throws IOException {
        List<PlayerInfoDto> players = playerService.getCurrentPlayersFromServer();
        ServerInfoCommand serverInfoCommand = rconClient.send(new ServerInfoCommand(), ServerInfoCommand.class);
        ServerInfoDto serverInfo = serverInfoCommand.getServerInfo();
        return rconDataConverter.convertScoreboard(players, serverInfo);
    }

    @Override
    public Scoreboard getCurrentScoreboard() {
        return this.markChanges();
    }

    @Override
    public void setCurrentScoreboard(Scoreboard scoreboard) {
        this.currentScoreboard = scoreboard;
    }

    @Override
    public void saveScoreboard(Scoreboard scoreboard) {
        scoreboard.setId(UUID.randomUUID().toString());
        scoreboard.setConcluded(new Date());
        scoreboardRepository.save(scoreboard);
        playerService.updateGamesPlayed(scoreboard);
    }

    @Override
    public Scoreboard getById(String id){
        return scoreboardRepository.findById(id).get();
    }

    @Override
    public List<Scoreboard> getAll(){
        Iterable<Scoreboard> all = scoreboardRepository.findAll();
        List<Scoreboard> scoreboards = new ArrayList<>();
        for(Scoreboard scoreboard: all){
            scoreboards.add(scoreboard);
        }
        return scoreboards;
    }

    @Override
    public List<Scoreboard> searchByNameAndDate(String name, Date start, Date end) {
        Iterable<Scoreboard> all = scoreboardRepository.findAll();
        List<Scoreboard> scoreboards = new ArrayList<>();
        for(Scoreboard scoreboard: all){
            Boolean nameHit = null;
            Boolean dateHit = null;
            if(name != null && name.trim().isEmpty() == false){
                nameHit = false;
                if(scoreboard.getRedTeam() != null){
                    for(Player player: scoreboard.getRedTeam()){
                        if(player.getPlayerName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))){
                            nameHit = true;
                            break;
                        }
                    }
                }
                if(scoreboard.getBlueTeam() != null){
                    for(Player player: scoreboard.getBlueTeam()){
                        if(player.getPlayerName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))){
                            nameHit = true;
                            break;
                        }
                    }
                }
            }
            if(start != null && end != null){
                dateHit = false;
                if(scoreboard.getStarted().before(end) && scoreboard.getConcluded().after(start)){
                    dateHit = true;
                }
            }
            // If neither name or date hit was initialized, then add all
            if(nameHit == null && dateHit == null){
                scoreboards.add(scoreboard);
            }
            // If one or the other was true and the other was null, or if both were true,
            // add the value
            else if((nameHit == null && dateHit == true) || (nameHit == true && dateHit == null)
                    || (nameHit == true && dateHit == true)){
                scoreboards.add(scoreboard);
            }

        }
        return scoreboards;
    }


    private Scoreboard markChanges() {
        if(currentScoreboard != null && lastScoreboard != null) {
            List<Player> allCurrentPlayers = new ArrayList<>();
            List<Player> allLastPlayers = new ArrayList<>();
            if(currentScoreboard.getBlueTeam() != null){
               allCurrentPlayers.addAll(currentScoreboard.getBlueTeam());
            }
            if(lastScoreboard.getRedTeam() != null){
                allCurrentPlayers.addAll(currentScoreboard.getRedTeam());
            }
            if(lastScoreboard.getBlueTeam() != null){
                allLastPlayers.addAll(lastScoreboard.getBlueTeam());
            }
            if(lastScoreboard.getRedTeam() != null){
                allLastPlayers.addAll(lastScoreboard.getRedTeam());
            }
            // Highlight new players since the last refresh
            for(Player newPlayer: allCurrentPlayers){
                boolean isNew = true;
                boolean hasDelta = false;

                for(Player lastPlayer: allLastPlayers){
                    if(lastPlayer.getUuid().equalsIgnoreCase(newPlayer.getUuid())){
                        isNew = false;
                        hasDelta = (lastPlayer.getKills() != newPlayer.getKills()) ||
                                (lastPlayer.getDeaths() != newPlayer.getDeaths())  ||
                                (lastPlayer.getAssists() != newPlayer.getAssists());
                        break;
                    }
                }
                if(isNew == true || hasDelta == true){
                    newPlayer.setChanged(true);
                }
            }
        }
        lastScoreboard = currentScoreboard;
        return currentScoreboard;
    }

}
