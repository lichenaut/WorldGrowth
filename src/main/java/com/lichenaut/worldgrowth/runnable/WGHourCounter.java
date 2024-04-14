package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGMessager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@RequiredArgsConstructor
public class WGHourCounter extends BukkitRunnable {

    private final Main main;
    private final long timeStarted = System.currentTimeMillis();

    @Override
    public void run() {
        if (main.getWorldMath().willTopMaxGrowthPerHour()) {
            WGMessager messager = main.getMessager();
            messager.spreadMsg(
                    true,
                    messager.getPointsOn(),
                    true);
        }
        main.setBlocksGrownThisHour(0);
        main.getHourMaxManager().addRunnable(this, 72000L);
    }
}
