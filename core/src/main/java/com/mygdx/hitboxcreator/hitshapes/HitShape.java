package com.mygdx.hitboxcreator.hitshapes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.events.EventDispatcher;
import com.mygdx.hitboxcreator.events.events.HitShapesChangedEvent;
import com.mygdx.hitboxcreator.services.OutputFormat;
import com.mygdx.hitboxcreator.statehash.StateHashable;

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
    boolean isSelected;
    private int selectionSave;

    TextureRegion region;
    private EventDispatcher eventDispatcher = App.inst().getEventDispatcher();

    static final int NUM_COMPONENTS = 2;

    abstract boolean contains(float x, float y);

    abstract void highlightBorder();

    abstract void initListener();

    public abstract float[] getData();

    public abstract OutputFormat.Type getType();


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
        addPopupMenu();
        highlightBorder();
        somethingChanged();
        initListener();
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
                remove();
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
        final Vector2 lastPos = new Vector2();

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            // if the hitshape loses focus while dragging (cursor outside of editor or over button)
            // the selection will be saved and restored when the cursor enters back on
            selection = selectionSave;
            highlightBorder();
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            selectionSave = 0;
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
                    lastPos.set(x, y);
                    return true;
                case Input.Buttons.RIGHT:
                    toFront();
                    return false;
                default:
                    return false;
            }
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



}
