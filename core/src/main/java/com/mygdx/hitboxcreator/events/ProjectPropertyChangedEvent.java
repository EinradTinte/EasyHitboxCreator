package com.mygdx.hitboxcreator.events;

import com.mygdx.hitboxcreator.utils.ProjectModel;

public class ProjectPropertyChangedEvent extends Event{

    private final ProjectModel project;
    private final Property property;

    public ProjectPropertyChangedEvent(ProjectModel project, Property property) {
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
    }


}
