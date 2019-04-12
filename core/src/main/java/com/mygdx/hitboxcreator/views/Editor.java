package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.hitboxcreator.App;

public class Editor extends Group{

    private Image imgBackground;






    private static final int[] ZOOM_LEVELS = {10, 16, 25, 33, 50, 66, 100, 150, 200, 300, 400, 600, 800, 1000};
    private static final int DEFAULT_ZOOM_INDEX = 6;

    private int zoomIndex = DEFAULT_ZOOM_INDEX;

    private ScaleGroup group;

    public Editor() {


        addListener(new InputListener() {
            float oldX, oldY;

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                getStage().setScrollFocus(Editor.this);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.MIDDLE) {
                    oldX = x;
                    oldY = y;
                    App.inst().setCursor(App.CursorStyle.Crosshair);
                    return true;
                } else return false;

            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                group.moveBy(x - oldX, y - oldY);
                oldX = x;
                oldY = y;
            }

            /** Zoom. Changes scaling and moves group so it zooms on cursor position. */
            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {

                zoomIndex = Math.max(0, Math.min(ZOOM_LEVELS.length-1, zoomIndex - amount));

                float dx = x-group.getX(), dy= y-group.getY();
                float newScale = ZOOM_LEVELS[zoomIndex] / 100F;
                group.moveBy(dx*(1-newScale/group.getScaleX()), dy*(1-newScale/group.getScaleX()));
                group.setScale(newScale);

                return true;
            }
        });




        setBounds(0,0,600,400);

        group = new ScaleGroup();
        addActor(group);
        group.addRectangle(200, 200, 50, 100);
        group.addRectangle(100, 100, 50, 50);
        group.addCircle(200, 50, 40);
    }






}
