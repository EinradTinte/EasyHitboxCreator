package com.einradtinte.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.hitshapes.HitShape;
import com.einradtinte.hitboxcreator.lml.actions.GlobalActions;

public class CanvasHolder extends WidgetGroup {
    private static final int[] ZOOM_LEVELS = {25, 33, 50, 66, 100, 150, 200, 300, 400, 600, 800, 1000};
    public static final int DEFAULT_ZOOM_INDEX = 4;
    private final float PADDING = 20;

    private ScaleGroup group;

    private Listener listener;
    private int zoomIndex = DEFAULT_ZOOM_INDEX;

    private Rectangle tmpRectangle = new Rectangle();




    public CanvasHolder() {
        addListener(new PanZoomListener());
        GlobalActions.getScrollOnHover(this);
        group = new ScaleGroup();
        addActor(group);
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

    private class PanZoomListener extends InputListener {
        private final Vector2 lastPos = new Vector2();

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (button != Input.Buttons.MIDDLE) return false;

            App.inst().setCursor(App.CursorStyle.Crosshair);
            lastPos.set(x, y);
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
                group.moveBy(x - lastPos.x, y - lastPos.y);
                lastPos.set(x, y);
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
