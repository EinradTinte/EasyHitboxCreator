package com.mygdx.hitboxcreator.events;

import java.lang.reflect.Type;

public abstract class Event {

    public boolean is(Class<? extends Event> eventClass) {
        return this.getClass().equals(eventClass);

    }

}
