package com.lichenaut.worldgrowth.util;


import com.lichenaut.worldgrowth.Main;
import com.lichenaut.worldgrowth.db.WGDBManager;
import com.lichenaut.worldgrowth.event.WGPointEvent;
import com.lichenaut.worldgrowth.event.block.*;
import com.lichenaut.worldgrowth.event.entity.*;
import com.lichenaut.worldgrowth.event.player.*;
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
                        BlockFromTo blockFromTo = new BlockFromTo(quota, pointValue);
                        pluginManager.registerEvents(blockFromTo, main);
                        varDeSerializer.deserializeCount(blockFromTo);
                        pointEvents.add(blockFromTo);
                        break;
                    case "block-grow":
                        BlockGrow blockGrow = new BlockGrow(quota, pointValue);
                        pluginManager.registerEvents(blockGrow, main);
                        varDeSerializer.deserializeCount(blockGrow);
                        pointEvents.add(blockGrow);
                        break;
                    case "block-ignite":
                        BlockIgnite blockIgnite = new BlockIgnite(quota, pointValue);
                        pluginManager.registerEvents(blockIgnite, main);
                        varDeSerializer.deserializeCount(blockIgnite);
                        pointEvents.add(blockIgnite);
                        break;
                    case "block-multi-place":
                        BlockMultiPlace blockMultiPlace = new BlockMultiPlace(quota, pointValue);
                        pluginManager.registerEvents(blockMultiPlace, main);
                        varDeSerializer.deserializeCount(blockMultiPlace);
                        pointEvents.add(blockMultiPlace);
                        break;
                    case "block-physics":
                        BlockPhysics blockPhysics = new BlockPhysics(quota, pointValue);
                        pluginManager.registerEvents(blockPhysics, main);
                        varDeSerializer.deserializeCount(blockPhysics);
                        pointEvents.add(blockPhysics);
                        break;
                    case "block-piston":
                        BlockPiston blockPiston = new BlockPiston(quota, pointValue);
                        pluginManager.registerEvents(blockPiston, main);
                        varDeSerializer.deserializeCount(blockPiston);
                        pointEvents.add(blockPiston);
                        break;
                    case "block-place":
                        BlockPlace blockPlace = new BlockPlace(quota, pointValue);
                        pluginManager.registerEvents(blockPlace, main);
                        varDeSerializer.deserializeCount(blockPlace);
                        pointEvents.add(blockPlace);
                        break;
                    case "block-receive-game":
                        BlockReceiveGame blockReceiveGame = new BlockReceiveGame(quota, pointValue);
                        pluginManager.registerEvents(blockReceiveGame, main);
                        varDeSerializer.deserializeCount(blockReceiveGame);
                        pointEvents.add(blockReceiveGame);
                        break;
                    case "block-redstone":
                        BlockRedstone blockRedstone = new BlockRedstone(quota, pointValue);
                        pluginManager.registerEvents(blockRedstone, main);
                        varDeSerializer.deserializeCount(blockRedstone);
                        pointEvents.add(blockRedstone);
                        break;
                    case "block-shear-entity":
                        BlockShearEntity blockShearEntity = new BlockShearEntity(quota, pointValue);
                        pluginManager.registerEvents(blockShearEntity, main);
                        varDeSerializer.deserializeCount(blockShearEntity);
                        pointEvents.add(blockShearEntity);
                        break;
                    case "brew":
                        Brew brew = new Brew(quota, pointValue);
                        pluginManager.registerEvents(brew, main);
                        varDeSerializer.deserializeCount(brew);
                        pointEvents.add(brew);
                        break;
                    case "brewing-stand-fuel":
                        BrewingStandFuel brewingStandFuel = new BrewingStandFuel(quota, pointValue);
                        pluginManager.registerEvents(brewingStandFuel, main);
                        varDeSerializer.deserializeCount(brewingStandFuel);
                        pointEvents.add(brewingStandFuel);
                        break;
                    case "brewing-start":
                        BrewingStart brewingStart = new BrewingStart(quota, pointValue);
                        pluginManager.registerEvents(brewingStart, main);
                        varDeSerializer.deserializeCount(brewingStart);
                        pointEvents.add(brewingStart);
                        break;
                    case "campfire-start":
                        CampfireStart campfireStart = new CampfireStart(quota, pointValue);
                        pluginManager.registerEvents(campfireStart, main);
                        varDeSerializer.deserializeCount(campfireStart);
                        pointEvents.add(campfireStart);
                        break;
                    case "cauldron-level-change":
                        CauldronLevelChange cauldronLevelChange = new CauldronLevelChange(quota, pointValue);
                        pluginManager.registerEvents(cauldronLevelChange, main);
                        varDeSerializer.deserializeCount(cauldronLevelChange);
                        pointEvents.add(cauldronLevelChange);
                        break;
                    case "fluid-level-change":
                        FluidLevelChange fluidLevelChange = new FluidLevelChange(quota, pointValue);
                        pluginManager.registerEvents(fluidLevelChange, main);
                        varDeSerializer.deserializeCount(fluidLevelChange);
                        pointEvents.add(fluidLevelChange);
                        break;
                    case "furnace-burn":
                        FurnaceBurn furnaceBurn = new FurnaceBurn(quota, pointValue);
                        pluginManager.registerEvents(furnaceBurn, main);
                        varDeSerializer.deserializeCount(furnaceBurn);
                        pointEvents.add(furnaceBurn);
                        break;
                    case "furnace-extract":
                        FurnaceExtract furnaceExtract = new FurnaceExtract(quota, pointValue);
                        pluginManager.registerEvents(furnaceExtract, main);
                        varDeSerializer.deserializeCount(furnaceExtract);
                        pointEvents.add(furnaceExtract);
                        break;
                    case "furnace-smelt":
                        FurnaceSmelt furnaceSmelt = new FurnaceSmelt(quota, pointValue);
                        pluginManager.registerEvents(furnaceSmelt, main);
                        varDeSerializer.deserializeCount(furnaceSmelt);
                        pointEvents.add(furnaceSmelt);
                        break;
                    case "furnace-start-smelt":
                        FurnaceStartSmelt furnaceStartSmelt = new FurnaceStartSmelt(quota, pointValue);
                        pluginManager.registerEvents(furnaceStartSmelt, main);
                        varDeSerializer.deserializeCount(furnaceStartSmelt);
                        pointEvents.add(furnaceStartSmelt);
                        break;
                    case "hopper-inventory-search":
                        HopperInventorySearch hopperInventorySearch = new HopperInventorySearch(quota, pointValue);
                        pluginManager.registerEvents(hopperInventorySearch, main);
                        varDeSerializer.deserializeCount(hopperInventorySearch);
                        pointEvents.add(hopperInventorySearch);
                        break;
                    case "leaves-decay":
                        LeavesDecay leavesDecay = new LeavesDecay(quota, pointValue);
                        pluginManager.registerEvents(leavesDecay, main);
                        varDeSerializer.deserializeCount(leavesDecay);
                        pointEvents.add(leavesDecay);
                        break;
                    case "moisture-change":
                        MoistureChange moistureChange = new MoistureChange(quota, pointValue);
                        pluginManager.registerEvents(moistureChange, main);
                        varDeSerializer.deserializeCount(moistureChange);
                        pointEvents.add(moistureChange);
                        break;
                    case "note-play":
                        NotePlay notePlay = new NotePlay(quota, pointValue);
                        pluginManager.registerEvents(notePlay, main);
                        varDeSerializer.deserializeCount(notePlay);
                        pointEvents.add(notePlay);
                        break;
                    case "sculk-bloom":
                        SculkBloom sculkBloom = new SculkBloom(quota, pointValue);
                        pluginManager.registerEvents(sculkBloom, main);
                        varDeSerializer.deserializeCount(sculkBloom);
                        pointEvents.add(sculkBloom);
                        break;
                    case "sign-change":
                        SignChange signChange = new SignChange(quota, pointValue);
                        pluginManager.registerEvents(signChange, main);
                        varDeSerializer.deserializeCount(signChange);
                        pointEvents.add(signChange);
                        break;
                    case "sponge-absorb":
                        SpongeAbsorb spongeAbsorb = new SpongeAbsorb(quota, pointValue);
                        pluginManager.registerEvents(spongeAbsorb, main);
                        varDeSerializer.deserializeCount(spongeAbsorb);
                        pointEvents.add(spongeAbsorb);
                        break;
                    case "tnt-prime":
                        TNTPrime tntPrime = new TNTPrime(quota, pointValue);
                        pluginManager.registerEvents(tntPrime, main);
                        varDeSerializer.deserializeCount(tntPrime);
                        pointEvents.add(tntPrime);
                        break;
                    case "area-effect-cloud-apply":
                        AreaEffectCloudApply areaEffectCloudApply = new AreaEffectCloudApply(quota, pointValue);
                        pluginManager.registerEvents(areaEffectCloudApply, main);
                        varDeSerializer.deserializeCount(areaEffectCloudApply);
                        pointEvents.add(areaEffectCloudApply);
                        break;
                    case "arrow-body-count-change":
                        ArrowBodyCountChange arrowBodyCountChange = new ArrowBodyCountChange(quota, pointValue);
                        pluginManager.registerEvents(arrowBodyCountChange, main);
                        varDeSerializer.deserializeCount(arrowBodyCountChange);
                        pointEvents.add(arrowBodyCountChange);
                        break;
                    case "bat-toggle-sleep":
                        BatToggleSleep batToggleSleep = new BatToggleSleep(quota, pointValue);
                        pluginManager.registerEvents(batToggleSleep, main);
                        varDeSerializer.deserializeCount(batToggleSleep);
                        pointEvents.add(batToggleSleep);
                        break;
                    case "creeper-power":
                        CreeperPower creeperPower = new CreeperPower(quota, pointValue);
                        pluginManager.registerEvents(creeperPower, main);
                        varDeSerializer.deserializeCount(creeperPower);
                        pointEvents.add(creeperPower);
                        break;
                    case "ender-dragon-change-phase":
                        EnderDragonChangePhase enderDragonChangePhase = new EnderDragonChangePhase(quota, pointValue);
                        pluginManager.registerEvents(enderDragonChangePhase, main);
                        varDeSerializer.deserializeCount(enderDragonChangePhase);
                        pointEvents.add(enderDragonChangePhase);
                        break;
                    case "entity-air-change":
                        EntityAirChange entityAirChange = new EntityAirChange(quota, pointValue);
                        pluginManager.registerEvents(entityAirChange, main);
                        varDeSerializer.deserializeCount(entityAirChange);
                        pointEvents.add(entityAirChange);
                        break;
                    case "entity-breed":
                        EntityBreed entityBreed = new EntityBreed(quota, pointValue);
                        pluginManager.registerEvents(entityBreed, main);
                        varDeSerializer.deserializeCount(entityBreed);
                        pointEvents.add(entityBreed);
                        break;
                    case "entity-change-block":
                        EntityChangeBlock entityChangeBlock = new EntityChangeBlock(quota, pointValue);
                        pluginManager.registerEvents(entityChangeBlock, main);
                        varDeSerializer.deserializeCount(entityChangeBlock);
                        pointEvents.add(entityChangeBlock);
                        break;
                    case "entity-combust":
                        EntityCombust entityCombust = new EntityCombust(quota, pointValue);
                        pluginManager.registerEvents(entityCombust, main);
                        varDeSerializer.deserializeCount(entityCombust);
                        pointEvents.add(entityCombust);
                        break;
                    case "entity-combust-by-block":
                        EntityCombustByBlock entityCombustByBlock = new EntityCombustByBlock(quota, pointValue);
                        pluginManager.registerEvents(entityCombustByBlock, main);
                        varDeSerializer.deserializeCount(entityCombustByBlock);
                        pointEvents.add(entityCombustByBlock);
                        break;
                    case "entity-combust-by-entity":
                        EntityCombustByEntity entityCombustByEntity = new EntityCombustByEntity(quota, pointValue);
                        pluginManager.registerEvents(entityCombustByEntity, main);
                        varDeSerializer.deserializeCount(entityCombustByEntity);
                        pointEvents.add(entityCombustByEntity);
                        break;
                    case "entity-damage":
                        EntityDamage entityDamage = new EntityDamage(quota, pointValue);
                        pluginManager.registerEvents(entityDamage, main);
                        varDeSerializer.deserializeCount(entityDamage);
                        pointEvents.add(entityDamage);
                        break;
                    case "entity-damage-by-block":
                        EntityDamageByBlock entityDamageByBlock = new EntityDamageByBlock(quota, pointValue);
                        pluginManager.registerEvents(entityDamageByBlock, main);
                        varDeSerializer.deserializeCount(entityDamageByBlock);
                        pointEvents.add(entityDamageByBlock);
                        break;
                    case "entity-damage-by-entity":
                        EntityDamageByEntity entityDamageByEntity = new EntityDamageByEntity(quota, pointValue);
                        pluginManager.registerEvents(entityDamageByEntity, main);
                        varDeSerializer.deserializeCount(entityDamageByEntity);
                        pointEvents.add(entityDamageByEntity);
                        break;
                    case "entity-death":
                        EntityDeath entityDeath = new EntityDeath(quota, pointValue);
                        pluginManager.registerEvents(entityDeath, main);
                        varDeSerializer.deserializeCount(entityDeath);
                        pointEvents.add(entityDeath);
                        break;
                    case "entity-dismount":
                        EntityDismount entityDismount = new EntityDismount(quota, pointValue);
                        pluginManager.registerEvents(entityDismount, main);
                        varDeSerializer.deserializeCount(entityDismount);
                        pointEvents.add(entityDismount);
                        break;
                    case "entity-drop-item":
                        EntityDropItem entityDropItem = new EntityDropItem(quota, pointValue);
                        pluginManager.registerEvents(entityDropItem, main);
                        varDeSerializer.deserializeCount(entityDropItem);
                        pointEvents.add(entityDropItem);
                        break;
                    case "entity-enter-block":
                        EntityEnterBlock entityEnterBlock = new EntityEnterBlock(quota, pointValue);
                        pluginManager.registerEvents(entityEnterBlock, main);
                        varDeSerializer.deserializeCount(entityEnterBlock);
                        pointEvents.add(entityEnterBlock);
                        break;
                    case "entity-enter-love-mode":
                        EntityEnterLoveMode entityEnterLoveMode = new EntityEnterLoveMode(quota, pointValue);
                        pluginManager.registerEvents(entityEnterLoveMode, main);
                        varDeSerializer.deserializeCount(entityEnterLoveMode);
                        pointEvents.add(entityEnterLoveMode);
                        break;
                    case "entity-exhaustion":
                        EntityExhaustion entityExhaustion = new EntityExhaustion(quota, pointValue);
                        pluginManager.registerEvents(entityExhaustion, main);
                        varDeSerializer.deserializeCount(entityExhaustion);
                        pointEvents.add(entityExhaustion);
                        break;
                    case "entity-explode":
                        EntityExplode entityExplode = new EntityExplode(quota, pointValue);
                        pluginManager.registerEvents(entityExplode, main);
                        varDeSerializer.deserializeCount(entityExplode);
                        pointEvents.add(entityExplode);
                        break;
                    case "entity-interact":
                        EntityInteract entityInteract = new EntityInteract(quota, pointValue);
                        pluginManager.registerEvents(entityInteract, main);
                        varDeSerializer.deserializeCount(entityInteract);
                        pointEvents.add(entityInteract);
                        break;
                    case "entity-knockback":
                        EntityKnockback entityKnockback = new EntityKnockback(quota, pointValue);
                        pluginManager.registerEvents(entityKnockback, main);
                        varDeSerializer.deserializeCount(entityKnockback);
                        pointEvents.add(entityKnockback);
                        break;
                    case "entity-mount":
                        EntityMount entityMount = new EntityMount(quota, pointValue);
                        pluginManager.registerEvents(entityMount, main);
                        varDeSerializer.deserializeCount(entityMount);
                        pointEvents.add(entityMount);
                        break;
                    case "entity-pickup-item":
                        EntityPickupItem entityPickupItem = new EntityPickupItem(quota, pointValue);
                        pluginManager.registerEvents(entityPickupItem, main);
                        varDeSerializer.deserializeCount(entityPickupItem);
                        pointEvents.add(entityPickupItem);
                        break;
                    case "entity-place":
                        EntityPlace entityPlace = new EntityPlace(quota, pointValue);
                        pluginManager.registerEvents(entityPlace, main);
                        varDeSerializer.deserializeCount(entityPlace);
                        pointEvents.add(entityPlace);
                        break;
                    case "entity-portal-enter":
                        EntityPortalEnter entityPortalEnter = new EntityPortalEnter(quota, pointValue);
                        pluginManager.registerEvents(entityPortalEnter, main);
                        varDeSerializer.deserializeCount(entityPortalEnter);
                        pointEvents.add(entityPortalEnter);
                        break;
                    case "entity-pose-change":
                        EntityPoseChange entityPoseChange = new EntityPoseChange(quota, pointValue);
                        pluginManager.registerEvents(entityPoseChange, main);
                        varDeSerializer.deserializeCount(entityPoseChange);
                        pointEvents.add(entityPoseChange);
                        break;
                    case "entity-potion-effect":
                        EntityPotionEffect entityPotionEffect = new EntityPotionEffect(quota, pointValue);
                        pluginManager.registerEvents(entityPotionEffect, main);
                        varDeSerializer.deserializeCount(entityPotionEffect);
                        pointEvents.add(entityPotionEffect);
                        break;
                    case "entity-regain-health":
                        EntityRegainHealth entityRegainHealth = new EntityRegainHealth(quota, pointValue);
                        pluginManager.registerEvents(entityRegainHealth, main);
                        varDeSerializer.deserializeCount(entityRegainHealth);
                        pointEvents.add(entityRegainHealth);
                        break;
                    case "entity-resurrect":
                        EntityResurrect entityResurrect = new EntityResurrect(quota, pointValue);
                        pluginManager.registerEvents(entityResurrect, main);
                        varDeSerializer.deserializeCount(entityResurrect);
                        pointEvents.add(entityResurrect);
                        break;
                    case "entity-shoot-bow":
                        EntityShootBow entityShootBow = new EntityShootBow(quota, pointValue);
                        pluginManager.registerEvents(entityShootBow, main);
                        varDeSerializer.deserializeCount(entityShootBow);
                        pointEvents.add(entityShootBow);
                        break;
                    case "entity-spawn":
                        EntitySpawn entitySpawn = new EntitySpawn(quota, pointValue);
                        pluginManager.registerEvents(entitySpawn, main);
                        varDeSerializer.deserializeCount(entitySpawn);
                        pointEvents.add(entitySpawn);
                        break;
                    case "entity-spell-cast":
                        EntitySpellCast entitySpellCast = new EntitySpellCast(quota, pointValue);
                        pluginManager.registerEvents(entitySpellCast, main);
                        varDeSerializer.deserializeCount(entitySpellCast);
                        pointEvents.add(entitySpellCast);
                        break;
                    case "entity-tame":
                        EntityTame entityTame = new EntityTame(quota, pointValue);
                        pluginManager.registerEvents(entityTame, main);
                        varDeSerializer.deserializeCount(entityTame);
                        pointEvents.add(entityTame);
                        break;
                    case "entity-target":
                        EntityTarget entityTarget = new EntityTarget(quota, pointValue);
                        pluginManager.registerEvents(entityTarget, main);
                        varDeSerializer.deserializeCount(entityTarget);
                        pointEvents.add(entityTarget);
                        break;
                    case "entity-teleport":
                        EntityTeleport entityTeleport = new EntityTeleport(quota, pointValue);
                        pluginManager.registerEvents(entityTeleport, main);
                        varDeSerializer.deserializeCount(entityTeleport);
                        pointEvents.add(entityTeleport);
                        break;
                    case "entity-toggle-glide":
                        EntityToggleGlide entityToggleGlide = new EntityToggleGlide(quota, pointValue);
                        pluginManager.registerEvents(entityToggleGlide, main);
                        varDeSerializer.deserializeCount(entityToggleGlide);
                        pointEvents.add(entityToggleGlide);
                        break;
                    case "entity-toggle-swim":
                        EntityToggleSwim entityToggleSwim = new EntityToggleSwim(quota, pointValue);
                        pluginManager.registerEvents(entityToggleSwim, main);
                        varDeSerializer.deserializeCount(entityToggleSwim);
                        pointEvents.add(entityToggleSwim);
                        break;
                    case "entity-transform":
                        EntityTransform entityTransform = new EntityTransform(quota, pointValue);
                        pluginManager.registerEvents(entityTransform, main);
                        varDeSerializer.deserializeCount(entityTransform);
                        pointEvents.add(entityTransform);
                        break;
                    case "entity-unleash":
                        EntityUnleash entityUnleash = new EntityUnleash(quota, pointValue);
                        pluginManager.registerEvents(entityUnleash, main);
                        varDeSerializer.deserializeCount(entityUnleash);
                        pointEvents.add(entityUnleash);
                        break;
                    case "exp-bottle":
                        ExpBottle expBottle = new ExpBottle(quota, pointValue);
                        pluginManager.registerEvents(expBottle, main);
                        varDeSerializer.deserializeCount(expBottle);
                        pointEvents.add(expBottle);
                        break;
                    case "explosion-prime":
                        ExplosionPrime explosionPrime = new ExplosionPrime(quota, pointValue);
                        pluginManager.registerEvents(explosionPrime, main);
                        varDeSerializer.deserializeCount(explosionPrime);
                        pointEvents.add(explosionPrime);
                        break;
                    case "firework-explode":
                        FireworkExplode fireworkExplode = new FireworkExplode(quota, pointValue);
                        pluginManager.registerEvents(fireworkExplode, main);
                        varDeSerializer.deserializeCount(fireworkExplode);
                        pointEvents.add(fireworkExplode);
                        break;
                    case "food-level-change":
                        FoodLevelChange foodLevelChange = new FoodLevelChange(quota, pointValue);
                        pluginManager.registerEvents(foodLevelChange, main);
                        varDeSerializer.deserializeCount(foodLevelChange);
                        pointEvents.add(foodLevelChange);
                        break;
                    case "horse-jump":
                        HorseJump horseJump = new HorseJump(quota, pointValue);
                        pluginManager.registerEvents(horseJump, main);
                        varDeSerializer.deserializeCount(horseJump);
                        pointEvents.add(horseJump);
                        break;
                    case "item-despawn":
                        ItemDespawn itemDespawn = new ItemDespawn(quota, pointValue);
                        pluginManager.registerEvents(itemDespawn, main);
                        varDeSerializer.deserializeCount(itemDespawn);
                        pointEvents.add(itemDespawn);
                        break;
                    case "item-merge":
                        ItemMerge itemMerge = new ItemMerge(quota, pointValue);
                        pluginManager.registerEvents(itemMerge, main);
                        varDeSerializer.deserializeCount(itemMerge);
                        pointEvents.add(itemMerge);
                        break;
                    case "lingering-potion-splash":
                        LingeringPotionSplash lingeringPotionSplash = new LingeringPotionSplash(quota, pointValue);
                        pluginManager.registerEvents(lingeringPotionSplash, main);
                        varDeSerializer.deserializeCount(lingeringPotionSplash);
                        pointEvents.add(lingeringPotionSplash);
                        break;
                    case "piglin-barter":
                        PiglinBarter piglinBarter = new PiglinBarter(quota, pointValue);
                        pluginManager.registerEvents(piglinBarter, main);
                        varDeSerializer.deserializeCount(piglinBarter);
                        pointEvents.add(piglinBarter);
                        break;
                    case "pig-zap":
                        PigZap pigZap = new PigZap(quota, pointValue);
                        pluginManager.registerEvents(pigZap, main);
                        varDeSerializer.deserializeCount(pigZap);
                        pointEvents.add(pigZap);
                        break;
                    case "pig-zombie-anger":
                        PigZombieAnger pigZombieAnger = new PigZombieAnger(quota, pointValue);
                        pluginManager.registerEvents(pigZombieAnger, main);
                        varDeSerializer.deserializeCount(pigZombieAnger);
                        pointEvents.add(pigZombieAnger);
                        break;
                    case "player-death":
                        PlayerDeath playerDeath = new PlayerDeath(quota, pointValue);
                        pluginManager.registerEvents(playerDeath, main);
                        varDeSerializer.deserializeCount(playerDeath);
                        pointEvents.add(playerDeath);
                        break;
                    case "potion-splash":
                        PotionSplash potionSplash = new PotionSplash(quota, pointValue);
                        pluginManager.registerEvents(potionSplash, main);
                        varDeSerializer.deserializeCount(potionSplash);
                        pointEvents.add(potionSplash);
                        break;
                    case "projectile-hit":
                        ProjectileHit projectileHit = new ProjectileHit(quota, pointValue);
                        pluginManager.registerEvents(projectileHit, main);
                        varDeSerializer.deserializeCount(projectileHit);
                        pointEvents.add(projectileHit);
                        break;
                    case "sheep-dye-wool":
                        SheepDyeWool sheepDyeWool = new SheepDyeWool(quota, pointValue);
                        pluginManager.registerEvents(sheepDyeWool, main);
                        varDeSerializer.deserializeCount(sheepDyeWool);
                        pointEvents.add(sheepDyeWool);
                        break;
                    case "sheep-regrow-wool":
                        SheepRegrowWool sheepRegrowWool = new SheepRegrowWool(quota, pointValue);
                        pluginManager.registerEvents(sheepRegrowWool, main);
                        varDeSerializer.deserializeCount(sheepRegrowWool);
                        pointEvents.add(sheepRegrowWool);
                        break;
                    case "slime-split":
                        SlimeSplit slimeSplit = new SlimeSplit(quota, pointValue);
                        pluginManager.registerEvents(slimeSplit, main);
                        varDeSerializer.deserializeCount(slimeSplit);
                        pointEvents.add(slimeSplit);
                        break;
                    case "strider-temperature-change":
                        StriderTemperatureChange striderTemperatureChange = new StriderTemperatureChange(quota, pointValue);
                        pluginManager.registerEvents(striderTemperatureChange, main);
                        varDeSerializer.deserializeCount(striderTemperatureChange);
                        pointEvents.add(striderTemperatureChange);
                        break;
                    case "villager-acquire-trade":
                        VillagerAcquireTrade villagerAcquireTrade = new VillagerAcquireTrade(quota, pointValue);
                        pluginManager.registerEvents(villagerAcquireTrade, main);
                        varDeSerializer.deserializeCount(villagerAcquireTrade);
                        pointEvents.add(villagerAcquireTrade);
                        break;
                    case "villager-career-change":
                        VillagerCareerChange villagerCareerChange = new VillagerCareerChange(quota, pointValue);
                        pluginManager.registerEvents(villagerCareerChange, main);
                        varDeSerializer.deserializeCount(villagerCareerChange);
                        pointEvents.add(villagerCareerChange);
                        break;
                    case "villager-replenish-trade":
                        VillagerReplenishTrade villagerReplenishTrade = new VillagerReplenishTrade(quota, pointValue);
                        pluginManager.registerEvents(villagerReplenishTrade, main);
                        varDeSerializer.deserializeCount(villagerReplenishTrade);
                        pointEvents.add(villagerReplenishTrade);
                        break;
                    case "async-player-chat":
                        AsyncPlayerChat asyncPlayerChat = new AsyncPlayerChat(quota, pointValue);
                        pluginManager.registerEvents(asyncPlayerChat, main);
                        varDeSerializer.deserializeCount(asyncPlayerChat);
                        pointEvents.add(asyncPlayerChat);
                        break;
                    case "player-advancement-done":
                        PlayerAdvancementDone playerAdvancementDone = new PlayerAdvancementDone(quota, pointValue);
                        pluginManager.registerEvents(playerAdvancementDone, main);
                        varDeSerializer.deserializeCount(playerAdvancementDone);
                        pointEvents.add(playerAdvancementDone);
                        break;
                    case "player-animation":
                        PlayerAnimation playerAnimation = new PlayerAnimation(quota, pointValue);
                        pluginManager.registerEvents(playerAnimation, main);
                        varDeSerializer.deserializeCount(playerAnimation);
                        pointEvents.add(playerAnimation);
                        break;
                    case "player-bed-enter":
                        PlayerBedEnter playerBedEnter = new PlayerBedEnter(quota, pointValue);
                        pluginManager.registerEvents(playerBedEnter, main);
                        varDeSerializer.deserializeCount(playerBedEnter);
                        pointEvents.add(playerBedEnter);
                        break;
                    case "player-bed-leave":
                        PlayerBedLeave playerBedLeave = new PlayerBedLeave(quota, pointValue);
                        pluginManager.registerEvents(playerBedLeave, main);
                        varDeSerializer.deserializeCount(playerBedLeave);
                        pointEvents.add(playerBedLeave);
                        break;
                    case "player-bucket":
                        PlayerBucket playerBucket = new PlayerBucket(quota, pointValue);
                        pluginManager.registerEvents(playerBucket, main);
                        varDeSerializer.deserializeCount(playerBucket);
                        pointEvents.add(playerBucket);
                        break;
                    case "player-bucket-entity":
                        PlayerBucketEntity playerBucketEntity = new PlayerBucketEntity(quota, pointValue);
                        pluginManager.registerEvents(playerBucketEntity, main);
                        varDeSerializer.deserializeCount(playerBucketEntity);
                        pointEvents.add(playerBucketEntity);
                        break;
                    case "player-changed-main-hand":
                        PlayerChangedMainHand playerChangedMainHand = new PlayerChangedMainHand(quota, pointValue);
                        pluginManager.registerEvents(playerChangedMainHand, main);
                        varDeSerializer.deserializeCount(playerChangedMainHand);
                        pointEvents.add(playerChangedMainHand);
                        break;
                    case "player-changed-world":
                        PlayerChangedWorld playerChangedWorld = new PlayerChangedWorld(quota, pointValue);
                        pluginManager.registerEvents(playerChangedWorld, main);
                        varDeSerializer.deserializeCount(playerChangedWorld);
                        pointEvents.add(playerChangedWorld);
                        break;
                    case "player-channel":
                        PlayerChannel playerChannel = new PlayerChannel(quota, pointValue);
                        pluginManager.registerEvents(playerChannel, main);
                        varDeSerializer.deserializeCount(playerChannel);
                        pointEvents.add(playerChannel);
                        break;
                    case "player-command-send":
                        PlayerCommandSend playerCommandSend = new PlayerCommandSend(quota, pointValue);
                        pluginManager.registerEvents(playerCommandSend, main);
                        varDeSerializer.deserializeCount(playerCommandSend);
                        pointEvents.add(playerCommandSend);
                        break;
                    case "player-drop-item":
                        PlayerDropItem playerDropItem = new PlayerDropItem(quota, pointValue);
                        pluginManager.registerEvents(playerDropItem, main);
                        varDeSerializer.deserializeCount(playerDropItem);
                        pointEvents.add(playerDropItem);
                        break;
                    case "player-edit-book":
                        PlayerEditBook playerEditBook = new PlayerEditBook(quota, pointValue);
                        pluginManager.registerEvents(playerEditBook, main);
                        varDeSerializer.deserializeCount(playerEditBook);
                        pointEvents.add(playerEditBook);
                        break;
                    case "player-egg-throw":
                        PlayerEggThrow playerEggThrow = new PlayerEggThrow(quota, pointValue);
                        pluginManager.registerEvents(playerEggThrow, main);
                        varDeSerializer.deserializeCount(playerEggThrow);
                        pointEvents.add(playerEggThrow);
                        break;
                    case "player-exp-change":
                        PlayerExpChange playerExpChange = new PlayerExpChange(quota, pointValue);
                        pluginManager.registerEvents(playerExpChange, main);
                        varDeSerializer.deserializeCount(playerExpChange);
                        pointEvents.add(playerExpChange);
                        break;
                    case "player-fish":
                        PlayerFish playerFish = new PlayerFish(quota, pointValue);
                        pluginManager.registerEvents(playerFish, main);
                        varDeSerializer.deserializeCount(playerFish);
                        pointEvents.add(playerFish);
                        break;
                    case "player-game-mode-change":
                        PlayerGameModeChange playerGameModeChange = new PlayerGameModeChange(quota, pointValue);
                        pluginManager.registerEvents(playerGameModeChange, main);
                        varDeSerializer.deserializeCount(playerGameModeChange);
                        pointEvents.add(playerGameModeChange);
                        break;
                    case "player-harvest-block":
                        PlayerHarvestBlock playerHarvestBlock = new PlayerHarvestBlock(quota, pointValue);
                        pluginManager.registerEvents(playerHarvestBlock, main);
                        varDeSerializer.deserializeCount(playerHarvestBlock);
                        pointEvents.add(playerHarvestBlock);
                        break;
                    case "player-hide-entity":
                        PlayerHideEntity playerHideEntity = new PlayerHideEntity(quota, pointValue);
                        pluginManager.registerEvents(playerHideEntity, main);
                        varDeSerializer.deserializeCount(playerHideEntity);
                        pointEvents.add(playerHideEntity);
                        break;
                    case "player-interact":
                        PlayerInteract playerInteract = new PlayerInteract(quota, pointValue);
                        pluginManager.registerEvents(playerInteract, main);
                        varDeSerializer.deserializeCount(playerInteract);
                        pointEvents.add(playerInteract);
                        break;
                    case "player-interact-entity":
                        PlayerInteractEntity playerInteractEntity = new PlayerInteractEntity(quota, pointValue);
                        pluginManager.registerEvents(playerInteractEntity, main);
                        varDeSerializer.deserializeCount(playerInteractEntity);
                        pointEvents.add(playerInteractEntity);
                        break;
                    case "player-item-break":
                        PlayerItemBreak playerItemBreak = new PlayerItemBreak(quota, pointValue);
                        pluginManager.registerEvents(playerItemBreak, main);
                        varDeSerializer.deserializeCount(playerItemBreak);
                        pointEvents.add(playerItemBreak);
                        break;
                    case "player-item-consume":
                        PlayerItemConsume playerItemConsume = new PlayerItemConsume(quota, pointValue);
                        pluginManager.registerEvents(playerItemConsume, main);
                        varDeSerializer.deserializeCount(playerItemConsume);
                        pointEvents.add(playerItemConsume);
                        break;
                    case "player-item-damage":
                        PlayerItemDamage playerItemDamage = new PlayerItemDamage(quota, pointValue);
                        pluginManager.registerEvents(playerItemDamage, main);
                        varDeSerializer.deserializeCount(playerItemDamage);
                        pointEvents.add(playerItemDamage);
                        break;
                    case "player-item-held":
                        PlayerItemHeld playerItemHeld = new PlayerItemHeld(quota, pointValue);
                        pluginManager.registerEvents(playerItemHeld, main);
                        varDeSerializer.deserializeCount(playerItemHeld);
                        pointEvents.add(playerItemHeld);
                        break;
                    case "player-item-mend":
                        PlayerItemMend playerItemMend = new PlayerItemMend(quota, pointValue);
                        pluginManager.registerEvents(playerItemMend, main);
                        varDeSerializer.deserializeCount(playerItemMend);
                        pointEvents.add(playerItemMend);
                        break;
                    case "player-join":
                        PlayerJoin playerJoin = new PlayerJoin(quota, pointValue);
                        pluginManager.registerEvents(playerJoin, main);
                        varDeSerializer.deserializeCount(playerJoin);
                        pointEvents.add(playerJoin);
                        break;
                    case "player-kick":
                        PlayerKick playerKick = new PlayerKick(quota, pointValue);
                        pluginManager.registerEvents(playerKick, main);
                        varDeSerializer.deserializeCount(playerKick);
                        pointEvents.add(playerKick);
                        break;
                    case "player-level-change":
                        PlayerLevelChange playerLevelChange = new PlayerLevelChange(quota, pointValue);
                        pluginManager.registerEvents(playerLevelChange, main);
                        varDeSerializer.deserializeCount(playerLevelChange);
                        pointEvents.add(playerLevelChange);
                        break;
                    case "player-locale-change":
                        PlayerLocaleChange playerLocaleChange = new PlayerLocaleChange(quota, pointValue);
                        pluginManager.registerEvents(playerLocaleChange, main);
                        varDeSerializer.deserializeCount(playerLocaleChange);
                        pointEvents.add(playerLocaleChange);
                        break;
                    case "player-login":
                        PlayerLogin playerLogin = new PlayerLogin(quota, pointValue);
                        pluginManager.registerEvents(playerLogin, main);
                        varDeSerializer.deserializeCount(playerLogin);
                        pointEvents.add(playerLogin);
                        break;
                    case "player-move":
                        PlayerMove playerMove = new PlayerMove(quota, pointValue);
                        pluginManager.registerEvents(playerMove, main);
                        varDeSerializer.deserializeCount(playerMove);
                        pointEvents.add(playerMove);
                        break;
                    case "player-quit":
                        PlayerQuit playerQuit = new PlayerQuit(quota, pointValue);
                        pluginManager.registerEvents(playerQuit, main);
                        varDeSerializer.deserializeCount(playerQuit);
                        pointEvents.add(playerQuit);
                        break;
                    case "player-recipe-book-click":
                        PlayerRecipeBookClick playerRecipeBookClick = new PlayerRecipeBookClick(quota, pointValue);
                        pluginManager.registerEvents(playerRecipeBookClick, main);
                        varDeSerializer.deserializeCount(playerRecipeBookClick);
                        pointEvents.add(playerRecipeBookClick);
                        break;
                    case "player-recipe-book-settings-change":
                        PlayerRecipeBookSettingsChange playerRecipeBookSettingsChange = new PlayerRecipeBookSettingsChange(quota, pointValue);
                        pluginManager.registerEvents(playerRecipeBookSettingsChange, main);
                        varDeSerializer.deserializeCount(playerRecipeBookSettingsChange);
                        pointEvents.add(playerRecipeBookSettingsChange);
                        break;
                    case "player-recipe-discover":
                        PlayerRecipeDiscover playerRecipeDiscover = new PlayerRecipeDiscover(quota, pointValue);
                        pluginManager.registerEvents(playerRecipeDiscover, main);
                        varDeSerializer.deserializeCount(playerRecipeDiscover);
                        pointEvents.add(playerRecipeDiscover);
                        break;
                    case "player-resource-pack-status":
                        PlayerResourcePackStatus playerResourcePackStatus = new PlayerResourcePackStatus(quota, pointValue);
                        pluginManager.registerEvents(playerResourcePackStatus, main);
                        varDeSerializer.deserializeCount(playerResourcePackStatus);
                        pointEvents.add(playerResourcePackStatus);
                        break;
                    case "player-respawn":
                        PlayerRespawn playerRespawn = new PlayerRespawn(quota, pointValue);
                        pluginManager.registerEvents(playerRespawn, main);
                        varDeSerializer.deserializeCount(playerRespawn);
                        pointEvents.add(playerRespawn);
                        break;
                    case "player-riptide":
                        PlayerRiptide playerRiptide = new PlayerRiptide(quota, pointValue);
                        pluginManager.registerEvents(playerRiptide, main);
                        varDeSerializer.deserializeCount(playerRiptide);
                        pointEvents.add(playerRiptide);
                        break;
                    case "player-shear-entity":
                        PlayerShearEntity playerShearEntity = new PlayerShearEntity(quota, pointValue);
                        pluginManager.registerEvents(playerShearEntity, main);
                        varDeSerializer.deserializeCount(playerShearEntity);
                        pointEvents.add(playerShearEntity);
                        break;
                    case "player-show-entity":
                        PlayerShowEntity playerShowEntity = new PlayerShowEntity(quota, pointValue);
                        pluginManager.registerEvents(playerShowEntity, main);
                        varDeSerializer.deserializeCount(playerShowEntity);
                        pointEvents.add(playerShowEntity);
                        break;
                    case "player-spawn-location":
                        PlayerSpawnLocation playerSpawnLocation = new PlayerSpawnLocation(quota, pointValue);
                        pluginManager.registerEvents(playerSpawnLocation, main);
                        varDeSerializer.deserializeCount(playerSpawnLocation);
                        pointEvents.add(playerSpawnLocation);
                        break;
                    case "player-statistic-increment":
                        PlayerStatisticIncrement playerStatisticIncrement = new PlayerStatisticIncrement(quota, pointValue);
                        pluginManager.registerEvents(playerStatisticIncrement, main);
                        varDeSerializer.deserializeCount(playerStatisticIncrement);
                        pointEvents.add(playerStatisticIncrement);
                        break;
                    case "player-swap-hand-items":
                        PlayerSwapHandItems playerSwapHandItems = new PlayerSwapHandItems(quota, pointValue);
                        pluginManager.registerEvents(playerSwapHandItems, main);
                        varDeSerializer.deserializeCount(playerSwapHandItems);
                        pointEvents.add(playerSwapHandItems);
                        break;
                    case "player-take-lectern-book":
                        PlayerTakeLecternBook playerTakeLecternBook = new PlayerTakeLecternBook(quota, pointValue);
                        pluginManager.registerEvents(playerTakeLecternBook, main);
                        varDeSerializer.deserializeCount(playerTakeLecternBook);
                        pointEvents.add(playerTakeLecternBook);
                        break;
                    case "player-teleport":
                        PlayerTeleport playerTeleport = new PlayerTeleport(quota, pointValue);
                        pluginManager.registerEvents(playerTeleport, main);
                        varDeSerializer.deserializeCount(playerTeleport);
                        pointEvents.add(playerTeleport);
                        break;
                    case "player-toggle-flight":
                        PlayerToggleFlight playerToggleFlight = new PlayerToggleFlight(quota, pointValue);
                        pluginManager.registerEvents(playerToggleFlight, main);
                        varDeSerializer.deserializeCount(playerToggleFlight);
                        pointEvents.add(playerToggleFlight);
                        break;
                    case "player-toggle-sneak":
                        PlayerToggleSneak playerToggleSneak = new PlayerToggleSneak(quota, pointValue);
                        pluginManager.registerEvents(playerToggleSneak, main);
                        varDeSerializer.deserializeCount(playerToggleSneak);
                        pointEvents.add(playerToggleSneak);
                        break;
                    case "player-toggle-sprint":
                        PlayerToggleSprint playerToggleSprint = new PlayerToggleSprint(quota, pointValue);
                        pluginManager.registerEvents(playerToggleSprint, main);
                        varDeSerializer.deserializeCount(playerToggleSprint);
                        pointEvents.add(playerToggleSprint);
                        break;
                    case "player-velocity":
                        PlayerVelocity playerVelocity = new PlayerVelocity(quota, pointValue);
                        pluginManager.registerEvents(playerVelocity, main);
                        varDeSerializer.deserializeCount(playerVelocity);
                        pointEvents.add(playerVelocity);
                        break;
                }
            }
        }
    }
}
