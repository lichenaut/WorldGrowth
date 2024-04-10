package com.lichenaut.worldgrowth.util;


import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.event.block.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;

import java.sql.SQLException;
import java.util.Set;

@RequiredArgsConstructor
public class WGRegisterer {

    private final Main main;
    private final Configuration configuration;
    private final WGDBManager databaseManager;
    private final PluginManager pluginManager;
    private final WGVarDeSerializer varDeSerializer;
    private final Set<WGPointEvent<?>> pointEvents;

    public void registerEvents() throws SQLException {
        ConfigurationSection events = configuration.getConfigurationSection("events");
        if (databaseManager != null && events != null) {
            for (String event : events.getKeys(false)) {
                ConfigurationSection eventSection = events.getConfigurationSection(event);
                if (eventSection == null) continue;

                int quota = eventSection.getInt("quota");
                if (quota < 1) continue;

                int pointValue = eventSection.getInt("points");
                switch (event) {
                    case "bell-resonate":
                        BellResonate bellResonate = new BellResonate(quota, pointValue);
                        pluginManager.registerEvents(bellResonate, main);
                        varDeSerializer.deserializeCount(bellResonate);
                        pointEvents.add(bellResonate);
                        break;
                    case "bell-ring":
                        BellRing bellRing = new BellRing(quota, pointValue);
                        pluginManager.registerEvents(bellRing, main);
                        varDeSerializer.deserializeCount(bellRing);
                        pointEvents.add(bellRing);
                        break;
                    case "block-break":
                        BlockBreak blockBreak = new BlockBreak(quota, pointValue);
                        pluginManager.registerEvents(blockBreak, main);
                        varDeSerializer.deserializeCount(blockBreak);
                        pointEvents.add(blockBreak);
                        break;
                    case "block-burn":
                        BlockBurn blockBurn = new BlockBurn(quota, pointValue);
                        pluginManager.registerEvents(blockBurn, main);
                        varDeSerializer.deserializeCount(blockBurn);
                        pointEvents.add(blockBurn);
                        break;
                    case "block-can-build":
                        BlockCanBuild blockCanBuild = new BlockCanBuild(quota, pointValue);
                        pluginManager.registerEvents(blockCanBuild, main);
                        varDeSerializer.deserializeCount(blockCanBuild);
                        pointEvents.add(blockCanBuild);
                        break;
                    case "block-cook":
                        BlockCook blockCook = new BlockCook(quota, pointValue);
                        pluginManager.registerEvents(blockCook, main);
                        varDeSerializer.deserializeCount(blockCook);
                        pointEvents.add(blockCook);
                        break;
                    case "block-damage":
                        BlockDamage blockDamage = new BlockDamage(quota, pointValue);
                        pluginManager.registerEvents(blockDamage, main);
                        varDeSerializer.deserializeCount(blockDamage);
                        pointEvents.add(blockDamage);
                        break;
                    case "block-damage-abort":
                        BlockDamageAbort blockDamageAbort = new BlockDamageAbort(quota, pointValue);
                        pluginManager.registerEvents(blockDamageAbort, main);
                        varDeSerializer.deserializeCount(blockDamageAbort);
                        pointEvents.add(blockDamageAbort);
                        break;
                    case "block-dispense":
                        BlockDispense blockDispense = new BlockDispense(quota, pointValue);
                        pluginManager.registerEvents(blockDispense, main);
                        varDeSerializer.deserializeCount(blockDispense);
                        pointEvents.add(blockDispense);
                        break;
                    case "block-dispense-armor":
                        BlockDispenseArmor blockDispenseArmor = new BlockDispenseArmor(quota, pointValue);
                        pluginManager.registerEvents(blockDispenseArmor, main);
                        varDeSerializer.deserializeCount(blockDispenseArmor);
                        pointEvents.add(blockDispenseArmor);
                        break;
                    case "block-drop-item":
                        BlockDropItem blockDropItem = new BlockDropItem(quota, pointValue);
                        pluginManager.registerEvents(blockDropItem, main);
                        varDeSerializer.deserializeCount(blockDropItem);
                        pointEvents.add(blockDropItem);
                        break;
                    case "block-exp":
                        BlockExp blockExp = new BlockExp(quota, pointValue);
                        pluginManager.registerEvents(blockExp, main);
                        varDeSerializer.deserializeCount(blockExp);
                        pointEvents.add(blockExp);
                        break;
                    case "block-explode":
                        BlockExplode blockExplode = new BlockExplode(quota, pointValue);
                        pluginManager.registerEvents(blockExplode, main);
                        varDeSerializer.deserializeCount(blockExplode);
                        pointEvents.add(blockExplode);
                        break;
                    case "block-fade":
                        BlockFade blockFade = new BlockFade(quota, pointValue);
                        pluginManager.registerEvents(blockFade, main);
                        varDeSerializer.deserializeCount(blockFade);
                        pointEvents.add(blockFade);
                        break;
                    case "block-fertilize":
                        BlockFertilize blockFertilize = new BlockFertilize(quota, pointValue);
                        pluginManager.registerEvents(blockFertilize, main);
                        varDeSerializer.deserializeCount(blockFertilize);
                        pointEvents.add(blockFertilize);
                        break;
                    case "block-from-to":
                        break;
                    case "block-grow":
                        break;
                    case "block-ignite":
                        break;
                    case "block-multi-place":
                        break;
                    case "block-physics":
                        break;
                    case "block-piston":
                        break;
                    case "block-place":
                        break;
                    case "block-receive-game":
                        break;
                    case "block-redstone":
                        break;
                    case "block-shear-entity":
                        break;
                    case "brew":
                        break;
                    case "brewing-stand-fuel":
                        break;
                    case "brewing-start":
                        break;
                    case "campfire-start":
                        break;
                    case "cauldron-level-change":
                        break;
                    case "fluid-level-change":
                        break;
                    case "furnace-burn":
                        break;
                    case "furnace-extract":
                        break;
                    case "furnace-smelt":
                        break;
                    case "furnace-start-smelt":
                        break;
                    case "hopper-inventory-search":
                        break;
                    case "leaves-decay":
                        break;
                    case "moisture-change":
                        break;
                    case "note-play":
                        break;
                    case "sculk-bloom":
                        break;
                    case "sign-change":
                        break;
                    case "sponge-absorb":
                        break;
                    case "tnt-prime":
                        break;
                }
            }
        }
    }
}
