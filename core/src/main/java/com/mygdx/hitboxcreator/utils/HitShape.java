package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygdx.hitboxcreator.App;

public abstract class HitShape extends Actor {
    InputListener inputListener;
    ShapeRenderer shapes;
    Color cBody;
    Color cNormal, cHover, cSelected;
    boolean drawBorder, isSelected;
    float grabArea, lineWidth;
    int selection;

    void initShapeRenderer() {
        shapes = App.inst().getShapeRenderer();
    }

    abstract void setColors();

    abstract void drawShape();

    abstract void highlightBorder();

    abstract class Selection{}
}
