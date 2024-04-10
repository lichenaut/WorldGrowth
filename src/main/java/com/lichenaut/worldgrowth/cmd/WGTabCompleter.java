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
        if (commandSender instanceof Player && commandSender.hasPermission("worldgrowth.help") && strings.length == 1) options.add("help");
        if (commandSender instanceof Player && commandSender.hasPermission("worldgrowth.progress") && strings.length == 1) options.add("progress");
        if (commandSender instanceof Player && commandSender.hasPermission("worldgrowth.reload") && strings.length == 1) options.add("reload");
        return options;
    }
}
