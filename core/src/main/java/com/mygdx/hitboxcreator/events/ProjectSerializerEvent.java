package com.mygdx.hitboxcreator.events;

import com.badlogic.gdx.files.FileHandle;
import com.mygdx.hitboxcreator.utils.ProjectModel;

public class ProjectSerializerEvent extends Event{

    private final Action action;
    private final ProjectModel project;
    private final FileHandle file;

    public ProjectSerializerEvent(Action action, ProjectModel project, FileHandle file) {
        this.action = action;
        this.project = project;
        this.file = file;
    }

    public Action getAction() { return action; }

    public ProjectModel getProject() { return project; }

    public FileHandle getFile() { return file; }

    public enum Action { SAVED, LOADED }
}
