package com.lichenaut.worldgrowth.util;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.runnable.WGEventCounter;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.Set;

@RequiredArgsConstructor
public class WGVarDeSerializer {

    private final Main main;
    private final Set<WGPointEvent<?>> pointEvents;
    private final WGRunnableManager eventCounterManager;
    private final WGDBManager databaseManager;

    public void deserializeVariablesExceptCount() throws SQLException {
        main.setBorderQuota(databaseManager.getQuota());
        main.setPoints(databaseManager.getPoints());
        main.setBorderSize(databaseManager.getSize());
    }

    public void deserializeCount(WGPointEvent<?> event) throws SQLException {
        event.setCount(databaseManager.getEventCount(event.getClass().getSimpleName()));
    }

    public void serializeVariables() throws SQLException {
        try {
            eventCounterManager.getRunnableQueue().get(0).cancel();
        } catch (IllegalStateException ignore) {}

        eventCounterManager.getRunnableQueue().clear();
        new WGEventCounter(main).run();

        for (WGPointEvent<?> event : pointEvents) databaseManager.setEventCount(event.getClass().getSimpleName(), event.getCount());
        databaseManager.setGlobal(main.getBorderQuota(), main.getPoints(), main.getBorderSize());
    }
}
