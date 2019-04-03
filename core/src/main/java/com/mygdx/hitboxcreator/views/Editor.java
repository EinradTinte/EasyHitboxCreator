package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragScrollListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.utils.HitRectangle;
import com.mygdx.hitboxcreator.utils.HitRectangle2;
import com.mygdx.hitboxcreator.utils.MyGroup;

public class Editor extends Group{

    private Image imgBackground, imgObject;
    private MyGroup scaleableGroup;


    private Group scaleGroup;
    private ShapeRenderer shapes;

    private final int[] zommLevel = {10,16,25,33,50,66,100,150,200,300,400,600,800,1000};
    private int zoom = 6;

    private ScaleGroup group;

    public Editor() {
        shapes = App.inst().getShapeRenderer();

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
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Crosshair);
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

                if (zoom-amount >= 0 && zoom-amount < zommLevel.length) {
                    zoom -= amount;
                    float dx = x-group.getX(), dy= y-group.getY();
                    float newScale = zommLevel[zoom] / 100F;
                    group.moveBy(dx*(1-newScale/group.getScaleX()), dy*(1-newScale/group.getScaleX()));
                    group.setScale(newScale);
                }
                return true;
            }
        });




        setBounds(0,0,600,400);

        group = new ScaleGroup();
        addActor(group);
        group.addRectangle(200, 200, 50, 100);
        group.addRectangle(100, 100, 50, 50);
    }






}
