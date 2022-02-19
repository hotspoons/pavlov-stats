package net.realact.pavlovstats.models.dtos.rcon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerInfoDto {
    private String mapLabel;
    private String gameMode;
    private String serverName;
    private String teams;
    private String team0Score;
    private String team1Score;
    private String roundState;
    private String playerCount;

    @JsonProperty("MapLabel")
    public String getMapLabel() {
        return mapLabel;
    }

    public void setMapLabel(String mapLabel) {
        this.mapLabel = mapLabel;
    }

    @JsonProperty("GameMode")
    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    @JsonProperty("ServerName")
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @JsonProperty("Teams")
    public String getTeams() {
        return teams;
    }

    public void setTeams(String teams) {
        this.teams = teams;
    }

    @JsonProperty("Team0Score")
    public String getTeam0Score() {
        return team0Score;
    }

    public void setTeam0Score(String team0Score) {
        this.team0Score = team0Score;
    }

    @JsonProperty("Team1Score")
    public String getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(String team1Score) {
        this.team1Score = team1Score;
    }

    @JsonProperty("RoundState")
    public String getRoundState() {
        return roundState;
    }

    public void setRoundState(String roundState) {
        this.roundState = roundState;
    }

    @JsonProperty("PlayerCount")
    public String getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(String playerCount) {
        this.playerCount = playerCount;
    }
}
