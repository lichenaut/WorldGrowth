package com.lichenaut.worldgrowth.util;

import com.lichenaut.worldgrowth.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitScheduler;

import javax.annotation.Nullable;

public class WGBossBar {

    private final Main main;
    private final BukkitScheduler scheduler;

    public WGBossBar(Main main) {
        this.main = main;
        this.scheduler = Bukkit.getScheduler();
    }

    public void incomingIndicator() {
        showIndicator(null, Sound.BLOCK_SCAFFOLDING_PLACE, 0.1F, 0.5F);
        scheduler.runTaskLaterAsynchronously(main, () -> showIndicator(null, Sound.BLOCK_SCAFFOLDING_PLACE, 0.25F, 1.5F), 3L);
    }

    public void growthIndicator() {
        BossBar bossBar = Bukkit.createBossBar(ChatColor.GREEN  + "" +  ChatColor.BOLD + "Growth Event!", BarColor.GREEN, BarStyle.SEGMENTED_12);
        bossBar.setProgress(0.0);
        showIndicator(bossBar, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.25F, 0.5F);
    }

    public void unificationIndicator() {
        BossBar bossBar = Bukkit.createBossBar(ChatColor.YELLOW  + "" +  ChatColor.BOLD + "Unification Event!", BarColor.YELLOW, BarStyle.SEGMENTED_12);
        bossBar.setProgress(0.0);
        showIndicator(bossBar, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 0.5F);
    }

    public void deunificationIndicator() {
        BossBar bossBar = Bukkit.createBossBar(ChatColor.RED  + "" +  ChatColor.BOLD + "Unification Event End!", BarColor.RED, BarStyle.SEGMENTED_12);
        bossBar.setProgress(1.0);
        showIndicator(bossBar, Sound.BLOCK_NOTE_BLOCK_BIT, 0.5F, 0.5F);
    }

    public void showIndicator(@Nullable BossBar bossBar, Sound sound, float volume, float pitch) {
        if (bossBar != null) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.playSound(player.getLocation(), sound, volume, pitch);
                bossBar.addPlayer(player);
            });
            bossBar.setVisible(true);
            scheduler.runTaskLaterAsynchronously(main, () -> bossBar.setProgress(0.7), 16L);
            scheduler.runTaskLaterAsynchronously(main, () -> {
                bossBar.setVisible(false);
                bossBar.removeAll();
            }, 24L);
        } else {
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), sound, volume, pitch));
        }
    }
}
