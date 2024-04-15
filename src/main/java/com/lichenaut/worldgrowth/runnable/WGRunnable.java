package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import org.bukkit.scheduler.BukkitRunnable;

public record WGRunnable(Main main, BukkitRunnable runnable, long delay) {

    public void run() { runnable.run(); }

    public void cancel() { runnable.cancel(); }
}
