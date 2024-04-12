package com.lichenaut.worldgrowth.world;

import org.bukkit.Location;

public record WGWorld(boolean isMain, String name, Location borderCenter, int startSize, int maxSize, int growthMultiplier) {}
