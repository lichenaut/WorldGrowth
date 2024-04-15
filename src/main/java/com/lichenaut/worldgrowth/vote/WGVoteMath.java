package com.lichenaut.worldgrowth.vote;

import com.lichenaut.worldgrowth.Main;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class WGVoteMath {

    private final Main main;
    private final Map<UUID, Boolean> votes = new HashMap<>();

    public void addVote(Player player, boolean vote) {
        votes.put(player.getUniqueId(), vote);
    }

    public boolean unificationThresholdMet() {
        double votesFor = 0;
        double votesAgainst = 0;
        for (Player p : main.getServer().getOnlinePlayers()) {
            if (votes.containsKey(p.getUniqueId())) {
                if (votes.get(p.getUniqueId())) {
                    votesFor++;
                } else {
                    votesAgainst++;
                }
            } else {
                votesAgainst++;
            }
        }

        votes.clear();
        if (votesFor + votesAgainst == 0) return false;

        return votesFor / (votesFor + votesAgainst) * 100 > main.getConfiguration().getDouble("voting-threshold");
    }
}
