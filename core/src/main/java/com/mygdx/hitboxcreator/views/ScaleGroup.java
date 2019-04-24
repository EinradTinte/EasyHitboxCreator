package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.events.EventDispatcher;
import com.mygdx.hitboxcreator.events.HitShapesChangedEvent;
import com.mygdx.hitboxcreator.events.InfoPropertyChangedEvent;
import com.mygdx.hitboxcreator.utils.HitCircle;
import com.mygdx.hitboxcreator.utils.HitRectangle;
import com.mygdx.hitboxcreator.utils.HitShape;
import com.mygdx.hitboxcreator.utils.ProjectModel;




public class ScaleGroup extends Group {

    private ProjectModel project;
    private Texture tObject;
    private Image imgObject = new Image();



    public ScaleGroup() {
    }

    /** Circles calculate their segment count for smooth drawing according to their size and the groups
     * scale. They do this in their somethingChanged() method that gets internally called on resize.
     * But they can't detect zooming change, so we have to manually call this method.
     *
     * Also no HitShape can detect when its borderWidth or colors get changed.
     * */
    public void redrawHitShapes() {
        for (Actor actor : getChildren()) {
            if (actor instanceof HitShape) {
                ((HitShape) actor).somethingChanged();
            }
        }
    }


    public void addHitShape(HitShape hitShape) {
        project.addHitShape(hitShape);
        addActor(hitShape);
    }


    @Override
    public boolean removeActor(Actor actor, boolean unfocus) {
        if (actor instanceof HitShape) project.removeHitShape((HitShape) actor);
        return super.removeActor(actor, unfocus);
    }



    public void reloadProject(ProjectModel project) {
        this.project = project;
        clear();
        addActor(imgObject);
        for (HitShape hitShape : project.getHitShapes()) {
            addActor(hitShape);
        }
        reloadImage(project.getImage());
    }

    public void reloadImage(String imgPath) {
        if (tObject != null) tObject.dispose();

        if (imgPath == null) {
            imgObject.setDrawable(null);
        } else {

            tObject = new Texture(imgPath);
            imgObject.setDrawable(new TextureRegionDrawable(new TextureRegion(tObject)));
            imgObject.setSize(tObject.getWidth(), tObject.getHeight());
            setSize(tObject.getWidth(), tObject.getHeight());

            centerOnParent();
        }

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
