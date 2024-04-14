package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.util.WGMessager;
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
            int count = pointEvent.getCount();
            if (count == 0) continue;

            int quota = pointEvent.getQuota();
            if (count < quota) continue;

            pointEvent.setCount(count % quota);
            //Convert met quotas to no points when the max block growth per hour is reached.
            if (!willTopMaxGrowthPerHour) main.addPoints((double) count / quota * pointEvent.getPointValue() * main.getBoostMultiplier());
        }

        if (!main.getWorldMath().willTopMaxGrowthPerHour() && main.getPoints() >= main.getBorderQuota()) {
            main.getBossBar().incomingIndicator();
            WGMessager messager = main.getMessager();
            messager.spreadMsg(
                    true,
                    messager.getGrowthIncoming(),
                    true);
        }

        main.getEventCounterManager().addRunnable(this, 6000L);
    }
}
