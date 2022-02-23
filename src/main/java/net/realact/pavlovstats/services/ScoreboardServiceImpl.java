package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.commands.ServerInfoCommand;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.models.dtos.rcon.PlayerInfoDto;
import net.realact.pavlovstats.models.dtos.rcon.ServerInfoDto;
import net.realact.pavlovstats.repositories.ScoreboardRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ScoreboardServiceImpl implements ScoreboardService {

    private final ScoreboardRepository scoreboardRepository;
    private final RconDataConverter rconDataConverter;
    private final PlayerService playerService;
    private final RCONClient rconClient;
    private Scoreboard currentScoreboard;

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
        return currentScoreboard;
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
}
