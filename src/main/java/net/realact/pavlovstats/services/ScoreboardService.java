package net.realact.pavlovstats.services;

import net.realact.pavlovstats.models.dtos.Scoreboard;

import java.io.IOException;
import java.util.List;

public interface ScoreboardService {
    Scoreboard getScoreboard() throws IOException;

    void saveScoreboard(Scoreboard scoreboard);

    Scoreboard getById(String id);

    List<Scoreboard> getAll();
}
