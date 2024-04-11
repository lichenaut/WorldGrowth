package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

@RequiredArgsConstructor
public class WGEventConverter extends BukkitRunnable {

    private final Main main;

    @Override
    public void run() {
        boolean willTopMaxGrowthPerHour = main.getWorldMath().willTopMaxGrowthPerHour();
        Set<WGPointEvent<?>> pointEvents = main.getPointEvents();
        for (WGPointEvent<?> pointEvent : pointEvents) {
            int quota = pointEvent.getQuota();
            int count = pointEvent.getCount();
            if (count < quota) return;

            pointEvent.setCount(count % quota);
            //Convert counts to no points when the max block growth per hour is reached.
            if (!willTopMaxGrowthPerHour) main.addPoints(count / quota * pointEvent.getPointValue());
        }

        main.getEventCounterManager().addRunnable(this, 6000L);
    }
}
