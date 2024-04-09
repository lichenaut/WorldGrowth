package com.lichenaut.worldgrowth.runnable;

import org.bukkit.scheduler.BukkitRunnable;

public record WGRunnable(BukkitRunnable runnable, long delay) {

    public void run() {
        runnable.run();
    }

    public Object getMultiplier() {
        if (runnable instanceof WGBoost) return ((WGBoost) runnable).getMultiplier();

        return null;
    }

    public Object getTimeStarted() {
        if (runnable instanceof WGBoost) return ((WGBoost) runnable).getTimeStarted();

        return null;
    }
}
