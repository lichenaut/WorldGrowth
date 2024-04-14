package com.lichenaut.worldgrowth.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WGTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        List<String> options = new ArrayList<>();
        if (strings.length == 1) {
            if (commandSender instanceof Player player) {
                if (player.hasPermission("worldgrowth.help")) options.add("help");
                if (player.hasPermission("worldgrowth.info")) options.add("info");
                if (player.hasPermission("worldgrowth.reload")) options.add("reload");
                if (player.hasPermission("worldgrowth.vote")) options.add("vote");
            }
        } else if (strings.length == 2) {
            if (strings[0].equals("vote")) {
                if (commandSender instanceof Player && commandSender.hasPermission("worldgrowth.vote")) {
                    options.add("yes");
                    options.add("no");
                }
            }
        }

        return options;
    }
}
