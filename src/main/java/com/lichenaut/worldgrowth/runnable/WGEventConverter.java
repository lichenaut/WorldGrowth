package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class WGEventConverter extends BukkitRunnable {

    private final Main main;

    @Override
    public void run() {
        boolean willTopMaxGrowthPerHour = main.getWorldMath().willTopMaxGrowthPerHour();
        Set<WGPointEvent<?>> pointEvents = main.getPointEvents();
        CompletableFuture<Void> conversionProcess = CompletableFuture.completedFuture(null);
        for (WGPointEvent<?> pointEvent : pointEvents) {
            conversionProcess = conversionProcess
                    .thenAcceptAsync(counted -> {
                        int quota = pointEvent.getQuota();
                        int count = pointEvent.getCount();
                        if (count < quota) return;

                        pointEvent.setCount(count % quota);
                        //Convert counts to no points when the max block growth per hour is reached.
                        if (!willTopMaxGrowthPerHour) main.addPoints(count / quota * pointEvent.getPointValue());
            });
        }
        conversionProcess.whenComplete((result, e) -> {
            if (e != null) {
                Logger logging = main.getLogging();
                logging.error("Error while converting event counts to points!");
                logging.error(e);
            }
            main.getEventCounterManager().addRunnable(this, 6000L);
        });
    }
}
