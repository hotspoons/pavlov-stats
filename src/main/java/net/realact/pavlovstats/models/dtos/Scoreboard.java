package net.realact.pavlovstats.models.dtos;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Date;
import java.util.List;

@RedisHash("Scoreboard-${appconfig.redis-hash-suffix}")
public class Scoreboard {

    @Id
    @Indexed
    private String id;
    private String mapName;
    private String gameMode;
    private String playerCount;
    private int redTeamScore;
    private int blueTeamScore;
    private Date started;
    private Date concluded;
    private List<Player> redTeam;
    private List<Player> blueTeam;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(String playerCount) {
        this.playerCount = playerCount;
    }

    public int getRedTeamScore() {
        return redTeamScore;
    }

    public void setRedTeamScore(int redTeamScore) {
        this.redTeamScore = redTeamScore;
    }

    public int getBlueTeamScore() {
        return blueTeamScore;
    }

    public void setBlueTeamScore(int blueTeamScore) {
        this.blueTeamScore = blueTeamScore;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getConcluded() {
        return concluded;
    }

    public void setConcluded(Date concluded) {
        this.concluded = concluded;
    }

    public List<Player> getRedTeam() {
        return redTeam;
    }

    public void setRedTeam(List<Player> redTeam) {
        this.redTeam = redTeam;
    }

    public List<Player> getBlueTeam() {
        return blueTeam;
    }

    public void setBlueTeam(List<Player> blueTeam) {
        this.blueTeam = blueTeam;
    }
}
