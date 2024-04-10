package com.lichenaut.worldgrowth.util;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.runnable.WGEventCounter;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Set;

@RequiredArgsConstructor
public class WGVarDeSerializer {

    private final Main main;
    private final Logger logging;
    private final Set<WGPointEvent<?>> pointEvents;
    private final WGRunnableManager eventCounterManager;
    private final WGDBManager databaseManager;

    public void serializeCountsQuotaPoints() throws SQLException {
        try {
            eventCounterManager.getRunnableQueue().get(0).cancel();
        } catch (IllegalStateException ignore) {}

        eventCounterManager.getRunnableQueue().clear();
        new WGEventCounter(main, logging, pointEvents, eventCounterManager).run();

        for (WGPointEvent<?> event : pointEvents) databaseManager.setEventCount(event.getClass().getSimpleName(), event.getCount());
        databaseManager.setGlobal(main.getBorderQuota(), main.getPoints());
    }

    public void deserializePointsQuota() throws SQLException {
        main.setBorderQuota(databaseManager.getQuota());
        main.setPoints(databaseManager.getPoints());
    }

    public void deserializeCount(WGPointEvent<?> event) throws SQLException {
        event.setCount(databaseManager.getEventCount(event.getClass().getSimpleName()));
    }
}
