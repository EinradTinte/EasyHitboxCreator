package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.events.EventDispatcher;
import com.mygdx.hitboxcreator.events.InfoPropertyChangedEvent;
import com.mygdx.hitboxcreator.utils.HitCircle;
import com.mygdx.hitboxcreator.utils.HitRectangle;
import com.mygdx.hitboxcreator.utils.HitShape;
import com.mygdx.hitboxcreator.utils.ProjectModel;




public class ScaleGroup2 extends Group {

    private ProjectModel project;
    private Texture tObject;
    private Image imgObject = new Image();
    private EventDispatcher eventDispatcher;

    public ScaleGroup2() {
        addActor(imgObject);
        eventDispatcher = App.inst().getEventDispatcher();

        addHitShape(new HitRectangle(100, 100, 100, 200));

        //project = new ProjectModel();
        //reloadImage(Gdx.files.internal("obstacle3_intact.png").path());

        //addHitShape(new HitRectangle(50, 50, 200, 100));
        //addHitShape(new HitRectangle(100, 100, 100, 200));

        addHitShape(new HitCircle(400,100,50));
    }


    public void addHitShape(HitShape hitShape) {
        //project.addHitShape(hitShape);
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
        reloadImage(project.getImage());
    }

    public void reloadImage(String imgPath) {
        if (tObject != null) tObject.dispose();
        tObject = new Texture(imgPath);
        imgObject.setDrawable(new TextureRegionDrawable(new TextureRegion(tObject)));
        imgObject.setSize(tObject.getWidth(), tObject.getHeight());
        setSize(tObject.getWidth(), tObject.getHeight());

        centerOnParent();


        //TODO: dispose texture
    }

    /** Centers group on parent. */
    public void centerOnParent() {
        centerOnParent(getWidth() / 2, getHeight() / 2);
    }

    /** Centers the specified point of the group on the parent. */
    public void centerOnParent(float x, float y) {
        setPosition((getParent().getWidth() / 2) - x, (getParent().getHeight() / 2) - y);
    }

    @Override
    protected void sizeChanged() {

    }




}
