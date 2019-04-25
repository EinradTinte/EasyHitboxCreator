package com.einradtinte.hitboxcreator.events.events;

public abstract class Event {

    public boolean is(Class<? extends Event> eventClass) {
        return this.getClass().equals(eventClass);

    }

}
