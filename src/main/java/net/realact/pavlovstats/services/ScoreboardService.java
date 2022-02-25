package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.dtos.Scoreboard;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ScoreboardService {
    Scoreboard getScoreboardFromRCON() throws IOException;
    void saveScoreboard(Scoreboard scoreboard);
    Scoreboard getById(String id);
    List<Scoreboard> getAll();
    List<Scoreboard> searchByNameAndDate(String name, Date start, Date end);

    Scoreboard getCurrentScoreboard();
    void setCurrentScoreboard(Scoreboard scoreboard);
}
