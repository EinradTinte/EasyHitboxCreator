package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.annotation.LmlInject;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.contrib.widget.file.ImgScalrFileChooserIconProvider;
import com.kotcrab.vis.ui.contrib.widget.file.WindowsFileChooserIconProvider;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.ScrollableTextArea;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.controller.EditorController;
import com.mygdx.hitboxcreator.controller.OutputSettingsDialogController;
import com.mygdx.hitboxcreator.events.Event;
import com.mygdx.hitboxcreator.events.EventDispatcher;
import com.mygdx.hitboxcreator.events.EventListener;
import com.mygdx.hitboxcreator.events.HitShapesChangedEvent;
import com.mygdx.hitboxcreator.events.ProjectPropertyChangedEvent;
import com.mygdx.hitboxcreator.utils.GlobalActions;
import com.mygdx.hitboxcreator.utils.HitShape;
import com.mygdx.hitboxcreator.services.ModelService;
import com.mygdx.hitboxcreator.utils.OutputBuilder;
import com.mygdx.hitboxcreator.utils.OutputSettingsDialog;
import com.mygdx.hitboxcreator.utils.ProjectModel;


public class MainView extends AbstractLmlView {

    private ModelService modelService;
    private EventDispatcher eventDispatcher;
    private EditorController editorController;
    private OutputBuilder outputBuilder;
    private Stage stage;




    //@LmlInject OutputSettingsDialogController outputSettingsDialogController;

    @LmlActor("btnOutputSettings") VisImageButton btnOutputSettings;
    @LmlActor("btnOutputSelectAll") VisImageButton btnOutputSelectAll;
    @LmlActor("edtImgPath") VisTextField edtImgPath;
    @LmlActor("editor") Editor editor;
    @LmlActor("outputTextArea") ScrollableTextArea outputArea;
    @LmlActor("btnClearImg") VisImageButton btnClearImg;

    //@LmlActor("outputSettingsDialog") private VisDialog outputSettingsDialog;


    private OutputSettingsDialog outputSettingsDialog;


    private FileChooser fileChooser;
    private FileTypeFilter typeFilterImg;







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


    public void initialize() {
        stage = getStage();
        modelService = App.inst().getModelService();
        eventDispatcher = App.inst().getEventDispatcher();
        outputBuilder = App.inst().getOutputFormatService().getOutputBuilder();
        initiateFileChooser();
        initEventListener();
        initOutputSettingsDialog();
        editorController = new EditorController(editor);

        // load model here because Editor gets first initiated here and can't act on project loaded events before that
        modelService.setProject(new ProjectModel());

        //outputArea.setProgrammaticChangeEvents(false);
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

    @LmlAction("updateOutputSettings") void updateOutputSettings() {

    }

    @LmlAction("resetViewFocus") public void resetViewFocus() {
        FocusManager.resetFocus(stage);
    }



    private void initEventListener() {
        eventDispatcher.addEventListener(new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                if (event.is(HitShapesChangedEvent.class)) {
                    setOutputText();
                } else if (event.is(ProjectPropertyChangedEvent.class)) {
                    switch (((ProjectPropertyChangedEvent) event).getProperty()) {
                        case IMG:
                            setImgText(((ProjectPropertyChangedEvent) event).getProject().getImage());
                            break;
                    }
                }
            }
        });
    }


    private void setImgText(String imgPath) {
        edtImgPath.setText(imgPath);
        btnClearImg.setDisabled(imgPath == null);
    }


    private void initOutputSettingsDialog() {
        //outputSettingsDialog = (VisDialog) App.inst().getParser().getActorsMappedByIds().get("outputSettingsDialog");

        outputSettingsDialog = new OutputSettingsDialog();

        //outputSettingsDialog = (VisDialog) App.inst().getParser().parseTemplate(Gdx.files.internal("lml/outputSettingsDialog.lml")).first();



    }


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


    private void initiateFileChooser() {
        typeFilterImg = new FileTypeFilter(false);
        typeFilterImg.addRule("Image files (*.png, *.jpg)", "png", "jpg");



        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
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
                modelService.getProject().setImage(imgPath);
            }
        });

        // TODO: dispose IconProvider on shutdown
    }



    @Override
    public void dispose() {
        super.dispose();


    }




}
