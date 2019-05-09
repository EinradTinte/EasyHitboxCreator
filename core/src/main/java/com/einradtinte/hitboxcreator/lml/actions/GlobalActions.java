package com.einradtinte.hitboxcreator.lml.actions;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.einradtinte.hitboxcreator.hitshapes.HitShape;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.services.ModelService;
import com.einradtinte.hitboxcreator.services.ProjectModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GlobalActions implements ActionContainer {

    private static final String DIR_PREFS = "dirPrefs";


    public static boolean hasCloseDlg;






    @LmlAction("getScrollOnHover") public static void getScrollOnHover(final Actor scrollPane) {
        scrollPane.addListener( new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                event.getStage().setScrollFocus(scrollPane);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (event.getRelatedActor() != toActor)
                    event.getStage().setScrollFocus(null);
            }
        });
    }

    /** VisTextArea has a bug that crashes it when the first line is only a newline character and you
     * want to select that. So we check for it and delete it.
     */
    public static String proofString(String s) {
        /*
        String[] lines = s.split("\\n");
        String out = "";

            for (int i = 0; i < lines.length; i++) {
                if (!lines[i].equals("")) {
                    out += lines[i];
                    if (i < lines.length-1) out += '\n';
                }
            }

        return out;
        */
        int i = s.indexOf("\n");
        return i == 0 ? s.substring(1) : s;
    }

    @LmlAction("resetViewFocus") public static void resetViewFocus() {
        FocusManager.resetFocus(App.inst().getStage());
    }

    @LmlAction("newProject") public  void newProjectChecked() {
        if (getModelService().hasProjectChanged()) {
            showUnsavedChangesDlg(App.inst().getI18NBundle().format("dlgTextUnsavedProject"), "newProject");
        } else newProject();
    }

    public static void newProject() {
        App.inst().getModelService().setProject(new ProjectModel());
    }

    @LmlAction("openProject") public void openProjectChecked() {
        if (getModelService().hasProjectChanged()) {
            showUnsavedChangesDlg(App.inst().getI18NBundle().format("dlgTextUnsavedProject"), "openProject");
        } else openProject();
    }

    public static void openProject() {
        FileTypeFilter typeFilter = new FileTypeFilter(false);
        typeFilter.addRule(String.format("Hitbox Project Files (*.%s)", ProjectModel.PROJECT_FILE_EXT), ProjectModel.PROJECT_FILE_EXT);

        FileChooser fileChooser = new FileChooser(ModelService.dirLastProject, FileChooser.Mode.OPEN);
        //fileChooser.setIconProvider();
        fileChooser.setFileTypeFilter(typeFilter);
        fileChooser.setListener(new SingleFileChooserListener() {
            @Override
            protected void selected(FileHandle fileHandle) {

                ModelService.dirLastProject = fileHandle.parent();
                App.inst().getModelService().loadProjectFromFile(fileHandle);
            }
        });

        Stage stage = App.inst().getStage();
        stage.addActor(fileChooser.fadeIn());
        fileChooser.setSize(Math.min(fileChooser.getWidth(), stage.getWidth()),
                Math.min(fileChooser.getHeight(), stage.getHeight()));
    }



    @LmlAction("saveProjectNoExit") public void saveAndNoExit() {
        saveProject(null);
    }

    @LmlAction("saveProjectAs") public void saveAsAndNoExit() {
        saveProjectAs(null);
    }

    /** Save project to its filehandle. If no filehandle exists opens saveAs dialog.
     * Accepts a method to invoke after saving. */
    public static void saveProject(String methodAfter) {
        if (App.inst().getModelService().getProject().getProjectFile() == null)
            saveProjectAs(methodAfter);
        else {
            App.inst().getModelService().saveProjectToFile();

            if (methodAfter != null) {
                try {
                    Method method = GlobalActions.class.getDeclaredMethod(methodAfter);
                    method.invoke(GlobalActions.class);
                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveProjectAs(String methodAfter) {
        FileTypeFilter typeFilter = new FileTypeFilter(false);
        typeFilter.addRule(String.format("Hitbox Project Files (*.%s)", ProjectModel.PROJECT_FILE_EXT), ProjectModel.PROJECT_FILE_EXT);

        FileChooser fileChooser = new FileChooser(ModelService.dirLastProject, FileChooser.Mode.SAVE);
        //fileChooser.setIconProvider();
        fileChooser.setFileTypeFilter(typeFilter);
        fileChooser.setListener(new SingleFileChooserListener() {
            @Override
            protected void selected(FileHandle fileHandle) {
                if (fileHandle.extension().length() == 0)
                    fileHandle = Gdx.files.getFileHandle(fileHandle.path()+"."+ProjectModel.PROJECT_FILE_EXT, fileHandle.type());

                ModelService.dirLastProject = fileHandle.parent();
                App.inst().getModelService().saveProjectToFile(fileHandle);

                if (methodAfter != null) {
                    try {
                        Method method = GlobalActions.class.getDeclaredMethod(methodAfter);
                        method.invoke(this);
                    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Stage stage = App.inst().getStage();
        stage.addActor(fileChooser.fadeIn());
        fileChooser.setSize(Math.min(fileChooser.getWidth(), stage.getWidth()),
                Math.min(fileChooser.getHeight(), stage.getHeight()));
    }

    @LmlAction("exit") public static void exit() {
        Gdx.app.exit();
    }

    @LmlAction("cls") void cls() {
        hasCloseDlg = false;
    }


    /** Convenient way to only need on dialog for multiple scenarios.
     *
     * @param text  Text the dialog will display.
     * @param method    Method that gets invoked after saving changes when user clicks on "yes (save changes)"
     */
    public static void showUnsavedChangesDlg(String text, String method) {
        LmlParser parser = App.inst().getParser();
        VisDialog dlg = (VisDialog) parser.parseTemplate(Gdx.files.internal("lml/unsavedChangesDialog.lml")).first();
        // setting text
        Label label = (Label) parser.getActorsMappedByIds().get("dlgTextUnsavedChanges");
        label.setText(text);
        // setting yes button
        TextButton btnYes = (TextButton) parser.getActorsMappedByIds().get("unsavedChangesYes");
        btnYes.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                saveProject(method);
            }
        });
        // setting no button
        TextButton btnNo = (TextButton) parser.getActorsMappedByIds().get("unsavedChangesNo");
        btnNo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (method != null) {
                    try {
                        Method m = GlobalActions.class.getDeclaredMethod(method);
                        m.invoke(GlobalActions.class);
                    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        dlg.show(App.inst().getStage());
    }


    public static void saveDirPrefs() {
        Preferences prefs = Gdx.app.getPreferences(DIR_PREFS);
        if (ModelService.dirLastImage != null) {
            prefs.putString("dirLastImage", ModelService.dirLastImage.path());
            prefs.putString("fileTypeImage", ModelService.dirLastImage.type().name());
        }
        if (ModelService.dirLastProject != null) {
            prefs.putString("dirLastProject", ModelService.dirLastProject.path());
            prefs.putString("fileTypeProject", ModelService.dirLastProject.type().name());
        }

        prefs.flush();
    }


    public static void loadDirPrefs() {
        Preferences prefs = Gdx.app.getPreferences(DIR_PREFS);
        ModelService.dirLastImage = prefs.getString("dirLastImage").equals("") ? null : Gdx.files.getFileHandle(prefs.getString("dirLastImage"),
                Files.FileType.valueOf(prefs.getString("fileTypeImage")));
        ModelService.dirLastProject = prefs.getString("dirLastProject").equals("") ? null : Gdx.files.getFileHandle(prefs.getString("dirLastProject"),
                Files.FileType.valueOf(prefs.getString("fileTypeProject")));
    }





    private ModelService getModelService() {
        return App.inst().getModelService();
    }


}
