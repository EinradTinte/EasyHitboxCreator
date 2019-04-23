package com.mygdx.hitboxcreator.services;


import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.events.Event;
import com.mygdx.hitboxcreator.events.EventDispatcher;
import com.mygdx.hitboxcreator.events.EventListener;
import com.mygdx.hitboxcreator.events.ProjectSerializerEvent;
import com.mygdx.hitboxcreator.utils.ProjectModel;

public class ModelService {

    EventDispatcher eventDispatcher;

    private ProjectModel projectModel;
    private int lastProjectStateHash;

    public ModelService() {
        eventDispatcher = App.inst().getEventDispatcher();
        setProject(new ProjectModel());

        eventDispatcher.addEventListener(new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                if (event.is(ProjectSerializerEvent.class)) {
                    if (((ProjectSerializerEvent)event).getAction() == ProjectSerializerEvent.Action.SAVED) {
                        updateProjectStateHash();
                    }
                }
            }
        });
    }



    public ProjectModel getProject() { return projectModel; }

    public void setProject(ProjectModel projectModel) {
        if (this.projectModel == projectModel) return;

        projectModel.setEventDispatcher(eventDispatcher);
        this.projectModel = projectModel;

        updateProjectStateHash();

        eventDispatcher.postEvent(new ProjectSerializerEvent(ProjectSerializerEvent.Action.LOADED, projectModel, projectModel.getProjectFile()));
    }

    public boolean hasProjectChanged() {
        return lastProjectStateHash != projectModel.computeStateHash();
    }

    private void updateProjectStateHash() {
        this.lastProjectStateHash = projectModel.computeStateHash();
    }
}
