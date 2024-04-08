package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;

@RequiredArgsConstructor
public class WGRunnableManager {

    private final Main plugin;
    private final LinkedList<WGRunnable> runnableQueue = new LinkedList<>();
    private BukkitTask currentTask;

    public void addRunnable(BukkitRunnable bukkitRunnable, long delay) {
        runnableQueue.offer(new WGRunnable(bukkitRunnable, delay));
        if (currentTask == null) scheduleNextRunnable();
    }

    private void scheduleNextRunnable() {
        WGRunnable runnable = runnableQueue.poll();
        if (runnable == null) return;

        currentTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            try {
                runnable.run();
            } finally {
                currentTask = null;
                scheduleNextRunnable();
            }
        }, runnable.delay());
    }

    //public void serializeQueue() {}
}