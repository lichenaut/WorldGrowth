package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class WGEventChecker extends BukkitRunnable {

    private final Main main;
    private final Logger logging;
    private final Set<WGPointEvent<?>> pointEvents;
    private final WGRunnableManager pointManager;

    @Override
    public void run() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        for (WGPointEvent<?> pointEvent : pointEvents) future = future.thenAcceptAsync(checked -> checkCount(pointEvent));
        future.whenComplete((result, e) -> {
            if (e != null) {
                logging.error("Error while converting event counts to points!");
                logging.error(e);
            }
            pointManager.addRunnable(this, 6000L);
        });
    }

    public void checkCount(WGPointEvent<?> event) {
        int quota = event.getQuota();
        int count = event.getCount();
        if (count < quota) return;

        event.setCount(count % quota);
        main.addPoints(count / quota * event.getPointValue());
    }
}
