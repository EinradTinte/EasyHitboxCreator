package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.graphics.Texture;
import com.github.czyzby.autumn.processor.event.EventDispatcher;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.events.Event;
import com.mygdx.hitboxcreator.events.EventListener;
import com.mygdx.hitboxcreator.events.InfoPropertyChangedEvent;
import com.mygdx.hitboxcreator.events.ProjectPropertyChangedEvent;
import com.mygdx.hitboxcreator.events.ProjectSerializerEvent;
import com.mygdx.hitboxcreator.utils.ModelService;


public class EditorController {

    private Editor editor;
    private ScaleGroup2 scaleGroup;
    private CanvasHolder canvasHolder;
    private InfoPanel infoPanel;

    public EditorController(Editor editor) {
        this.editor = editor;
        infoPanel = editor.getInfoPanel();
        canvasHolder = editor.getCanvasHolder();
        scaleGroup = canvasHolder.getScaleGroup();

        initEventListener();
    }


    private void initEventListener() {
        App.inst().getEventDispatcher().addEventListener(new EventListener() {
            @Override
            public void receiveEvent(Event event) {
                if (event.is(ProjectPropertyChangedEvent.class)) {
                    switch (((ProjectPropertyChangedEvent) event).getProperty()) {
                        case IMG:
                            String imgPath = ((ProjectPropertyChangedEvent) event).getProject().getImage();
                            infoPanel.setImgDimens(imgPath);
                            scaleGroup.reloadImage(imgPath);
                            canvasHolder.setZoomIndex(CanvasHolder.DEFAULT_ZOOM_INDEX);
                            break;
                    }
                }
            }
        });
    }

    public void initialize(Editor editor) {
        this.editor = editor;



        //editor.reloadProject(modelService.getProject());
    }


}
