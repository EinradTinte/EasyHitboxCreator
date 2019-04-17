package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.mygdx.hitboxcreator.events.EventDispatcher;
import com.mygdx.hitboxcreator.events.HitShapesChangedEvent;
import com.mygdx.hitboxcreator.views.Shader;

public abstract class HitShape extends Actor {


    Color cBody;
    static Color cBodyNormal = new Color(1,0,0,0.5F);
    static Color cBodySelected = new Color(1,0.2F,0,0.5F);
    static Color cBorderNormal = new Color(0,0,0,0.5F);
    static Color cBorderSelected = new Color(0,0,1,1);
    boolean drawBorder;
    float grabArea;
    static float borderWidth = 2;
    int selection;

    TextureRegion region;
    EventDispatcher eventDispatcher = App.inst().getEventDispatcher();

    static final int NUM_COMPONENTS = 2;





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

    /** Dispatches event when HitShape gets altered.
     * Subclasses will also use this method to update their PolygonSprites.
     * In this case make sure to call super.somethingChanged() !
     */
    void somethingChanged() {
        eventDispatcher.postEvent(new HitShapesChangedEvent(HitShapesChangedEvent.Action.FORM_CHANGED));
    }

    /** To create a PolygonSprite that we can draw, we need a TextureRegion (1x1px & white).
     * We load it once in the main class and refer to it. */
    void setRegion() {
        region = App.inst().getRegion();
    }



    /** One vertex consists of only x & y coordinate. This is a simple helper method to avoid mixing
     * idx up.
     * @param vertices Float array in which the vertices will be set.
     * @param idx  Index of vertex. CAUTION: Vertices has a size of vertexCount * number of vertexComponents.
     *             Idx is the vertex you want to set. The method then calculates which array index that
     *             actually is.
     */
    void setVertex(float[] vertices, int idx, float x, float y) {
        vertices[idx*NUM_COMPONENTS] = x;
        vertices[idx*NUM_COMPONENTS + 1] = y;
    }


    abstract float[] getData();

}
