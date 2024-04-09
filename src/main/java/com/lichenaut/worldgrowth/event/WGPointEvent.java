package com.lichenaut.worldgrowth.event;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public interface WGPointEvent<T extends Event> extends Listener {

    void onEvent(T event);

    int getCount();

    void setCount(int count);

    int getQuota();

    int getPointValue();
}
