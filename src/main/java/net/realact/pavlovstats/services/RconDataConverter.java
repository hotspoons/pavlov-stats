package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.dtos.Player;
import net.realact.pavlovstats.models.dtos.Scoreboard;
import net.realact.pavlovstats.models.dtos.rcon.PlayerInfoDto;
import net.realact.pavlovstats.models.dtos.rcon.ServerInfoDto;

import java.util.Comparator;
import java.util.List;

public interface RconDataConverter {
    List<Player> convertPlayers(List<PlayerInfoDto> rconPlayers);
    Player convertPlayer(PlayerInfoDto rconPlayer);
    Scoreboard convertScoreboard(List<PlayerInfoDto> rconPlayers, ServerInfoDto serverInfoDto);

    Comparator<Player> getKdaComparator();
}
