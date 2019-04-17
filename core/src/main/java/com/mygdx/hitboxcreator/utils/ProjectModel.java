package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import com.mygdx.hitboxcreator.events.EventDispatcher;
import com.mygdx.hitboxcreator.events.ProjectPropertyChangedEvent;
import com.mygdx.hitboxcreator.statehash.StateHashUtils;
import com.mygdx.hitboxcreator.statehash.StateHashable;

public class ProjectModel implements StateHashable {

    private final Array<HitRectangle> recs = new Array<HitRectangle>();
    private final Array<HitCircle> circles = new Array<HitCircle>();
    private FileHandle projectFile;
    private String imgPath;

    private Color cBodyNormal;
    static Color cBodySelected;
    static Color cBorderNormal;
    static Color cBorderSelected;

    private EventDispatcher eventDispatcher;


    public ProjectModel() {
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void setProjectFile(FileHandle projectFile) {
        this.projectFile = projectFile;
    }

    public FileHandle getProjectFile() { return projectFile; }

    public void setImage(String imgPath) {
        this.imgPath = imgPath;
        if (eventDispatcher != null) {
            eventDispatcher.postEvent(new ProjectPropertyChangedEvent(this, ProjectPropertyChangedEvent.Property.IMG));
        }
    }

    public String getImage() { return imgPath;}

    public void addHitShape(HitShape hitShape) {
        if (hitShape.getClass() == HitRectangle.class) {
            recs.add((HitRectangle) hitShape);
        } else if (hitShape.getClass() == HitCircle.class) {
            circles.add((HitCircle) hitShape);
        }
    }



    public void removeHitShape(HitShape hitShape) {
        if (hitShape.getClass() == HitRectangle.class) {
            recs.removeValue((HitRectangle) hitShape, true);
        } else if (hitShape.getClass() == HitCircle.class) {
            circles.removeValue((HitCircle) hitShape, true);
        }
    }

    public HitShape[] getHitShapes() {
        return recs.items;
    }


    @Override
    public int computeStateHash() {
        return StateHashUtils.computeHash(projectFile, imgPath, recs, circles, cBodyNormal, cBodySelected, cBorderNormal, cBorderSelected);
    }
}
