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
    public List<Player> getPlayers() throws IOException{
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
    public void updatePlayerStats(Scoreboard scoreboard) {
        for(Player player: scoreboard.getBlueTeam()){
            playerRepository.save(player);
        }
        for(Player player: scoreboard.getRedTeam()){
            playerRepository.save(player);
        }
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

    public List<Player> getRankedList(){
        List<Player> rankedPlayers = new ArrayList<>();
        for(Player player: playerRepository.findAll()){
            rankedPlayers.add(player);
        }
        Collections.sort(rankedPlayers, rconDataConverter.getKdaComparator());
        return rankedPlayers;
    }
}
