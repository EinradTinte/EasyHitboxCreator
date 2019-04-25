package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.DefaultLmlParser;
import com.mygdx.hitboxcreator.hitshapes.HitCircle;
import com.mygdx.hitboxcreator.hitshapes.HitRectangle;
import com.mygdx.hitboxcreator.hitshapes.HitShape;

public class InfoPanel extends Container {



    private final Label lblZoom;
    private final Label lblImgDimens;
    private final Label lblRecCount;
    private final Label lblCirCount;

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
        lblRecCount = root.findActor("lblRecCount");
        lblCirCount = root.findActor("lblCirCount");
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

    public void setHitShapeCount(Array<HitShape> hitshapes) {
        int rectangleCount = 0, circleCount = 0;
        for (HitShape hitShape : hitshapes) {
            if (hitShape instanceof HitRectangle) rectangleCount++;
            else if (hitShape instanceof HitCircle) circleCount++;
        }
        lblRecCount.setText(""+rectangleCount);
        lblCirCount.setText(""+circleCount);
    }
}
