package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.utils.HitCircle;
import com.mygdx.hitboxcreator.utils.HitRectangle;
import com.mygdx.hitboxcreator.utils.HitShape;
import com.mygdx.hitboxcreator.utils.ProjectModel;




public class ScaleGroup2 extends Group {

    private ProjectModel project;
    private Texture tObject;
    private Image imgObject = new Image();

    public ScaleGroup2() {
        addActor(imgObject);


        project = new ProjectModel();
        setImage(Gdx.files.internal("obstacle3_intact.png").path());

        //addHitShape(new HitRectangle(50, 50, 200, 100));
        addHitShape(new HitRectangle(100, 100, 100, 200));

        addHitShape(new HitCircle(400,100,50));
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
        super.draw(batch, parentAlpha);
        App.inst().getShader().setTransformMatrix(computeTransform());
    }


}
