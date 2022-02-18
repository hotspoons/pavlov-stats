package net.realact.pavlovstats.models.dtos;

public class KDA {
    private int kills;
    private int deaths;
    private int assists;
    private PlayerInfoDto playerInfoDto;

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

    public PlayerInfoDto getPlayerInfoDto() {
        return playerInfoDto;
    }

    public void setPlayerInfoDto(PlayerInfoDto playerInfoDto) {
        this.playerInfoDto = playerInfoDto;
    }
}
