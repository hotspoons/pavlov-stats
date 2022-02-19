package net.realact.pavlovstats.repositories;

import net.realact.pavlovstats.models.dtos.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, String> {
}
