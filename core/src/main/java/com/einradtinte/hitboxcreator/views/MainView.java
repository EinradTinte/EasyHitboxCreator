package com.einradtinte.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.lml.actions.GlobalActions;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kotcrab.vis.ui.contrib.widget.file.ImgScalrFileChooserIconProvider;
import com.kotcrab.vis.ui.contrib.widget.file.WindowsFileChooserIconProvider;
import com.kotcrab.vis.ui.widget.ScrollableTextArea;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.einradtinte.hitboxcreator.events.events.Event;
import com.einradtinte.hitboxcreator.events.EventDispatcher;
import com.einradtinte.hitboxcreator.events.EventListener;
import com.einradtinte.hitboxcreator.events.events.HitShapesChangedEvent;
import com.einradtinte.hitboxcreator.events.events.ProjectChangedEvent;
import com.einradtinte.hitboxcreator.hitshapes.HitShape;
import com.einradtinte.hitboxcreator.services.ModelService;
import com.einradtinte.hitboxcreator.services.OutputBuilder;


public class MainView extends AbstractLmlView {

    private ModelService modelService;
    private EventDispatcher eventDispatcher;
    private OutputBuilder outputBuilder;
    private Stage stage;

    private OutputSettingsDialog outputSettingsDialog;

    private FileChooser fileChooser;
    private FileTypeFilter typeFilterImg;

    @LmlActor("edtImgPath") VisTextField edtImgPath;
    @LmlActor("editor") Editor editor;
    @LmlActor("outputTextArea") ScrollableTextArea outputArea;
    @LmlActor("btnClearImg") VisImageButton btnClearImg;




    //region -- view --
    public MainView() {
        super(App.inst().getStage());
    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("lml/main.lml");
    }

    @Override
    public String getViewId() {
        return "main";
    }
    //endregion view



    public void init() {
        stage = getStage();
        modelService = App.inst().getModelService();
        eventDispatcher = App.inst().getEventDispatcher();
        outputBuilder = App.inst().getOutputFormatService().getOutputBuilder();
        initiateFileChooser();
        initEventListener();
        outputSettingsDialog = new OutputSettingsDialog();

        // load model here because Editor gets first initiated here and can't act on project loaded events before that
        GlobalActions.newProject();
    }


    private void initEventListener() {
        eventDispatcher.addEventListener(new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                if (event.is(HitShapesChangedEvent.class)) {
                    setOutputText();
                    projectChanged();
                } else if (event.is(ProjectChangedEvent.class)) {
                    switch (((ProjectChangedEvent) event).getProperty()) {
                        case IMG:
                            setImgText(((ProjectChangedEvent) event).getProject().getImage());
                            break;
                        case LOADED:
                            setImgText(((ProjectChangedEvent) event).getProject().getImage());
                            break;
                    }
                    projectChanged();
                }
            }
        });
    }

    private void initiateFileChooser() {
        typeFilterImg = new FileTypeFilter(false);
        typeFilterImg.addRule("Image files (*.png, *.jpg)", "png", "jpg");



        fileChooser = new FileChooser(ModelService.dirLastImage, FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        fileChooser.setFileTypeFilter(typeFilterImg);


        if (WindowsFileChooserIconProvider.isPlatformSupported()) {
            fileChooser.setIconProvider(new WindowsFileChooserIconProvider(fileChooser));
        } else { //fallback to ImgScalrFileChooserIconProvider
            fileChooser.setIconProvider(new ImgScalrFileChooserIconProvider(fileChooser));
        }
        fileChooser.setViewMode(FileChooser.ViewMode.SMALL_ICONS);


        fileChooser.setListener(new SingleFileChooserListener() {
            @Override
            protected void selected(FileHandle fileHandle) {
                String imgPath = fileHandle.file().getAbsolutePath();
                ModelService.dirLastImage = fileHandle.parent();
                modelService.getProject().setImage(imgPath);
            }
        });

        // TODO: dispose IconProvider on shutdown
    }


    @LmlAction("outputSettings") void outputSettings() {
        outputSettingsDialog.show(getStage());
    }

    @LmlAction("outputSelectAll") void outputSelectAll() {
        outputArea.selectAll();
        outputArea.copy();
    }

    @LmlAction("pickImgPath") void pickImgPath() {
        stage.addActor(fileChooser.fadeIn());
        fileChooser.setSize(Math.min(fileChooser.getWidth(), stage.getWidth()),
                Math.min(fileChooser.getHeight(), stage.getHeight()));
    }

    @LmlAction("clearImg") void clearImg() {
        modelService.getProject().setImage(null);
    }


    private void setImgText(String imgPath) {
        edtImgPath.setText(imgPath);
        btnClearImg.setDisabled(imgPath == null);
    }


    /** Convert HitShapes to text. */
    private void setOutputText() {
        outputBuilder.begin();
        for (HitShape hitShape : modelService.getProject().getHitShapes()) {
            outputBuilder.add(hitShape.getType(), hitShape.getData());
        }

        // catching any preceding newline characters, as those break the textarea when you select them
        String s = GlobalActions.proofString(outputBuilder.end());

        // appending " " because we want to set the same text again but the method would ignore it
        outputArea.setText(s+" ");

        // little hack because the textarea doesn't set it widths properly when you set a text that
        // is wider than the previous width. You have to call draw() and then set the text again so
        // it works
        // TODO: figure out where the actual problem lies
        stage.draw();
        outputArea.setText(s);
    }


    private void projectChanged() {
        String changed = modelService.hasProjectChanged() ? "*" : "";
        Gdx.graphics.setTitle(String.format("%s  |  %s%s", "Easy Hitbox Creator", changed, modelService.getProject().getName()));
    }



}
