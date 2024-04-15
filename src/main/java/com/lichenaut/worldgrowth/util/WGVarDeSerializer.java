package com.lichenaut.worldgrowth.util;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.runnable.WGEventConverter;
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

    public void deserializeVariablesExceptCounts() throws SQLException {
        main.setBorderQuota(databaseManager.getQuota());
        main.setBlocksGrownThisHour(databaseManager.getBlocks());
        main.setPoints(databaseManager.getPoints());
    }

    public void deserializeCount(WGPointEvent<?> event) throws SQLException {
        event.setCount(databaseManager.getEventCount(event.getClass().getSimpleName()));
    }

    public void serializeVariables() throws SQLException {
        try {
            eventCounterManager.getRunnableQueue().get(0).cancel();
        } catch (Exception ignore) {}
        finally {
            eventCounterManager.getRunnableQueue().clear();
            new WGEventConverter(main).run();

            for (WGPointEvent<?> event : pointEvents) {
                int count = event.getCount();
                if (count != 0) databaseManager.setEventCount(event.getClass().getSimpleName(), count);
            }
            databaseManager.setGlobal(main.getBorderQuota(), main.getPoints(), main.getBlocksGrownThisHour());
        }
    }
}
