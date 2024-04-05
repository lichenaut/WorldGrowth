package com.lichenaut.worldgrowth.cmd;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.util.WGAsyncRunnabler;
import com.lichenaut.worldgrowth.util.WGMsgBank;
import com.lichenaut.worldgrowth.util.WGRunnable;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class WGCommand implements CommandExecutor {

    private final Main plugin;
    private final WGMsgBank msgBank;

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (checkDisallowed(commandSender, "worldgrowth.command")) return true;

        if (strings.length == 0) {
            sendMsg(commandSender, msgBank.getInvalidCommand());
            return true;
        }

        switch (strings[0]) {
            case "help" -> {
                if (checkDisallowed(commandSender, "worldgrowth.help")) return true;
                sendMsg(commandSender, msgBank.getHelpCommand());
                return true;
            }
            case "reload" -> {
                if (checkDisallowed(commandSender, "worldgrowth.reload")) return true;
                plugin.reloadWG();
                sendMsg(commandSender, msgBank.getReloadCommand());
                return true;
            }
            case "boost" -> {
                if (commandSender instanceof Player) {
                    sendMsg(commandSender, msgBank.getOnlyConsoleCommand());
                    return true;
                }
                if (strings.length != 3) {
                    sendMsg(commandSender, msgBank.getUsageBoostCommand());
                    return true;
                }
                String multiplier = strings[1];
                String ticks = strings[2];
                if (!multiplier.matches("[0-9]+") || !ticks.matches("[0-9]+")) {
                    sendMsg(commandSender, msgBank.getUsageBoostCommand());
                    return true;
                }

                long delay = Integer.parseInt(strings[2]);
                WGAsyncRunnabler boosterManager = plugin.getBoosterManager();
                boosterManager.addRunnable(new WGRunnable(new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.setBoostMultiplier(Integer.parseInt(multiplier));
                        spreadMsg(plugin.getConfiguration().getBoolean("broadcast-boosts"), msgBank.concatArrays(
                                msgBank.combineMessage(msgBank.getBoostedGains1(), multiplier),
                                msgBank.combineMessage(msgBank.getBoostedGains2(), String.format("%.2f", (double) delay / 1200)),
                                msgBank.getBoostedGains3()));
                    }
                }, 0L));
                boosterManager.addRunnable(new WGRunnable(new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.setBoostMultiplier(1);
                        spreadMsg(plugin.getConfiguration().getBoolean("broadcast-boosts"), msgBank.getDeboostedGains());
                    }
                }, delay));
                return true;
            }
        }

        sendMsg(commandSender, msgBank.getInvalidCommand());
        return true;
    }

    private boolean checkDisallowed(CommandSender sender, String permission) {
        return sender instanceof Player && !sender.hasPermission(permission);
    }

    private void sendMsg(CommandSender sender, BaseComponent[] message) {
        if (sender instanceof Player) {
            sender.spigot().sendMessage(message);
            return;
        }

        infoLog(message);
    }

    private void spreadMsg(boolean broadcast, BaseComponent[] message) {
        if (broadcast) plugin.getServer().spigot().broadcast(message);
        infoLog(message);
    }

    private void infoLog(BaseComponent[] message) {
        plugin.getLogging().info(new TextComponent(message).toLegacyText().replaceAll("§[0-9a-fA-FklmnoKLMNO]", ""));
    }
}
