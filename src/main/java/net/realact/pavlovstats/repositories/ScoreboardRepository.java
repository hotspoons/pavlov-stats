package net.realact.pavlovstats.repositories;

import net.realact.pavlovstats.models.dtos.Scoreboard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreboardRepository extends CrudRepository<Scoreboard, String> {
}
