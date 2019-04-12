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
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.utils.HitShape;
import com.mygdx.hitboxcreator.utils.ProjectModel;




public class ScaleGroup2 extends Group {
    private final ShapeRenderer shapes = App.inst().getShapeRenderer();
    private ProjectModel project;
    private Texture tObject;
    private Image imgObject = new Image();

    public ScaleGroup2() {
        addActor(imgObject);


        project = new ProjectModel();
        setImage(Gdx.files.internal("obstacle3_intact.png").path());
    }


    public void addHitShape(HitShape hitShape) {
        project.addHitShape(hitShape);
        addActor(hitShape);
    }

    public void removeHitShape(HitShape hitShape) {
        removeActor(hitShape);
        project.removeHitShape(hitShape);
    }


    public void reloadProject(ProjectModel project) {
        clear();
        for (HitShape hitShape : project.getHitShapes()) {
            addActor(hitShape);
        }
        setImage(project.getImage());
    }

    public void setImage(String imgPath) {
        if (tObject != null) tObject.dispose();
        tObject = new Texture(imgPath);
        imgObject.setDrawable(new TextureRegionDrawable(new TextureRegion(tObject)));
        imgObject.setSize(tObject.getWidth(), tObject.getHeight());
        setSize(tObject.getWidth(), tObject.getHeight());

        project.setImage(imgPath);
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


}
