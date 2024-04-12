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
        main.getLogging().warn("WGEventConverter run");
        boolean willTopMaxGrowthPerHour = main.getWorldMath().willTopMaxGrowthPerHour();
        Set<WGPointEvent<?>> pointEvents = main.getPointEvents();
        System.out.println(pointEvents.size());
        for (WGPointEvent<?> pointEvent : pointEvents) {
            System.out.println("e"); //TODO why does this only print once
            int quota = pointEvent.getQuota();
            int count = pointEvent.getCount();
            System.out.println(count < quota);
            if (count < quota) return;

            main.getLogging().warn("WGEventConverter run{}", count);
            main.getLogging().warn(willTopMaxGrowthPerHour);
            System.out.println("WGEventConverter run" + count);
            System.out.println(quota);
            System.out.println(count / quota);
            System.out.println(pointEvent.getPointValue());
            System.out.println(main.getBoostMultiplier());
            System.out.println((double) count / quota * pointEvent.getPointValue() * main.getBoostMultiplier());

            pointEvent.setCount(count % quota);
            //Convert counts to no points when the max block growth per hour is reached.
            if (!willTopMaxGrowthPerHour) main.addPoints((double) count / quota * pointEvent.getPointValue() * main.getBoostMultiplier());
        }

        main.getEventCounterManager().addRunnable(this, 6000L);
    }
}
