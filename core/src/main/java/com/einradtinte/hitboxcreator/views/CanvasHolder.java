package com.einradtinte.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.hitshapes.HitShape;
import com.einradtinte.hitboxcreator.lml.actions.GlobalActions;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

public class CanvasHolder extends WidgetGroup {
    private static final int[] ZOOM_LEVELS = {25, 33, 50, 66, 100, 150, 200, 300, 400, 600, 800, 1000};
    public static final int DEFAULT_ZOOM_INDEX = 4;
    private final float PADDING = 20;

    private ScaleGroup group;

    private Listener listener;
    private int zoomIndex = DEFAULT_ZOOM_INDEX;

    private Rectangle tmpRectangle = new Rectangle();

    private Image imgSelection;
    private boolean dragging, selecting;
    private Vector2 selectionStart = new Vector2();
    private Rectangle selectionRec = new Rectangle();

    private PopupMenu popupMenu;


    public CanvasHolder() {
        addListener(new PanZoomListener());
        GlobalActions.getScrollOnHover(this);
        group = new ScaleGroup();
        addActor(group);

        imgSelection = new Image(VisUI.getSkin().getPatch("select-frame"));

        // popupMenu to center its content
        popupMenu = new PopupMenu();
        String text = App.inst().getI18NBundle().format("menuItemCanvas");
        MenuItem menuItem = new MenuItem(text);
        menuItem.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                group.centerOnParent();
            }
        });
        popupMenu.addItem(menuItem);
    }


    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (!tmpRectangle.set(getX(), getY(), getWidth(), getHeight()).contains(x, y)) return null;
        return super.hit(x, y, touchable);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /** Keeps group on Screen */
    private void fitOnScreen() {
        if (group.getRight() < PADDING) group.setX(PADDING, Align.right);
        if (group.getX() > getWidth() - PADDING) group.setX(getWidth() - PADDING);
        if (group.getTop() < PADDING) group.setY(PADDING, Align.top);
        if (group.getY() > getHeight() - PADDING) group.setY(getHeight() - PADDING);
    }




    public ScaleGroup getScaleGroup() { return group; }


    public interface Listener {
        void onZoomChanged(int percentage);
    }

    public void setZoomIndex(int zoomIndex) {
        this.zoomIndex = zoomIndex;
        float zoom = ZOOM_LEVELS[zoomIndex] / 100f;
        group.setScale(zoom);

        if (listener != null) {
            listener.onZoomChanged(ZOOM_LEVELS[zoomIndex]);
        }

        // changing border width according to zoom level
        HitShape.setBorderWidth(Math.max(1, 2 / zoom));

        group.redrawHitShapes();
    }

    /** Checks if any HitShapes fall in the selection-box and selects them. */
    private void checkSelection() {
        float dx = group.getX(), dy = group.getY();
        float scaling = group.getScaleX();
        selectionRec.set((imgSelection.getX() - dx)/scaling, (imgSelection.getY() - dy)/scaling, imgSelection.getWidth()/scaling, imgSelection.getHeight()/scaling);
        for (Actor hitshape : group.getHitShapes()) {
            ((HitShape) hitshape).setSelected(((HitShape) hitshape).contains(selectionRec));
        }
    }

    private class PanZoomListener extends InputListener {
        private final Vector2 lastPos = new Vector2();



        // TODO: nur eine Maustaste gleichzeitig

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            switch (button) {
                case Input.Buttons.MIDDLE:
                        App.inst().setCursor(App.CursorStyle.Crosshair);
                        lastPos.set(x, y);
                        dragging = true;
                        return true;
                case Input.Buttons.LEFT:
                    addActor(imgSelection);
                    selectionStart.set(x, y);
                    imgSelection.setBounds(selectionStart.x, selectionStart.y, 0, 0);
                    HitShape.unselectAllHitShapes();
                    selecting = true;
                    return true;
                case Input.Buttons.RIGHT:
                    HitShape.unselectAllHitShapes();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            switch (button) {
                case Input.Buttons.MIDDLE:
                    dragging = false;
                    break;
                case Input.Buttons.LEFT:
                    selecting = false;
                    removeActor(imgSelection);
                    break;
                case Input.Buttons.RIGHT:
                    popupMenu.showMenu(event.getStage(), event.getStageX(), event.getStageY());
                    break;
            }
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            if (dragging) {
                group.moveBy(x - lastPos.x, y - lastPos.y);
                lastPos.set(x, y);
            }
            if (selecting) {
                imgSelection.setBounds(Math.min(selectionStart.x, x), Math.min(selectionStart.y, y), Math.abs(x - selectionStart.x), Math.abs(y - selectionStart.y));
                checkSelection();
            }
        }

        /** Zoom. Changes scaling and moves group so it zooms on cursor position. */
        @Override
        public boolean scrolled(InputEvent event, float x, float y, int amount) {

            zoomIndex = Math.max(0, Math.min(ZOOM_LEVELS.length-1, zoomIndex - amount));

            float dx = x-group.getX(), dy= y-group.getY();
            float newScale = ZOOM_LEVELS[zoomIndex] / 100F;
            group.moveBy(dx*(1-newScale/group.getScaleX()), dy*(1-newScale/group.getScaleX()));
            setZoomIndex(zoomIndex);
            return true;
        }
    }
}
