package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.mygdx.hitboxcreator.App;

public abstract class HitShape extends Actor {
    InputListener inputListener;
    ShapeRenderer shapes;
    Color cBody;
    static Color cBodyNormal = new Color(1,0,0,0.5F);
    static Color cBodySelected = new Color(1,0.2F,0,0.5F);
    static Color cBorderNormal = new Color(0,0,0,0.5F);
    static Color cBorderSelected = new Color(0,0,1,1);
    boolean drawBorder;
    float grabArea;
    static float borderWidth = 2;
    int selection;

    void initShapeRenderer() {
        shapes = App.inst().getShapeRenderer();
    }

    static void setColors(Color normalBody, Color selectedBody, Color normalBorder, Color selectedBorder) {
        cBodyNormal = normalBody;
        cBodySelected = selectedBody;
        cBorderNormal = normalBorder;
        cBorderSelected = selectedBorder;
    }

    static void setBorderWidth(float width) {
        borderWidth = width;
    }

    // this is exactly like super.hit() only I define my own "Hitzone"
    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) return null;
        if (!isVisible()) return null;
        return contains(x, y) ? this : null;
    }

    boolean isFront() {
        Group parent = this.getParent();
        if (parent == null) return false;
        return parent.getChildren().size == getZIndex()+1;
    }

    abstract boolean contains(float x, float y);

    abstract void highlightBorder();

    abstract class Selection{}

    /**
     * exit() and touchDown() already defined.
     * Edit mouseMoved() and touchDragged()
     */
    class HitShapeInputListener extends InputListener {
        float oldX, oldY;

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            // workaround because exit gets also triggered when actor loses touchfocus
            // and that would cause to lose the highlighting when dragging ends
            if (isFront() && contains(x, y)) {
                mouseMoved(null, x, y);
            } else {
                // sets color back to normal when mouse exits
                selection = 0;
                highlightBorder();
            }
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            oldX = x;
            oldY = y;
            if (button == Input.Buttons.LEFT) {
                toFront();
                return true;
            }
            else return false;
        }
    }
}
