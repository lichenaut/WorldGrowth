package com.lichenaut.worldgrowth.vote;

import com.lichenaut.worldgrowth.Main;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
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
        for (Player player : main.getServer().getOnlinePlayers()) {
            if (votes.containsKey(player.getUniqueId())) {
                if (votes.get(player.getUniqueId())) {
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

    public int getYesVotes() {
        int votesFor = 0;
        for (Player player : main.getServer().getOnlinePlayers()) {
            if (votes.containsKey(player.getUniqueId()) && votes.get(player.getUniqueId())) votesFor++;
        }
        return votesFor;
    }
}
