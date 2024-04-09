package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;

@Getter
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
        WGRunnable runnable = runnableQueue.peek();
        if (runnable == null) return;

        currentTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            try {
                runnable.run();
            } finally {
                currentTask = null;
                runnableQueue.pop();
                scheduleNextRunnable();
            }
        }, runnable.delay());
    }
}
