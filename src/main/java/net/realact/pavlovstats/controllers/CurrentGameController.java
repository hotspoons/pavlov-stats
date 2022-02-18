package net.realact.pavlovstats.controllers;

import net.realact.pavlovstats.config.AppConfig;
import net.realact.pavlovstats.models.commands.InspectPlayerCommand;
import net.realact.pavlovstats.models.commands.RefreshListCommand;
import net.realact.pavlovstats.models.commands.ServerInfoCommand;
import net.realact.pavlovstats.models.dtos.KDA;
import net.realact.pavlovstats.models.dtos.PlayerDto;
import net.realact.pavlovstats.models.dtos.PlayerInfoDto;
import net.realact.pavlovstats.models.dtos.ServerInfoDto;
import net.realact.pavlovstats.services.RCONClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class CurrentGameController {

    private final RCONClient rconClient;
    private final AppConfig appConfig;

    public CurrentGameController(AppConfig appConfig, RCONClient rconClient){
        this.appConfig = appConfig;
        this.rconClient = rconClient;
    }

    @GetMapping("/current-players")
    public List<PlayerInfoDto> getCurrentPlayers() throws IOException {
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

    @GetMapping("/scoreboard")
    public Object getScoreboard() throws IOException {
        List<PlayerInfoDto> players = this.getCurrentPlayers();

        ServerInfoDto serverInfo = rconClient.send(new ServerInfoCommand(), ServerInfoCommand.class).getServerInfo();
        if(serverInfo == null){
            throw new IOException("Could not fetch server info");
        }
        Map<String, Object> scoreboard = new LinkedHashMap<>();
        scoreboard.put("serverInfo", serverInfo);
        /**
         *
         */
        if(serverInfo.getGameMode().equalsIgnoreCase("TDM") /* Or other team modes*/){
            scoreboard.put("scoreboard", this.formatTeamBoards(serverInfo, players));
        }
        else{
            scoreboard.put("scoreboard", this.formatSingleBoard(serverInfo, players));
        }
        return scoreboard;
    }

    // TODO all of this stuff belongs in a service, not a controller
    private Map<String, List<KDA>> formatTeamBoards(ServerInfoDto serverInfo, List<PlayerInfoDto> players) {
        List<KDA> scoredUsersTeamA = new ArrayList<>();
        List<KDA> scoredUsersTeamB = new ArrayList<>();
        for(PlayerInfoDto playerInfoDto: players){
            if(playerInfoDto.getTeamId().equalsIgnoreCase("0")){
                scoredUsersTeamA.add(parseKda(playerInfoDto));
            }
            else{
                scoredUsersTeamB.add(parseKda(playerInfoDto));
            }
        }
        // TODO is kills the best gauge?
        Collections.sort(scoredUsersTeamA, getKdaComparator());
        Collections.sort(scoredUsersTeamB, getKdaComparator());
        Map<String, List<KDA>> sorted = new LinkedHashMap<>();
        sorted.put("Red Team", scoredUsersTeamA);
        sorted.put("Blue Team", scoredUsersTeamB);
        return sorted;
    }


    private List<KDA> formatSingleBoard(ServerInfoDto serverInf, List<PlayerInfoDto> players) {
        List<KDA> scoredUsers = new ArrayList<>();
        for(PlayerInfoDto playerInfoDto: players){
            scoredUsers.add(parseKda(playerInfoDto));
        }
        Collections.sort(scoredUsers, getKdaComparator());
        return scoredUsers;
    }


    private Comparator<KDA> getKdaComparator() {
        Comparator<KDA> comparator = new Comparator<KDA>() {
            @Override
            public int compare(KDA o1, KDA o2) {
                int kills = o1.getKills() - o2.getKills();
                if(kills == 0){
                    // If kills are equal, sort by who has the least deaths
                    return o2.getDeaths() - o1.getDeaths();
                }
                return kills;
            }
        };
        return comparator;
    }

    private KDA parseKda(PlayerInfoDto playerInfoDto){
        KDA kda = new KDA();
        kda.setPlayerInfoDto(playerInfoDto);
        if(playerInfoDto != null && playerInfoDto.getKDA() != null){
            String[] kdaSplit = playerInfoDto.getKDA().split("\\/");
            if(kdaSplit.length == 3){
                kda.setKills(Integer.parseInt(kdaSplit[0]));
                kda.setDeaths(Integer.parseInt(kdaSplit[1]));
                kda.setAssists(Integer.parseInt(kdaSplit[2]));
            }
        }
        return kda;
    }
}
