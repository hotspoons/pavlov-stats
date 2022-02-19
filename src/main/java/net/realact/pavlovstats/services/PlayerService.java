package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.models.dtos.rcon.PlayerInfoDto;

import java.io.IOException;
import java.util.List;

public interface PlayerService {
    List<Player> getPlayers() throws IOException;

    List<PlayerInfoDto> getCurrentPlayersFromServer() throws IOException;

    void updatePlayerStats(Scoreboard scoreboard);

    void updateGamesPlayed(Scoreboard scoreboard);
}
