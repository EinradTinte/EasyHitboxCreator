package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.FlushablePool;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.views.Shader;

public abstract class HitShape extends Actor {
    InputListener inputListener;
    Shader shader = App.inst().getShader();
    Color cBody;
    static Color cBodyNormal = new Color(1,0,0,0.5F);
    static Color cBodySelected = new Color(1,0.2F,0,0.5F);
    static Color cBorderNormal = new Color(0,0,0,0.5F);
    static Color cBorderSelected = new Color(0,0,1,1);
    boolean drawBorder;
    float grabArea;
    static float borderWidth = 2;
    int selection;







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
        final Vector2 lastPos = new Vector2();

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
            if (button != Input.Buttons.LEFT) return false;

            toFront();
            lastPos.set(x, y);
            return true;
        }
    }


    /** Loads a  triangle to the shaders mesh which represents the HitShapes */
    void drawTriangle(Vector2 corner00, Vector2 corner01, Vector2 corner11, Color color) {
        float colorBits = color.toFloatBits();

        shader.vertex(corner00.x, corner00.y, colorBits);
        shader.vertex(corner01.x, corner01.y, colorBits);
        shader.vertex(corner11.x, corner11.y, colorBits);
    }


    private final static FlushablePool<Vector2> vectorPool = new FlushablePool<Vector2>() {
        @Override
        protected Vector2 newObject () {
            return new Vector2();
        }
    };

    /** Obtain a temporary {@link Vector2} object, must be free'd using {@link #freeAll()}. */
    protected static Vector2 obtainV2 () {
        return vectorPool.obtain();
    }

    /** Free all objects obtained using one of the `obtainXX` methods. */
    protected static void freeAll () {
        vectorPool.flush();
    }

}
