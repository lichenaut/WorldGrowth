package com.lichenaut.worldgrowth.event;

import com.lichenaut.worldgrowth.util.WGDatabaseManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.sql.SQLException;

@RequiredArgsConstructor
public abstract class WGPointEvent<T extends Event> implements Listener {

    protected final WGDatabaseManager databaseManager;

    protected abstract void onEvent(T event) throws SQLException;

    protected void incrementCount() throws SQLException {
        databaseManager.incrementEvent(this.getClass().getSimpleName());
    }
}
