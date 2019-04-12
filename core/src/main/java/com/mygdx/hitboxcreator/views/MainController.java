package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;

public class MainController {

    FileChooser fileChooser;
    FileTypeFilter typeFilterImg;

    public MainController() {

        typeFilterImg = new FileTypeFilter(false);
        typeFilterImg.addRule("Image files (*.png, *.jpg)", "png", "jpg");

        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        fileChooser.setFileTypeFilter(typeFilterImg);
        fileChooser.setListener(new SingleFileChooserListener() {
            @Override
            protected void selected(FileHandle fileHandle) {

            }
        });
    }
}
