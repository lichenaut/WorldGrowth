package com.lichenaut.worldgrowth.event;

import com.lichenaut.worldgrowth.db.WGDatabaseManager;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@SuppressWarnings("unused")
public abstract class WGPointEvent<T extends Event> implements Listener {

    private final String simpleClassName = this.getClass().getSimpleName();
    protected final WGDatabaseManager databaseManager;
    protected final Logger logging;
    protected final int quota;
    protected final int points;

    protected abstract void onEvent(T event);

    protected void incrementCount() {
        CompletableFuture
                .runAsync(() -> {
                    try {
                        databaseManager.incrementEventCount(simpleClassName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionallyAsync(e -> {
                    logging.error("Database error when incrementing event count of type {}", simpleClassName);
                    logging.error(e);
                    return null;
                });
    }

    public void checkCount() {
        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return databaseManager.getEventCount(simpleClassName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenComposeAsync(count -> CompletableFuture.supplyAsync(() -> {
                    if (quota == 0 || count < quota) return null;

                    try {
                        databaseManager.setEventCount(simpleClassName, count % quota);
                        databaseManager.addPoints(count / quota * points);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        return databaseManager.getPoints();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .thenComposeAsync(points -> CompletableFuture.runAsync(() -> {
                    if (points != null) logging.info("Points updated to {}", points);
                }))
                .exceptionallyAsync(e -> {
                    logging.error("Database error when converting events to points for event type {}", simpleClassName);
                    logging.error(e);
                    return null;
                });
    }
}
