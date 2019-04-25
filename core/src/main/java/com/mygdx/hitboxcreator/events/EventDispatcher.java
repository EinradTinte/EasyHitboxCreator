package com.mygdx.hitboxcreator.events;

import com.badlogic.gdx.utils.SnapshotArray;
import com.mygdx.hitboxcreator.events.events.Event;

public class EventDispatcher {
    private SnapshotArray<EventListener> listeners;

    public EventDispatcher() {
        listeners = new SnapshotArray<EventListener>();
    }

    public void postEvent(Event event) {
        for (int i = 0; i < listeners.size; i++) {
            listeners.get(i).receiveEvent(event);
        }
    }

    public void addEventListener(EventListener eventListener) {
        listeners.add(eventListener);
    }

    public void removeEventListener(EventListener eventListener) {
        listeners.removeValue(eventListener, true);
    }

    public void removeAllEventListeners() {
        listeners.clear();
    }
}
