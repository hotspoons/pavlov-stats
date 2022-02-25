package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.models.dtos.rcon.PlayerInfoDto;
import net.realact.pavlovstats.models.dtos.rcon.ServerInfoDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RconDataConverterImpl implements RconDataConverter {
    @Override
    public List<Player> convertPlayers(List<PlayerInfoDto> rconPlayers) {
        List<Player> players = new ArrayList<>();
        for(PlayerInfoDto playerDto: rconPlayers){
            players.add(convertPlayer(playerDto));
        }
        return players;
    }

    @Override
    public Player convertPlayer(PlayerInfoDto rconPlayer) {
        Player player = new Player();
        String[] kdaSplit = rconPlayer.getKDA().split("\\/");
        if(kdaSplit.length == 3){
            player.setKills(Integer.parseInt(kdaSplit[0]));
            player.setDeaths(Integer.parseInt(kdaSplit[1]));
            player.setAssists(Integer.parseInt(kdaSplit[2]));
        }
        player.setUuid(rconPlayer.getUniqueId());
        player.setPlayerName(rconPlayer.getPlayerName());
        player.setLastPlayed(new Date());
        return player;
    }

    @Override
    public Scoreboard convertScoreboard(List<PlayerInfoDto> rconPlayers, ServerInfoDto serverInfoDto) {
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.setGameMode(serverInfoDto.getGameMode());
        scoreboard.setPlayerCount(serverInfoDto.getPlayerCount());
        scoreboard.setMapName(serverInfoDto.getMapLabel());
        scoreboard.setRedTeamScore(Integer.parseInt(serverInfoDto.getTeam0Score()));
        scoreboard.setBlueTeamScore(Integer.parseInt(serverInfoDto.getTeam1Score()));
        List<PlayerInfoDto> redTeam = new ArrayList<>();
        List<PlayerInfoDto> blueTeam = new ArrayList<>();
        for(PlayerInfoDto playerInfoDto: rconPlayers){
            if(playerInfoDto.getTeamId().equalsIgnoreCase("0")) {
                redTeam.add(playerInfoDto);
            }
            else{
                blueTeam.add(playerInfoDto);
            }
        }
        scoreboard.setRedTeam(convertPlayers(redTeam));
        scoreboard.setBlueTeam(convertPlayers(blueTeam));
        return scoreboard;
    }


}
