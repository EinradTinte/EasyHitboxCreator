package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.files.FileHandle;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.processor.event.EventDispatcher;
import com.mygdx.hitboxcreator.events.ProjectSerializerEvent;

public class ProjectSerializer {

    @Inject EventDispatcher eventDispatcher;


    public ProjectModel loadProject(FileHandle file) {
        //TODO: load
        ProjectModel project = new ProjectModel();

        project.setProjectFile(file);

        eventDispatcher.postEvent(new ProjectSerializerEvent(ProjectSerializerEvent.Action.LOADED, project, file));

        return project;
    }
}
