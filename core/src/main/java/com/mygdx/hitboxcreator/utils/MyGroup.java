package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hitboxcreator.App;

public class MyGroup extends Actor {
    private Array<HitRectangle> rectangles = new Array<HitRectangle>();
    private Array<Circle> circles = new Array<Circle>();
    private ShapeRenderer shapes;



    public MyGroup() {
        shapes = App.inst().getShapeRenderer();

        addListener(new InputListener() {


            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                return rectangles.get(0).inputListener.mouseMoved(event, x, y);
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                scaleBy(-amount * 0.2F);
                for (HitRectangle rec : rectangles) rec.setScale(getScaleX());
                return true;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                /*if (button == Input.Buttons.LEFT) {
                    for (HitRectangle rec : rectangles) {
                        if (rec.inputListener.touchDown(event, x, y, pointer, button))
                            setF
                    }
                    return true;
                } else return false;
                */
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
            }
        });

        rectangles.add(new HitRectangle(200, 200, 50, 100));

        setBounds(0,0,600,400);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawShapes();
    }

    private void drawShapes() {
        float offsetX = getX()/getScaleX(), offsetY = getY()/getScaleX();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        //shapes.setProjectionMatrix(viewport.getCamera().combined);
        shapes.setProjectionMatrix(getStage().getViewport().getCamera().combined);
        shapes.begin();
        shapes.set(ShapeRenderer.ShapeType.Filled);
        for (HitRectangle rec : rectangles) {
            float cx = rec.x, cy = rec.y;
            rec.x = cx + offsetX;
            rec.y = cy + offsetY;
            rec.draw();
            rec.x = cx;
            rec.y = cy;
        }
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }


}
