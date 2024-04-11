package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGMessager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@RequiredArgsConstructor
public abstract class WGBoost extends BukkitRunnable {

    private final Main main;
    private final double multiplier;
    private final long timeStarted = System.currentTimeMillis();

    public void runBoost(long delay) {
        main.setBoostMultiplier(multiplier);
        WGMessager messager = main.getMessager();
        messager.spreadMsg(
                main.getConfiguration().getBoolean("broadcast-boosts"),
                messager.concatArrays(
                    messager.combineMessage(messager.getBoostedGains1(), String.valueOf(multiplier)),
                    messager.combineMessage(messager.getBoostedGains2(), String.format("%.2f", (double) delay / 1200)),
                    messager.getBoostedGains3()));
    }

    public void runReset() {
        main.setBoostMultiplier(1.0);
        WGMessager messager = main.getMessager();
        messager.spreadMsg(
                main.getConfiguration().getBoolean("broadcast-boosts"),
                messager.getDeboostedGains());
    }
}
