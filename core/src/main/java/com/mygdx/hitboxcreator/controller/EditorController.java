package com.mygdx.hitboxcreator.controller;

import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.events.Event;
import com.mygdx.hitboxcreator.events.EventListener;
import com.mygdx.hitboxcreator.events.HitShapesChangedEvent;
import com.mygdx.hitboxcreator.events.ProjectPropertyChangedEvent;
import com.mygdx.hitboxcreator.events.ProjectSerializerEvent;
import com.mygdx.hitboxcreator.utils.HitCircle;
import com.mygdx.hitboxcreator.utils.HitRectangle;
import com.mygdx.hitboxcreator.utils.ProjectModel;
import com.mygdx.hitboxcreator.views.CanvasHolder;
import com.mygdx.hitboxcreator.views.Editor;
import com.mygdx.hitboxcreator.views.InfoPanel;
import com.mygdx.hitboxcreator.views.ScaleGroup;


public class EditorController {

    private Editor editor;
    private ScaleGroup scaleGroup;
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
                } else if (event.is(ProjectSerializerEvent.class)) {
                    switch (((ProjectSerializerEvent) event).getAction()) {
                        case LOADED:
                            ProjectModel project = ((ProjectSerializerEvent) event).getProject();
                            scaleGroup.reloadProject(project);
                            infoPanel.setImgDimens(project.getImage());
                            infoPanel.setHitShapeCount(project.getHitShapes());
                            canvasHolder.setZoomIndex(CanvasHolder.DEFAULT_ZOOM_INDEX);

                            //scaleGroup.addHitShape(new HitCircle(100,200,30));
                            //scaleGroup.addHitShape(new HitRectangle(100, 100, 100, 200));
                            //scaleGroup.addHitShape(new HitCircle(400,100,50));
                            break;
                    }
                } else if (event.is(HitShapesChangedEvent.class)) {
                    switch (((HitShapesChangedEvent) event).getAction()) {
                        case QUANTITY_CHANGED:
                            infoPanel.setHitShapeCount(App.inst().getModelService().getProject().getHitShapes());
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
