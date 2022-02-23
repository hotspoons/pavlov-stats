package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.RequestResponse;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.models.dtos.rcon.PlayerInfoDto;

import java.io.IOException;
import java.util.List;

public interface PlayerService {
    List<Player> getPlayersFromRCON() throws IOException;

    List<PlayerInfoDto> getCurrentPlayersFromServer() throws IOException;

    List<Player> getAllPlayers();

    void updatePlayerStats(Scoreboard scoreboard, Scoreboard previousScoreboard);

    void updateGamesPlayed(Scoreboard scoreboard);

    Player getPlayerByUuid(String uuid);

    void sortPlayers(RequestResponse.Sort sort, boolean ascending, List<Player> filteredResults);

}
