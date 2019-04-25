package com.einradtinte.hitboxcreator.events.events;

public class HitShapesChangedEvent extends Event {
    private final Action action;

    public HitShapesChangedEvent(Action action) {
        this.action = action;
    }

    public Action getAction() { return action; }

    public enum Action {
        FORM_CHANGED,
        QUANTITY_CHANGED
    }
}
