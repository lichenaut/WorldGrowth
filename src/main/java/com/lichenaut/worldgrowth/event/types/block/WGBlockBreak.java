package com.lichenaut.worldgrowth.event.types.block;

import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.util.WGDatabaseManager;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.SQLException;

public class WGBlockBreak extends WGPointEvent<BlockBreakEvent> {

    public WGBlockBreak(WGDatabaseManager databaseManager) {super(databaseManager);}

    @Override
    protected void onEvent(BlockBreakEvent event) throws SQLException {

    }
}
