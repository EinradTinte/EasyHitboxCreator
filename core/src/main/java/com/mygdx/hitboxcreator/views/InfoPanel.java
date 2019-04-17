package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.annotation.OnEvent;
import com.github.czyzby.autumn.processor.event.EventDispatcher;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.DefaultLmlParser;
import com.mygdx.hitboxcreator.events.InfoPropertyChangedEvent;

public class InfoPanel extends Container {



    private final Label lblZoom;
    private final Label lblImgDimens;

    public InfoPanel(LmlParser parser) {
        align(Align.top);
        fillX();

        // Workaround of parser's only single parsing operation limitation
        LmlParser localParser = new DefaultLmlParser(parser.getData());
        localParser.setSyntax(parser.getSyntax());
        Group root = (Group) (localParser.parseTemplate(Gdx.files.internal("lml/canvasInfoPanel.lml")).first());
        setActor(root);

        lblZoom = root.findActor("lblZoom");
        lblImgDimens = root.findActor("lblImgDimens");
    }




    public void setZoomLevel(float zoom) {
        lblZoom.setText(String.format("%.0f%%", zoom));
    }


    public void setImgDimens(String imgPath) {
        if (imgPath == null) {
            lblImgDimens.setText("NaN");
        } else try {
            Texture texture = new Texture(imgPath);
            lblImgDimens.setText(texture.getWidth() + "x" + texture.getHeight());
            texture.dispose();
        } catch (Exception e) {
            lblImgDimens.setText("Can't load file.");
            throw new IllegalArgumentException("Can't load image file at "+imgPath);
        }

    }
}
