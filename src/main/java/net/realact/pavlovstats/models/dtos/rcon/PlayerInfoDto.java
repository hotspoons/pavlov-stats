package net.realact.pavlovstats.models.dtos.rcon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerInfoDto {
    private String playerName;
    private String uniqueId;
    private String KDA;
    private String score;
    private String dead;
    private String cash;
    private String teamId;

    @JsonProperty("PlayerName")
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @JsonProperty("UniqueId")
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @JsonProperty("KDA")
    public String getKDA() {
        return KDA;
    }

    public void setKDA(String KDA) {
        this.KDA = KDA;
    }

    @JsonProperty("Score")
    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
    @JsonProperty("Dead")
    public String getDead() {
        return dead;
    }

    public void setDead(String dead) {
        this.dead = dead;
    }
    @JsonProperty("Cash")
    public String getCash() {
        return cash;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    @JsonProperty("TeamId")
    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
