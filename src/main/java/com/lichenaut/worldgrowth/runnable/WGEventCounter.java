package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class WGEventCounter extends BukkitRunnable {

    private final Main main;
    private final Logger logging;
    private final Set<WGPointEvent<?>> pointEvents;
    private final WGRunnableManager counterManager;

    @Override
    public void run() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (WGPointEvent<?> pointEvent : pointEvents) {
            future = future
                    .thenAcceptAsync(counted -> {
                        int quota = pointEvent.getQuota();
                        int count = pointEvent.getCount();
                        if (count < quota) return;

                        pointEvent.setCount(count % quota);
                        main.addPoints(count / quota * pointEvent.getPointValue());
            });
        }
        future.whenComplete((result, e) -> {
            if (e != null) {
                logging.error("Error while converting event counts to points!");
                logging.error(e);
            }
            counterManager.addRunnable(this, 6000L);
        });
    }
}
