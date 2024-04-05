package com.lichenaut.worldgrowth.util;

import com.lichenaut.worldgrowth.Main;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ConcurrentLinkedQueue;

@RequiredArgsConstructor
public class WGAsyncRunnabler {

    private final Main plugin;
    private final ConcurrentLinkedQueue<WGRunnable> runnableQueue = new ConcurrentLinkedQueue<>();
    private BukkitTask currentTask;

    public synchronized void addRunnable(WGRunnable runnable) {
        runnableQueue.offer(runnable);
        if (currentTask == null) scheduleNextRunnable();
    }

    private synchronized void scheduleNextRunnable() {
        WGRunnable runnable = runnableQueue.poll();
        if (runnable == null) return;

        currentTask = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            try {
                runnable.run();
            } finally {
                currentTask = null;
                scheduleNextRunnable();
            }
        }, runnable.delay());
    }

    public synchronized void serializeQueue() {}
}
