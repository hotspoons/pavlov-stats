package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.commands.InspectPlayerCommand;
import net.realact.pavlovstats.models.commands.RefreshListCommand;
import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.models.dtos.rcon.PlayerDto;
import net.realact.pavlovstats.models.dtos.rcon.PlayerInfoDto;
import net.realact.pavlovstats.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService{

    private final PlayerRepository playerRepository;
    private final RconDataConverter rconDataConverter;
    private final RCONClient rconClient;

    public PlayerServiceImpl(PlayerRepository playerRepository, RCONClient rconClient, RconDataConverter rconDataConverter){
        this.playerRepository = playerRepository;
        this.rconClient = rconClient;
        this.rconDataConverter = rconDataConverter;
    }

    @Override
    public List<Player> getPlayersFromRCON() throws IOException{
        return rconDataConverter.convertPlayers(this.getCurrentPlayersFromServer());
    }

    @Override
    public List<PlayerInfoDto> getCurrentPlayersFromServer() throws IOException {
        RefreshListCommand rlc = rconClient.send(new RefreshListCommand(), RefreshListCommand.class);
        List<PlayerInfoDto> playerInfoList = new ArrayList<>();
        List<PlayerDto> players = rlc.getPlayerList();
        if(players != null){
            for(PlayerDto player: players){
                InspectPlayerCommand ipc = rconClient.send(new InspectPlayerCommand(player.getUniqueId()),
                        InspectPlayerCommand.class);
                playerInfoList.add(ipc.getPlayerInfo());
            }
        }
        return playerInfoList;
    }

    @Override
    public List<Player> getAllPlayers() {
        List<Player> rankedPlayers = new ArrayList<>();
        for(Player player: playerRepository.findAll()){
            rankedPlayers.add(player);
        }
        Collections.sort(rankedPlayers, rconDataConverter.getKdaComparator());
        return rankedPlayers;    }

    @Override
    public void updatePlayerStats(Scoreboard scoreboard, Scoreboard previousScoreboard) {

        for(Player player: scoreboard.getBlueTeam()){
            for(Player previousPlayer: previousScoreboard.getBlueTeam()){
                if(player.getUuid().equalsIgnoreCase(previousPlayer.getUuid())){
                    savePlayer(player, previousPlayer, false);
                }
            }

            playerRepository.save(player);
        }
        for(Player player: scoreboard.getRedTeam()){
            playerRepository.save(player);
        }
    }

    private void savePlayer(Player player, Player previousPlayer, boolean force){
        int killsDelta = 0;
        int deathsDelta = 0;
        int assistsDelta = 0;
        player.setLastPlayed(new Date());
        if(player.getDeaths() != previousPlayer.getDeaths()){
            killsDelta = player.getKills() - previousPlayer.getKills();
            deathsDelta = player.getDeaths() - previousPlayer.getDeaths();
            assistsDelta = player.getAssists() - previousPlayer.getAssists();
            if((killsDelta + deathsDelta + assistsDelta) != 0){
                force = true;
            }
        }
        Player loadedPlayer = getPlayerByUuid(player.getUuid());
        if(loadedPlayer != null){
            player.setKills(loadedPlayer.getKills() + killsDelta);
            player.setDeaths(loadedPlayer.getDeaths() + deathsDelta);
            player.setAssists(loadedPlayer.getAssists() + assistsDelta);
            if((killsDelta + deathsDelta + assistsDelta) != 0 || force == true){
                playerRepository.save(player);
            }
        }
        // New Player!
        else{
            playerRepository.save(player);
        }
    }

    @Override
    public Player getPlayerByUuid(String uuid){
        return playerRepository.findById(uuid).get();
    }

    @Override
    public void updateGamesPlayed(Scoreboard scoreboard) {
        for(Player player: scoreboard.getBlueTeam()){
            player.setGames(player.getGames() + 1);
            playerRepository.save(player);
        }
        for(Player player: scoreboard.getRedTeam()){
            player.setGames(player.getGames() + 1);
            playerRepository.save(player);
        }
    }
}
