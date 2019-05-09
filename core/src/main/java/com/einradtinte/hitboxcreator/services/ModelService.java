package com.einradtinte.hitboxcreator.services;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.events.EventDispatcher;
import com.einradtinte.hitboxcreator.hitshapes.HitCircle;
import com.einradtinte.hitboxcreator.hitshapes.HitRectangle;
import com.einradtinte.hitboxcreator.events.events.ProjectChangedEvent;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

public class ModelService {

    EventDispatcher eventDispatcher;

    private ProjectModel project;
    private int lastProjectStateHash;
    public static FileHandle dirLastProject;
    public static FileHandle dirLastImage;

    public ModelService() {
        eventDispatcher = App.inst().getEventDispatcher();
    }



    public ProjectModel getProject() { return project; }

    public void setProject(ProjectModel projectModel) {
        // uncommented because I already set it as project if the imagepath is faulty
        //if (this.project == projectModel) return;

        this.project = projectModel;
        project.setEventDispatcher(eventDispatcher);

        updateProjectStateHash();

        eventDispatcher.postEvent(new ProjectChangedEvent(projectModel, ProjectChangedEvent.Property.LOADED));
    }

    public boolean hasProjectChanged() {
        return lastProjectStateHash != project.computeStateHash();
    }

    private void updateProjectStateHash() {
        this.lastProjectStateHash = project.computeStateHash();
    }

    public void loadProjectFromFile(FileHandle file) {
        Json json = new Json();
        json.addClassTag("hitRectangle", HitRectangle.class);
        json.addClassTag("hitCircle", HitCircle.class);
        ProjectModel projectModel = json.fromJson(ProjectModel.class, file);
        projectModel.setProjectFile(file);
        // check if the loaded image path exists
        if (projectModel.getImage() != null && !Gdx.files.external(projectModel.getImage()).exists()) {
            Dialogs.showErrorDialog(App.inst().getStage(), App.inst().getI18NBundle().format("dlgTextImageNotExists", projectModel.getImage()));
            projectModel.setImage(null);
            this.project = projectModel;
            saveProjectToFile();
        }
        setProject(projectModel);
    }

    public void saveProjectToFile(FileHandle file) {
        project.setProjectFile(file);
        Json json = new Json();
        json.addClassTag("hitRectangle", HitRectangle.class);
        json.addClassTag("hitCircle", HitCircle.class);
        file.writeString(json.toJson(project), false);

        updateProjectStateHash();
        eventDispatcher.postEvent(new ProjectChangedEvent(project, ProjectChangedEvent.Property.SAVED));
    }

    public void saveProjectToFile() {
        saveProjectToFile(project.getProjectFile());
    }
}
