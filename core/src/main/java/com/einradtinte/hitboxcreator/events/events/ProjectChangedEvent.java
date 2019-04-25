package com.einradtinte.hitboxcreator.events.events;

import com.einradtinte.hitboxcreator.services.ProjectModel;

public class ProjectChangedEvent extends Event {

    private final ProjectModel project;
    private final Property property;

    public ProjectChangedEvent(ProjectModel project, Property property) {
        this.project = project;
        this.property = property;
    }

    public ProjectModel getProject() {
        return project;
    }

    public Property getProperty() {
        return property;
    }

    public enum Property {
        IMG,
        LOADED,
        SAVED
    }


}
