package com.lichenaut.worldgrowth.cmd;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.runnable.WGBoost;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.lichenaut.worldgrowth.util.WGMessager;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class WGCommand implements CommandExecutor {

    private final Main plugin;
    private final WGMessager messager;

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (checkDisallowed(commandSender, "worldgrowth.command")) return true;

        if (strings.length == 0) {
            messager.sendMsg(commandSender, messager.getInvalidCommand());
            return true;
        }

        switch (strings[0]) {
            case "progress" -> {
                if (checkDisallowed(commandSender, "worldgrowth.progress")) return true;
                //messager.sendMsg(commandSender, );
                return true;
            }
            case "help" -> {
                if (checkDisallowed(commandSender, "worldgrowth.help")) return true;
                messager.sendMsg(commandSender, messager.getHelpCommand());
                return true;
            }
            case "reload" -> {
                if (checkDisallowed(commandSender, "worldgrowth.reload")) return true;
                plugin.reloadWG();
                messager.sendMsg(commandSender, messager.getReloadCommand());
                return true;
            }
            case "boost" -> {
                if (commandSender instanceof Player) {
                    messager.sendMsg(commandSender, messager.getOnlyConsoleCommand());
                    return true;
                }
                if (strings.length != 3) {
                    messager.sendMsg(commandSender, messager.getUsageBoostCommand());
                    return true;
                }
                String multiplier = strings[1];
                String ticks = strings[2];
                if (!multiplier.matches("[0-9]+") || !ticks.matches("[0-9]+")) {
                    messager.sendMsg(commandSender, messager.getUsageBoostCommand());
                    return true;
                }

                int multiplierInt = Integer.parseInt(multiplier);
                long delay = Integer.parseInt(strings[2]);
                WGRunnableManager boosterManager = plugin.getBoostManager();
                boosterManager.addRunnable(new WGBoost(plugin, messager, multiplierInt) {
                    @Override
                    public void run() {
                        runBoost(multiplierInt, delay);
                    }
                }, 0L);
                boosterManager.addRunnable(new WGBoost(plugin, messager, 1) {
                    @Override
                    public void run() {
                        runReset();
                    }
                }, delay);
                return true;
            }
        }

        messager.sendMsg(commandSender, messager.getInvalidCommand());
        return true;
    }

    private boolean checkDisallowed(CommandSender sender, String permission) {
        return sender instanceof Player && !sender.hasPermission(permission);
    }
}
