package com.lichenaut.worldgrowth.util;

import org.bukkit.scheduler.BukkitRunnable;

public record WGRunnable(BukkitRunnable runnable, long delay) {

    public void run() {
        runnable.run();
    }
}
