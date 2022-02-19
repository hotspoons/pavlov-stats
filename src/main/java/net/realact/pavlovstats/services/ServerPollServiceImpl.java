package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.dtos.Scoreboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ServerPollServiceImpl implements ServerPollService{
    private final RCONClient rconClient;
    private final RconDataConverter rconDataConverter;
    private final ScoreboardService scoreboardService;
    private final PlayerService playerService;

    private final static Logger logger = LoggerFactory.getLogger(ServerPollServiceImpl.class);

    private Scoreboard currentScoreboard;

    public ServerPollServiceImpl(RCONClient rconClient, RconDataConverter rconDataConverter,
                                 ScoreboardService scoreboardService, PlayerService playerService){

        this.rconClient = rconClient;
        this.rconDataConverter = rconDataConverter;
        this.scoreboardService = scoreboardService;
        this.playerService = playerService;
    }

    @Override
    @Scheduled(fixedDelayString = "${appconfig.server-polling-interval}")
    public void poll(){
        try {
            Scoreboard scoreboard = scoreboardService.getScoreboard();
            if(!this.isSameScoreboard(scoreboard)){
                scoreboardService.saveScoreboard(scoreboard);
            }
            playerService.updatePlayerStats(scoreboard);
            this.currentScoreboard = scoreboard;
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void persistStats(Scoreboard scoreboard) {
        scoreboardService.saveScoreboard(scoreboard);
    }

    private boolean isSameScoreboard(Scoreboard scoreboard) {
        if(this.currentScoreboard == null){
            this.currentScoreboard = scoreboard;
            return true;
        }
        int redScoreLast = currentScoreboard.getRedTeamScore();
        int blueScoreLast = currentScoreboard.getBlueTeamScore();
        int redScore = scoreboard.getRedTeamScore();
        int blueScore = scoreboard.getBlueTeamScore();
        if(currentScoreboard.getMapName().equalsIgnoreCase(scoreboard.getMapName()) &&
        currentScoreboard.getGameMode().equalsIgnoreCase(scoreboard.getGameMode()) &&
        redScore >= redScoreLast &&
        blueScore >= blueScoreLast){
            return true;
        }
        return false;
    }
}
