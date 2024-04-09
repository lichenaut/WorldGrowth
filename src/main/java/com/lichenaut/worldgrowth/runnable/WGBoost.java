package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGMessager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@RequiredArgsConstructor
public abstract class WGBoost extends BukkitRunnable {

    private final Main plugin;
    private final WGMessager messager;
    private final int multiplier;
    private long timeStarted;

    public void runBoost(int multiplier, long delay) {
        plugin.setBoostMultiplier(multiplier);
        timeStarted = System.currentTimeMillis();
        messager.spreadMsg(
                plugin.getConfiguration().getBoolean("broadcast-boosts"),
                messager.concatArrays(
                    messager.combineMessage(messager.getBoostedGains1(), String.valueOf(multiplier)),
                    messager.combineMessage(messager.getBoostedGains2(), String.format("%.2f", (double) delay / 1200)),
                    messager.getBoostedGains3()));
    }

    public void runReset() {
        plugin.setBoostMultiplier(1);
        messager.spreadMsg(
                plugin.getConfiguration().getBoolean("broadcast-boosts"),
                messager.getDeboostedGains());
    }
}
