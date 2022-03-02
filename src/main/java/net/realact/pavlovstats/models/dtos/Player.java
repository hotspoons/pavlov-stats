package net.realact.pavlovstats.models.dtos;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Date;
import java.util.List;

@RedisHash("Player-${appconfig.redis-hash-suffix}")
public class Player {

    @Id
    @Indexed
    private String uuid;
    @Indexed
    private String playerName;
    private int kills;
    private int deaths;
    private int assists;
    private float kdr = -1F;
    private int games;
    private Date lastPlayed;
    private List<String> previousNames;
    @Transient
    private boolean changed;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public float getKdr() {
        if(this.kdr == -1F){
            this.kdr = deaths != 0 ? (float) kills / (float) deaths :
                    deaths;
        }
        return kdr;
    }

    public void setKdr(float kdr) {
        this.kdr = kdr;
    }

    public List<String> getPreviousNames() {
        return previousNames;
    }

    public void setPreviousNames(List<String> previousNames) {
        this.previousNames = previousNames;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
