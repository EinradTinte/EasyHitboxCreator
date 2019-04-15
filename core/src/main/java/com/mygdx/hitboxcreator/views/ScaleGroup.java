package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.utils.HitCircle;
import com.mygdx.hitboxcreator.utils.HitRectangle;

public class ScaleGroup extends Group implements Disposable {
    private Image imgObject;
    private Texture tObject;
    private ShapeRenderer shapes;


    public ScaleGroup() {

        imgObject = new Image();
        addActor(imgObject);
        shapes = App.inst().getShapeRenderer();

        setSize(600,400);
        setImage(new Texture("obstacle3_intact.png"));
    }

    /** Add a rectangular Hitbox */
    public void addRectangle(float x, float y, float width, float height) {
        addActor(new HitRectangle(x, y, width, height));
    }

    /** Add a round Hitbox */
    public void addCircle(float x, float y, float radius) {
        addActor(new HitCircle(x, y, radius));
    }

    /**
     * Loads the Image of the Object you want to create a hitbox for
     * @param texture
     */
    public void setImage(Texture texture) {
        if (tObject != null) tObject.dispose();
        tObject = texture;
        imgObject.setDrawable(new TextureRegionDrawable(new TextureRegion(tObject)));
        imgObject.setSize(texture.getWidth(), texture.getHeight());
        setSize(texture.getWidth(), texture.getHeight());
    }

    /**
     *  Draws the object, HitShapes and a border around the object for positioning reference.
     *  Although it calls drawChildren(batch, alpha) the HitShapes and border are actually drawn
     *  by a ShapeRenderer that belongs to App. This group and all HitShapes are referencing it.
     *  ShapeRenderer has to be set up (ProjectionMatrix and groupTransform) and started.
     *
     */
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

    @Override
    public void dispose() {
        if (tObject != null) tObject.dispose();
    }
}
