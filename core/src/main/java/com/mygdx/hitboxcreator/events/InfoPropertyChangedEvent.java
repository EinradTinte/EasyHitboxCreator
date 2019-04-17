package com.mygdx.hitboxcreator.events;

import com.badlogic.gdx.graphics.Texture;

public class InfoPropertyChangedEvent extends Event{

    private final Property property;
    private final Texture texture;
    private final int rectangleCount;
    private final int circleCount;

    public InfoPropertyChangedEvent(Property property, Texture texture, int rectangleCount, int circleCount) {
        this.property = property;
        this.texture = texture;
        this.rectangleCount = rectangleCount;
        this.circleCount = circleCount;
    }

    public Property getProperty() { return property; }

    public Texture getTexture() { return texture; }

    public int getRectangleCount() { return rectangleCount; }

    public int getCircleCount() { return circleCount; }

    public enum Property {
        IMG,
        RECTANGLES,
        CIRCLES
    }


}
