package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.commands.InspectPlayerCommand;
import net.realact.pavlovstats.models.commands.RefreshListCommand;
import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.RequestResponse;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.models.dtos.rcon.PlayerDto;
import net.realact.pavlovstats.models.dtos.rcon.PlayerInfoDto;
import net.realact.pavlovstats.repositories.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class PlayerServiceImpl implements PlayerService{

    private final PlayerRepository playerRepository;
    private final RconDataConverter rconDataConverter;
    private final RCONClient rconClient;

    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

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
                try {
                    InspectPlayerCommand ipc = rconClient.send(new InspectPlayerCommand(player.getUniqueId()),
                            InspectPlayerCommand.class);
                    playerInfoList.add(ipc.getPlayerInfo());
                }
                // If we have a transient failure, the worst thing that happens in most cases
                // is the player's stats are not updated when the failure occurred, the change
                // will be picked up in a subsequent pass. Close the connection, which will
                // be reopened on the next request
                catch(IOException e){
                    logger.error(e.getMessage());
                    try {
                        rconClient.close();
                    } catch (IOException ex) {
                        logger.error(ex.getMessage());
                    }
                }
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
        Collections.sort(rankedPlayers, this.getKdaComparator());
        return rankedPlayers;
    }

    @Override
    public void updatePlayerStats(Scoreboard scoreboard, Scoreboard previousScoreboard) {
        // Go through both lists in case someone swapped teams since the last sample
        for(Player player: scoreboard.getBlueTeam()){
            findAndSavePlayer(previousScoreboard.getRedTeam(), player);
            findAndSavePlayer(previousScoreboard.getBlueTeam(), player);
        }
        for(Player player: scoreboard.getRedTeam()){
            findAndSavePlayer(previousScoreboard.getRedTeam(), player);
            findAndSavePlayer(previousScoreboard.getBlueTeam(), player);
        }
    }

    private void findAndSavePlayer(List<Player> playerList, Player player){
        for(Player previousPlayer: playerList){
            if(player.getUuid().equalsIgnoreCase(previousPlayer.getUuid())){
                savePlayer(player, previousPlayer, false);
                break;
            }
        }
    }

    private void savePlayer(Player player, Player previousPlayer, boolean force){
        int killsDelta = 0;
        int deathsDelta = 0;
        int assistsDelta = 0;
        player.setLastPlayed(new Date());

        killsDelta = player.getKills() - previousPlayer.getKills();
        deathsDelta = player.getDeaths() - previousPlayer.getDeaths();
        assistsDelta = player.getAssists() - previousPlayer.getAssists();
        Player loadedPlayer = getPlayerByUuid(player.getUuid());
        if(loadedPlayer != null){
            loadedPlayer.setKills(loadedPlayer.getKills() + killsDelta);
            loadedPlayer.setDeaths(loadedPlayer.getDeaths() + deathsDelta);
            loadedPlayer.setAssists(loadedPlayer.getAssists() + assistsDelta);
            // Track old names
            if(!player.getPlayerName().equalsIgnoreCase(loadedPlayer.getPlayerName())){
                if(loadedPlayer.getPreviousNames() == null){
                    loadedPlayer.setPreviousNames(new ArrayList<>());
                }
                loadedPlayer.getPreviousNames().add(loadedPlayer.getPlayerName());
                loadedPlayer.setPlayerName(player.getPlayerName());
                force = true;
            }
            if(killsDelta < 0 || deathsDelta < 0 || assistsDelta < 0){
                // If we are negative, it means our delta is against an old round, so skip
                return;
            }
            if((killsDelta + deathsDelta + assistsDelta) != 0 || force == true){
                playerRepository.save(loadedPlayer);
            }
        }
        // New Player!
        else{
            playerRepository.save(player);
        }
    }

    @Override
    public Player getPlayerByUuid(String uuid){
        return playerRepository.findById(uuid).isEmpty() ? null : playerRepository.findById(uuid).get();
    }

    @Override
    public void updateGamesPlayed(Scoreboard scoreboard) {
        List<Player> allPlayers = getAllPlayers();
        for(Player player: scoreboard.getBlueTeam()){
            incrementGamesAndSave(player);

        }
        for(Player player: scoreboard.getRedTeam()){
            incrementGamesAndSave(player);
        }
    }

    @Override
    public void sortPlayers(RequestResponse.Sort sort, boolean ascending, List<Player> filteredResults) {
        if(sort != null){
            switch (sort){
                case ADK: filteredResults.sort(this.getAdkComparator());
                    break;
                case DKA: filteredResults.sort(this.getDkaComparator());
                    break;
                case KDA: filteredResults.sort(this.getKdaComparator());
                    break;
                case KDR: filteredResults.sort(this.getKDRComparator());
                    break;
                case NAME: filteredResults.sort(this.getNameComparator());
                    break;
                case LAST_PLAYED: filteredResults.sort(this.getLastPlayedComparator());
                    break;
                case GAMES: filteredResults.sort(this.getGamesPlayedComparator());
                    break;
            }
            if(ascending){
                Collections.reverse(filteredResults);
            }
        }
    }



    private Comparator<Player> getKdaComparator() {
        Comparator<Player> comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                int kills = o1.getKills() - o2.getKills();
                if(kills == 0){
                    // If kills are equal, sort by who has the least deaths
                    int deaths = o2.getDeaths() - o1.getDeaths();
                    if(deaths == 0){
                        return o1.getAssists() - o2.getAssists();
                    }
                    return deaths;
                }
                return kills;
            }
        };
        return comparator;
    }

    private Comparator<Player> getDkaComparator() {
        Comparator<Player> comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                int deaths = o1.getDeaths() - o2.getDeaths();
                if(deaths == 0){
                    // If deaths are equal, sort by who has the least kills since we want to see who sucks
                    int kills = o2.getKills() - o1.getKills();
                    if(kills == 0){
                        return o2.getAssists() - o1.getAssists();
                    }
                    return deaths;
                }
                return deaths;
            }
        };
        return comparator;
    }

    private Comparator<Player> getAdkComparator() {
        Comparator<Player> comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                int assists = o1.getAssists() - o2.getAssists();
                if(assists == 0){
                    // If assists are equal, sort by who has the most kills
                    int kills = o1.getKills() - o2.getKills();
                    if(kills == 0){
                        return o2.getDeaths() - o2.getDeaths();
                    }
                    return kills;
                }
                return assists;
            }
        };
        return comparator;
    }

    private Comparator<Player> getKDRComparator() {
        Comparator<Player> comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                if(o1.getKdr() - o2.getKdr() == 0){
                    return 0;
                }
                int kdrInt = (int)(Math.ceil(o1.getKdr() - o2.getKdr()));
                if(kdrInt <= 0){
                    kdrInt -= 1;
                }
                return kdrInt;
            }
        };
        return comparator;
    }

    private Comparator<Player> getNameComparator() {
        Comparator<Player> comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                String p1 = o1.getPlayerName() != null ?
                        o1.getPlayerName().toLowerCase(Locale.ROOT).trim() : "";
                String p2 = o2.getPlayerName() != null ?
                        o2.getPlayerName().toLowerCase(Locale.ROOT).trim() : "";
                return p1.compareTo(p2);
            }
        };
        return comparator;
    }

    private Comparator<Player> getGamesPlayedComparator() {
        Comparator<Player> comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o1.getGames() - o2.getGames();
            }
        };
        return comparator;
    }
    private Comparator<Player> getLastPlayedComparator() {
        Comparator<Player> comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                Date d1 = o1.getLastPlayed() != null ? o1.getLastPlayed() : new Date(0L);
                Date d2 = o2.getLastPlayed() != null ? o2.getLastPlayed() : new Date(0L);
                return d1.compareTo(d2);
            }
        };
        return comparator;
    }


    private void incrementGamesAndSave(Player player) {
        Player loadedPlayer = getPlayerByUuid(player.getUuid());
        if(loadedPlayer != null){
            loadedPlayer.setGames(loadedPlayer.getGames() + 1);
            loadedPlayer.setLastPlayed(new Date());
            playerRepository.save(loadedPlayer);
        }
        // Corner case, brand new player!
        else{
            player.setGames(1);
            playerRepository.save(player);
        }
    }
}
