package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;

@Getter
public class WGRunnableManager {

    private final Main main;
    private final BukkitScheduler scheduler;
    private final LinkedList<WGRunnable> runnableQueue = new LinkedList<>();
    private BukkitTask currentTask;

    public WGRunnableManager(Main main) {
        this.main = main;
        scheduler = main.getServer().getScheduler();
    }

    public void addRunnable(BukkitRunnable bukkitRunnable, long delay) {
        runnableQueue.offer(new WGRunnable(bukkitRunnable, delay));
        if (currentTask == null) scheduleNextRunnable();
    }

    private void scheduleNextRunnable() {
        WGRunnable runnable = runnableQueue.peek();
        if (runnable == null) return;

        currentTask = scheduler.runTaskLater(main, () -> {
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
