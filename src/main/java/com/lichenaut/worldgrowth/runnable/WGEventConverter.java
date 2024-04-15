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
        main.getEventCounterManager().addRunnable(this, 6000L);
        boolean willTopMaxGrowthPerHour = main.getWorldMath().willTopMaxGrowthPerHour();
        int borderQuota = main.getBorderQuota();
        Set<WGPointEvent<?>> pointEvents = main.getPointEvents();
        for (WGPointEvent<?> pointEvent : pointEvents) {
            if (main.getPoints() == borderQuota) break;

            int count = pointEvent.getCount();
            if (count == 0) continue;

            int quota = pointEvent.getQuota();
            if (count < quota) continue;

            pointEvent.setCount(count % quota);
            if (willTopMaxGrowthPerHour) continue;

            main.addPoints((double) (count / quota) * pointEvent.getPointValue() * main.getBoostMultiplier());
            if (main.getPoints() >= borderQuota) {
                main.setPoints(borderQuota);
                break;
            }
        }

        if (main.getPoints() == borderQuota && !willTopMaxGrowthPerHour && main.isEnabled()) {
            main.getBossBar().incomingIndicator();
            WGMessager messager = main.getMessager();
            messager.spreadMsg(
                    true,
                    messager.getGrowthIncoming(),
                    true);
        }
    }
}
