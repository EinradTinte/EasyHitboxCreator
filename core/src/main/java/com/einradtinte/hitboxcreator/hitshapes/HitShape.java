package com.einradtinte.hitboxcreator.hitshapes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.services.OutputFormat;
import com.einradtinte.hitboxcreator.views.ScaleGroup;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.einradtinte.hitboxcreator.events.EventDispatcher;
import com.einradtinte.hitboxcreator.events.events.HitShapesChangedEvent;
import com.einradtinte.hitboxcreator.statehash.StateHashable;

import java.util.ArrayList;

public abstract class HitShape extends Actor implements Json.Serializable, StateHashable {


    Color cBody;
    static Color cBodyNormal = new Color(1,0,0,0.5F);
    static Color cBodySelected = new Color(1,0.2F,0,0.5F);
    static Color cBorderNormal = new Color(0,0,0,0.5F);
    static Color cBorderSelected = new Color(0,0,1,1);
    static Color cSelected = new Color(0, 0, 1, 0.7F);

    static boolean drawBorder = true;
    static float borderWidth = 2;
    float grabArea = 6;
    int selection;
    public boolean isSelected;
    private int selectionSave;
    private boolean dragging;

    TextureRegion region;
    private EventDispatcher eventDispatcher = App.inst().getEventDispatcher();

    static final int NUM_COMPONENTS = 2;

    abstract boolean contains(float x, float y);

    public abstract boolean contains(Rectangle rec);

    abstract void highlightBorder();

    public abstract float[] getData();

    public abstract OutputFormat.Type getType();

    abstract boolean mouseMoved(float x, float y);

    abstract void touchDragged(Vector2 lastPos, float x, float y);


    public static void setColors(Color normalBody, Color selectedBody, Color normalBorder, Color selectedBorder) {
        cBodyNormal = normalBody;
        cBodySelected = selectedBody;
        cBorderNormal = normalBorder;
        cBorderSelected = selectedBorder;
    }

    public static void setBorderWidth(float width) {
        borderWidth = width;
    }


    /** Initializes all the things every HitShape has.
     * The only thing subclasses have to do is setting there attributes and call init().*/
    void init() {
        setRegion();
        highlightBorder();
        somethingChanged();
        addListener(new HitShapeInputListener());
    }


    /** To create a PolygonSprite that we can draw, we need a TextureRegion (1x1px & white).
     * We load it once in the main class and refer to it. */
    private void setRegion() {
        region = App.inst().getRegion();
    }


    /** PopupMenu to delete HitShape */
    private void addPopupMenu() {
        PopupMenu popupMenu = new PopupMenu() {
            @Override
            public boolean remove() {
                isSelected = false;
                highlightBorder();
                return super.remove();
            }

            @Override
            public void showMenu(Stage stage, float x, float y) {
                if (!isSelected) unselectAllHitShapes();
                isSelected = true;
                highlightBorder();
                super.showMenu(stage, x, y);
            }
        };
        String text = App.inst().getI18NBundle().format("delete");
        MenuItem menuItem = new MenuItem(text, VisUI.getSkin().getDrawable("custom/ic-trash-red"),
                new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                removeSelected();
            }
        });
        popupMenu.addItem(menuItem);

        this.addListener(popupMenu.getDefaultInputListener());
    }



    // this is exactly like super.hit() only I define my own "Hitzone"
    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) return null;
        if (!isVisible()) return null;
        return contains(x, y) ? this : null;
    }





    /**
     * Set mouseMoved() and touchDragged()
     */
    class HitShapeInputListener extends InputListener {
        final Vector2 lastXY = new Vector2();
        Vector2 oldPos = new Vector2();

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            // if the hitshape loses focus while dragging (cursor outside of editor or over button)
            // the selection will be saved and restored when the cursor enters back on
            selection = selectionSave;
            highlightBorder();
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            // pointer == -1: hover event   |   pointer != -1: click event
            if (pointer == -1) {
                selection = 0;
                highlightBorder();
            } else {
                mouseMoved(null, x, y);
            }
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            switch (button) {
                case Input.Buttons.LEFT:
                    selectionSave = selection;
                    toFront();
                    PopupMenu.removeEveryMenu(getStage());
                    if (!HitShape.this.isSelected) unselectAllHitShapes();
                    lastXY.set(x, y);
                    oldPos.set(getX(), getY());
                    dragging = true;
                    // stop event so that CanvasHolder does not react on it
                    event.stop();
                    return true;
                case Input.Buttons.RIGHT:
                    toFront();
                    // stop event so that CanvasHolder does not react on it
                    event.stop();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            selectionSave = 0;
            dragging = false;
            if (button == Input.Buttons.RIGHT && contains(x, y)) {
                try {
                    ((ScaleGroup) getParent().getParent()).popupMenu.showMenu(event.getStage(), HitShape.this);
                } catch (Exception e) {

                }
            }
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            if (!dragging || selection == 0) return;

            float dx = x - lastXY.x;
            float dy = y - lastXY.y;
            float dxp = Math.round(dx), dyp = Math.round(dy);

            if (HitShape.this.isSelected)
                dragSelected(dxp, dyp);
            else
                HitShape.this.touchDragged(lastXY, dxp, dyp);
            somethingChanged();

            lastXY.x = x + (oldPos.x - getX()) - (dx - dxp);
            lastXY.y = y + (oldPos.y - getY()) - (dy - dyp);
            oldPos.set(getX(), getY());

            // mouseMoved does not get called when button is pressed therefore we call it
            // only difference it makes is updating cursor image on resizing and fluent transition
            // from side to corner resizing on rectangles
            // not really a great feature
            //mouseMoved(null, lastXY.x, lastXY.y);
        }

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            return HitShape.this.mouseMoved(x, y);
        }
    }


    /** Dispatches event when HitShape gets altered.
     * Subclasses will also use this method to update their PolygonSprites.
     * In this case make sure to call super.somethingChanged() !
     */
    public void somethingChanged() {
        eventDispatcher.postEvent(new HitShapesChangedEvent(HitShapesChangedEvent.Action.FORM_CHANGED));
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


    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        highlightBorder();
    }

    public static void unselectAllHitShapes() {
        for (Actor hitshape : App.inst().getModelService().getProject().getHitShapes()) {
            ((HitShape) hitshape).setSelected(false);
        }
    }

    /** Drags all selected HitShapes. */
    private static void dragSelected(float dxp, float dyp) {
        Array<HitShape> hitshapes = App.inst().getModelService().getProject().getHitShapes();
        HitShape hitshape;

        for (int i = 0; i < hitshapes.size; i++) {
            hitshape = hitshapes.get(i);
            if (hitshape.isSelected) {
                hitshape.moveBy(dxp, dyp);
                hitshape.somethingChanged();
            }

        }
    }

    public static void removeSelected() {
        ArrayList<HitShape> del = new ArrayList<>();
        for (Actor actor : App.inst().getModelService().getProject().getHitShapes()) {
            HitShape hitshape = (HitShape) actor;
            if (hitshape.isSelected)
                del.add(hitshape);
        }
        for (HitShape h : del)
            h.remove();
    }
}
