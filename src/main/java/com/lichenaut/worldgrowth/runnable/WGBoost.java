package com.lichenaut.worldgrowth.runnable;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGMessager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
        ComponentBuilder minutes = new ComponentBuilder("");
        minutes.bold(true);
        if (delay == 1200) {
            minutes.append(messager.getMinute());
        } else {
            minutes.append(messager.getMinutes());
        }
        messager.spreadMsg(
                main.getConfiguration().getBoolean("broadcast-boosts"),
                messager.concatArrays(
                    messager.combineMessage(messager.getBoostedGains1(), String.valueOf(multiplier)),
                    messager.combineMessage(messager.getBoostedGains2(), String.format("%.2f", (double) delay / 1200)),
                    messager.combineMessage(minutes.create(), "!")),
                true);
    }

    public void runReset() {
        main.setBoostMultiplier(1.0);
        WGMessager messager = main.getMessager();
        BaseComponent[] concatMessage = messager.getDeboostedGains();

        if (main.getBorderManager().getRunnableQueue().size() > 1) {
            concatMessage = messager.concatArrays(
                    concatMessage,
                    messager.getRunnableQueued());
        }

        messager.spreadMsg(
                main.getConfiguration().getBoolean("broadcast-boosts"),
                concatMessage,
                true);
    }
}
