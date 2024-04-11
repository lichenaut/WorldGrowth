package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
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
        main.setBlocksGrownThisHour(0);
        main.getHourMaxManager().addRunnable(this, 72000L);
    }
}
