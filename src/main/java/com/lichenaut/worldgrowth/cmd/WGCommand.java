package com.lichenaut.worldgrowth.cmd;

import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.runnable.WGBoost;
import com.lichenaut.worldgrowth.runnable.WGRunnableManager;
import com.lichenaut.worldgrowth.runnable.WGUnifier;
import com.lichenaut.worldgrowth.util.WGMessager;
import com.lichenaut.worldgrowth.vote.WGVoteMath;
import com.lichenaut.worldgrowth.world.WGWorld;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WGCommand implements CommandExecutor {

    private static CompletableFuture<Void> commandFuture = CompletableFuture.completedFuture(null);
    private final Main main;
    private final WGMessager messager;
    private final Set<String> voteOptions = new HashSet<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public WGCommand(Main main, WGMessager messager) {
        this.main = main;
        this.messager = messager;
        voteOptions.add("yes");
        voteOptions.add("y");
        voteOptions.add("no");
        voteOptions.add("n");
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (checkDisallowed(commandSender, "worldgrowth.command")) return true;

        if (strings.length == 0) {
            commandFuture = commandFuture
                    .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getInvalidCommand(), false));
            return true;
        }

        switch (strings[0]) {
            case "stats" -> {
                if (checkDisallowed(commandSender, "worldgrowth.stats")) return true;

                if (commandSender instanceof Player player && isOnCooldown(player) && !player.isOp()) {
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getCooldownCommand(), false));
                    return true;
                }

                if (commandSender instanceof Player player) {
                    cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                }

                commandFuture = commandFuture
                        .thenAcceptAsync(processed -> {
                            List<BaseComponent> components = new ArrayList<>(Arrays.asList(messager.getStatsCommand1()));
                            components.add(new TextComponent("\n"));
                            BaseComponent[] incompleteBarColor = messager.getIncompleteBarColor();
                            BaseComponent[] completeBar = messager.combineMessage(messager.getCompleteBarColor(), "=");
                            BaseComponent[] incompleteBar = messager.combineMessage(incompleteBarColor, "=");

                            WGVoteMath voteMath = main.getVoteMath();
                            int yesVotes = voteMath.getYesVotes();
                            int totalVotes = voteMath.getVotes().size();
                            int completeBars;
                            double progressPercent;
                            if (totalVotes == 0) {
                                completeBars = 0;
                                progressPercent = 0.00;
                            } else {
                                completeBars = 33 * yesVotes / totalVotes;
                                progressPercent = 100 * yesVotes / (double) totalVotes;
                            }
                            int incompleteBars = 33 - completeBars;
                            buildBar(components, incompleteBarColor, completeBar, incompleteBar, completeBars, incompleteBars);
                            components.add(new TextComponent(String.format("%.2f", progressPercent) + "%\n"));
                            components.addAll(Arrays.asList(messager.getStatsCommand2()));
                            components.add(new TextComponent(main.getConfiguration().getDouble("voting-threshold") + "%\n"));
                            if (commandSender instanceof Player player) {
                                Map<UUID, Boolean> votes = voteMath.getVotes();
                                UUID uuid = player.getUniqueId();
                                String vote = votes.containsKey(uuid) ?
                                        votes.get(uuid) != null ?
                                                votes.get(uuid) ? "yes" : "no" : "no" : "no";
                                components.addAll(Arrays.asList(messager.getStatsCommand3()));
                                components.add(new TextComponent(vote + "\n"));
                            }
                            double duration = main.getUnificationManager().getRunnableQueue().stream().mapToDouble(r -> {
                                        if (r == main.getUnificationManager().getRunnableQueue().stream().findFirst().orElse(null)) {
                                            return r.delay() - (System.currentTimeMillis() - (double) ((WGUnifier) r.runnable()).getTimeStarted()) / 50;
                                        } else {
                                            return r.delay();
                                        }
                                    })
                                    .sum() / 1200;
                            String unificationsDuration = String.format("%.2f", duration);
                            components.addAll(Arrays.asList(messager.getStatsCommand4()));
                            components.add(new TextComponent(unificationsDuration + "\n\n"));

                            double points = main.getPoints();
                            int borderQuota = main.getBorderQuota();
                            completeBars = (int) (33 * points / borderQuota);
                            incompleteBars = 33 - completeBars;
                            buildBar(components, incompleteBarColor, completeBar, incompleteBar, completeBars, incompleteBars);
                            components.add(new TextComponent(new DecimalFormat("#.##").format(points) + "/" + borderQuota + "\n"));
                            Server server = main.getServer();
                            for (WGWorld wgWorld : main.getWorldMath().getWorlds()) {
                                String worldName = wgWorld.name();
                                components.add(new TextComponent(messager.getStatsCommand5()));
                                components.add(new TextComponent(worldName));
                                components.add(new TextComponent(messager.getStatsCommand6()));
                                components.add(new TextComponent(String.format("%.2f", Objects.requireNonNull(server.getWorld(worldName)).getWorldBorder().getSize())));
                                components.add(new TextComponent("\n"));
                            }

                            components.add(new TextComponent("\n"));
                            components.addAll(Arrays.asList(messager.getStatsCommand7()));
                            components.add(new TextComponent(String.valueOf(main.getBoostMultiplier())));

                            BaseComponent[] baseComponents = new BaseComponent[components.size()];
                            components.toArray(baseComponents);
                            messager.sendMsg(commandSender, baseComponents, false);
                        });

                return true;
            }
            case "help" -> {
                if (checkDisallowed(commandSender, "worldgrowth.help")) return true;

                commandFuture = commandFuture
                        .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getHelpCommand(), false));
                return true;
            }
            case "reload" -> {
                if (checkDisallowed(commandSender, "worldgrowth.reload")) return true;

                main.reloadWG();

                main.setMainFuture(main.getMainFuture()
                                .thenAcceptAsync(reloaded -> messager.sendMsg(commandSender, messager.getReloadCommand(), false)));
                return true;
            }
            case "boost" -> {
                if (commandSender instanceof Player player) {
                    if (player.isOp()) {
                        commandFuture = commandFuture
                                .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getOnlyConsoleCommand(), false));
                    }
                    return true;
                }
                if (strings.length != 3) {
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getUsageBoostCommand(), false));
                    return true;
                }

                commandFuture = commandFuture
                        .thenApplyAsync(processed -> CompletableFuture.supplyAsync(() -> {
                            String multiplier = strings[1];
                            String ticks = strings[2];
                            if (!multiplier.matches("[0-9]+(\\.[0-9]+)?") || !ticks.matches("[0-9]+")) return null;

                            return new String[]{multiplier, ticks};
                        }))
                        .thenAcceptAsync(valuesFuture -> valuesFuture.thenAcceptAsync(values -> {
                            if (values == null) {
                                messager.sendMsg(commandSender, messager.getUsageBoostCommand(), false);
                                return;
                            }

                            double multiplierDouble = Double.parseDouble(values[0]);
                            long delay = Integer.parseInt(values[1]);
                            WGRunnableManager boosterManager = main.getBoostManager();
                            boosterManager.addRunnable(new WGBoost(main, multiplierDouble) {
                                @Override
                                public void run() {
                                    runBoost(delay);
                                }
                            }, 0L);
                            boosterManager.addRunnable(new WGBoost(main, multiplierDouble) {
                                @Override
                                public void run() {
                                    runReset();
                                }
                            }, delay);
                        }));
                return true;
            }
            case "vote" -> {
                if (!(commandSender instanceof Player)) {
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getOnlyPlayerCommand(), false));
                    return true;
                }

                if (checkDisallowed(commandSender, "worldgrowth.vote")) return true;

                if (strings.length != 2 || !voteOptions.contains(strings[1].toLowerCase())) {
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getUsageVoteCommand(), false));
                    return true;
                }

                if (strings[1].equalsIgnoreCase("yes") || strings[1].equalsIgnoreCase("y")) {
                    main.getVoteMath().addVote((Player) commandSender, true);
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getVoteYesCommand(), false));
                } else if (strings[1].equalsIgnoreCase("no") || strings[1].equalsIgnoreCase("n")) {
                    main.getVoteMath().addVote((Player) commandSender, false);
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getVoteNoCommand(), false));
                } else {
                    commandFuture = commandFuture
                            .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getUsageVoteCommand(), false));
                }

                return true;
            }
        }

        commandFuture = commandFuture
                .thenAcceptAsync(processed -> messager.sendMsg(commandSender, messager.getInvalidCommand(), false));
        return true;
    }

    private boolean checkDisallowed(CommandSender sender, String permission) {
        return sender instanceof Player && !sender.hasPermission(permission);
    }

    private boolean isOnCooldown(Player player) {
        if (cooldowns.containsKey(player.getUniqueId())) {
            return System.currentTimeMillis() < cooldowns.get(player.getUniqueId()) + 20000;
        }

        return false;
    }

    private void buildBar(List<BaseComponent> components, BaseComponent[] incompleteBarColor, BaseComponent[] completeBar, BaseComponent[] incompleteBar, int completeBars, int incompleteBars) {
        components.addAll(Arrays.asList(messager.combineMessage(incompleteBarColor, "[")));
        for (int i = 0; i < completeBars; i++) components.addAll(Arrays.asList(completeBar));
        for (int i = 0; i < incompleteBars; i++) components.addAll(Arrays.asList(incompleteBar));
        components.addAll(Arrays.asList(messager.combineMessage(incompleteBarColor, "] ")));
    }
}
