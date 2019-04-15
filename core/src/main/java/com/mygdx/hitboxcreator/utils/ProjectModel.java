package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ProjectModel{

    private final Array<HitRectangle> recs = new Array<HitRectangle>();
    private final Array<HitCircle> circles = new Array<HitCircle>();
    private FileHandle projectFile;
    private String imgPath;

    private Color cBodyNormal;
    static Color cBodySelected;
    static Color cBorderNormal;
    static Color cBorderSelected;


    public ProjectModel() {
    }



    public void setImage(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImage() { return imgPath;}

    public void addHitShape(HitShape hitShape) {
        if (hitShape.getClass() == HitRectangle.class) {
            recs.add((HitRectangle) hitShape);
        } else if (hitShape.getClass() == HitCircle.class) {
            circles.add((HitCircle) hitShape);
        }
    }



    public void removeHitShape(HitShape hitShape) {
        if (hitShape.getClass() == HitRectangle.class) {
            recs.removeValue((HitRectangle) hitShape, true);
        } else if (hitShape.getClass() == HitCircle.class) {
            circles.removeValue((HitCircle) hitShape, true);
        }
    }

    public HitShape[] getHitShapes() {
        return recs.items;
    }

}
