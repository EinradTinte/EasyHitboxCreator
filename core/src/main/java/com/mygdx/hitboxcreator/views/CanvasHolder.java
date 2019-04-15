package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.utils.ProjectModel;

public class CanvasHolder extends WidgetGroup {
    private static final int[] ZOOM_LEVELS = {10, 16, 25, 33, 50, 66, 100, 150, 200, 300, 400, 600, 800, 1000};
    private static final int DEFAULT_ZOOM_INDEX = 6;
    private final float PADDING = 20;

    private ScaleGroup2 group;

    private Listener listener;
    private int zoomIndex = DEFAULT_ZOOM_INDEX;




    public CanvasHolder(Skin skin) {



        addListener(new PanZoomListener());
        group = new ScaleGroup2();
        addActor(group);
    }

    public void setProject(ProjectModel model) {
        group.reloadProject(model);

        fitToCenter();
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

    private void fitToCenter() {
        group.setPosition(
                (getWidth() - group.getWidth() * group.getScaleX()) / 2,
                (getHeight() - group.getHeight() * group.getScaleY()) /2);
    }

    public void addHitRectangle() {

    }

    public interface Listener {
        void onZoomChanged(int percentage);
    }

    private void setZoomIndex(int zoomIndex) {
        this.zoomIndex = zoomIndex;
        float scale = ZOOM_LEVELS[zoomIndex] / 100f;

        if (listener != null) {
            listener.onZoomChanged(ZOOM_LEVELS[zoomIndex]);
        }
    }

    private class PanZoomListener extends InputListener {
        private final Vector2 lastPos = new Vector2();

        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            getStage().setScrollFocus(CanvasHolder.this);
        }

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
            group.setScale(newScale);
            setZoomIndex(zoomIndex);
            return true;
        }
    }
}
