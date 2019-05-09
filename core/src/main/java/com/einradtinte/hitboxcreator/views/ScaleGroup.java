package com.einradtinte.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.SnapshotArray;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.hitshapes.HitShape;
import com.einradtinte.hitboxcreator.lml.actions.GlobalActions;
import com.einradtinte.hitboxcreator.services.ProjectModel;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Arrays;


public class ScaleGroup extends Group {

    private ProjectModel project;
    private Texture tObject;
    private Image imgObject = new Image();
    private HitShapes hitshapes = new HitShapes();
    public PopupMenu popupMenu;



    public ScaleGroup() {
        addActor(imgObject);
        addActor(hitshapes);
        initPopupMenu();
    }

    /** Circles calculate their segment count for smooth drawing according to their size and the groups
     * scale. They do this in their somethingChanged() method that gets internally called on resize.
     * But they can't detect zooming change, so we have to manually call this method.
     *
     * Also no HitShape can detect when its borderWidth or colors get changed.
     * */
    public void redrawHitShapes() {
        for (Actor actor : hitshapes.getChildren()) {
            ((HitShape) actor).somethingChanged();
        }
    }


    public void addHitShape(HitShape hitShape) {
        project.addHitShape(hitShape);
        hitshapes.addActor(hitShape);
        HitShape.unselectAllHitShapes();
    }



    public void reloadProject(ProjectModel project) {
        this.project = project;
        hitshapes.clear();

        for (HitShape hitShape : project.getHitShapes()) {
            hitshapes.addActor(hitShape);
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
        }

        centerOnParent();
        //TODO: dispose texture
    }

    public SnapshotArray<Actor> getHitShapes() {
        return hitshapes.getChildren();
    }

    /** Centers group on parent. */
    public void centerOnParent() {
        centerOnParent(getWidth() / 2, getHeight() / 2);
    }

    /** Centers the specified point of the group on the parent. */
    public void centerOnParent(float x, float y) {
        setPosition((getParent().getWidth() / 2) - x, (getParent().getHeight() / 2) - y);
    }

    private void initPopupMenu() {
        popupMenu = new PopupMenu() {
            HitShape hitshape;
            boolean unselectOnClose;

            @Override
            public boolean remove() {
                if (unselectOnClose) hitshape.setSelected(false);
                return super.remove();
            }



            @Override
            public void showMenu(Stage stage, Actor actor) {
                removeEveryMenu(stage);

                this.hitshape = (HitShape) actor;
                if (!hitshape.isSelected) {
                    HitShape.unselectAllHitShapes();
                }
                unselectOnClose = !hitshape.isSelected;
                hitshape.setSelected(true);

                super.showMenu(stage, Gdx.input.getX(), stage.getHeight() - Gdx.input.getY());
            }
        };
        String text = App.inst().getI18NBundle().format("delete");
        MenuItem menuItem = new MenuItem(text, VisUI.getSkin().getDrawable("custom/ic-trash-red"),
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        HitShape.removeSelected();
                    }
                });
        popupMenu.addItem(menuItem);
    }

    private class HitShapes extends Group {

        @Override
        public boolean removeActor(Actor actor, boolean unfocus) {
            project.removeHitShape((HitShape) actor);
            return super.removeActor(actor, unfocus);
        }
    }

}
