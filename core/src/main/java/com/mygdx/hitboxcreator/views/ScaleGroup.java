package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.utils.HitRectangle2;

public class ScaleGroup extends Group {
    private Image imgObject;
    private ShapeRenderer shapes;

    public ScaleGroup() {
        imgObject = new Image();
        addActor(imgObject);
        shapes = App.inst().getShapeRenderer();

        setBounds(0,0,600,400);
        setImage(new Texture("obstacle3_intact.png"));

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return false;
            }
        });
    }

    public void addRectangle(float x, float y, float width, float height) {
        addActor(new HitRectangle2(x, y, width, height));


    }


    public void setImage(Texture texture) {
        imgObject.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        imgObject.setSize(texture.getWidth(), texture.getHeight());
        setSize(texture.getWidth(), texture.getHeight());
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapes.setProjectionMatrix(getStage().getViewport().getCamera().combined);
        shapes.begin();


        applyTransform(shapes, computeTransform());

        shapes.set(ShapeRenderer.ShapeType.Filled);
        super.draw(batch, parentAlpha);



        resetTransform(shapes);

        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(Color.RED);
        shapes.rect(getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
