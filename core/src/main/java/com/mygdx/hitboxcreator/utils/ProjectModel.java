package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import com.mygdx.hitboxcreator.events.EventDispatcher;
import com.mygdx.hitboxcreator.events.ProjectPropertyChangedEvent;
import com.mygdx.hitboxcreator.statehash.StateHashUtils;
import com.mygdx.hitboxcreator.statehash.StateHashable;

import java.util.ArrayList;

public class ProjectModel implements StateHashable {

    private final Array<HitShape> hitShapes = new Array<>();
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
        hitShapes.add(hitShape);
    }



    public void removeHitShape(HitShape hitShape) {
            hitShapes.removeValue(hitShape, true);
    }

    public Array<HitShape> getHitShapes() {
        return hitShapes;
    }


    @Override
    public int computeStateHash() {
        return StateHashUtils.computeHash(projectFile, imgPath, hitShapes, cBodyNormal, cBodySelected, cBorderNormal, cBorderSelected);
    }
}
