package com.einradtinte.hitboxcreator.services;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import com.einradtinte.hitboxcreator.events.EventDispatcher;
import com.einradtinte.hitboxcreator.events.events.HitShapesChangedEvent;
import com.einradtinte.hitboxcreator.events.events.ProjectChangedEvent;
import com.einradtinte.hitboxcreator.hitshapes.HitShape;
import com.einradtinte.hitboxcreator.statehash.StateHashUtils;
import com.einradtinte.hitboxcreator.statehash.StateHashable;

public class ProjectModel implements StateHashable {

    private final Array<HitShape> hitShapes = new Array<>();
    private transient FileHandle projectFile;
    private String imgPath;
    private transient String name = "unnamed project";
    public static final transient String PROJECT_FILE_EXT = "hbx";

    private transient Color cBodyNormal;
    static transient Color cBodySelected;
    static transient Color cBorderNormal;
    static transient Color cBorderSelected;

    private transient EventDispatcher eventDispatcher;


    public ProjectModel() {
    }

    public String getName() { return name; }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void setProjectFile(FileHandle projectFile) {
        this.projectFile = projectFile;
        name = projectFile.file().getName();
    }

    public FileHandle getProjectFile() { return projectFile; }

    public void setImage(String imgPath) {
        this.imgPath = imgPath;
        if (eventDispatcher != null) {
            eventDispatcher.postEvent(new ProjectChangedEvent(this, ProjectChangedEvent.Property.IMG));
        }
    }

    public String getImage() { return imgPath;}

    public void addHitShape(HitShape hitShape) {
        hitShapes.add(hitShape);
        eventDispatcher.postEvent(new HitShapesChangedEvent(HitShapesChangedEvent.Action.QUANTITY_CHANGED));
    }



    public void removeHitShape(HitShape hitShape) {
        hitShapes.removeValue(hitShape, true);
        eventDispatcher.postEvent(new HitShapesChangedEvent(HitShapesChangedEvent.Action.QUANTITY_CHANGED));
    }

    public Array<HitShape> getHitShapes() {
        return hitShapes;
    }


    @Override
    public int computeStateHash() {
        return StateHashUtils.computeHash(projectFile, imgPath, hitShapes, cBodyNormal, cBodySelected, cBorderNormal, cBorderSelected);
    }
}
