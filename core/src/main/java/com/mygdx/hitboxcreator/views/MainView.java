package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.impl.AbstractLmlView;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.contrib.widget.file.ImgScalrFileChooserIconProvider;
import com.kotcrab.vis.ui.contrib.widget.file.WindowsFileChooserIconProvider;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import com.mygdx.hitboxcreator.App;


public class MainView extends AbstractLmlView {

    @LmlActor("btnOutputSettings") VisImageButton btnOutputSettings;
    @LmlActor("btnOutputSelectAll") VisImageButton btnOutputSelectAll;
    @LmlActor("edtImgPath") VisTextField edtImgPath;

    FileChooser fileChooser;
    FileTypeFilter typeFilterImg;
    FileChooser.FileIconProvider fileIconProvider;


    public MainView() {
        super(App.inst().getStage());
        //getStage().setDebugAll(true);

        initiateFileChooser();

    }

    @Override
    public FileHandle getTemplateFile() {
        return Gdx.files.internal("lml/main.lml");
    }

    @Override
    public String getViewId() {
        return "main";
    }

    @LmlAction("outputSettings") void outputSettings() {

    }

    @LmlAction("outputSelectAll") void outputSelectAll() {

    }

    @LmlAction("pickImgPath") void pickImgPath() {
        getStage().addActor(fileChooser.fadeIn());
    }

    public void initiateFileChooser() {
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
                edtImgPath.setText(fileHandle.file().getAbsolutePath());
            }
        });

        // TODO: dispose IconProvider on shutdown
    }

}
