package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hitboxcreator.App;

public class HitRectangle extends Rectangle{

    private float lineWidth = 2;
    private float scale = 1;
    private Color cLeft, cRight, cTop, cBottom, cMain;
    private Color cNormal, cSelected;
    private boolean drawBorder = true;
    private boolean isSelected = true;
    private final float grabArea = 10;
    private float oldX, oldY, oldWidth, oldHeight;
    private int selection = 0;
    private ShapeRenderer shapes;


    public InputListener inputListener = new InputListener() {
        float oldX, oldY;

        @Override
        public boolean mouseMoved(InputEvent event, float x, float y) {
            selection = 0;
            if (isSelected) {
                if (x < getX()-grabArea || x > getX()+getWidth()+grabArea || y < getY()-grabArea || y > getY()+getHeight()+grabArea) {
                    highlightBorder();
                    return false;
                }
                if (getX()-grabArea < x && x < getX()+grabArea) selection = Select.left;
                else if (getX()+getWidth()-grabArea < x && x < getX()+getWidth()+grabArea) selection = Select.right;
                if (getY()-grabArea < y && y < getY()+grabArea) selection = selection | Select.bottom;
                else if (getY()+getHeight()-grabArea < y && y < getY()+getHeight()+grabArea) selection = selection | Select.top;
                highlightBorder();
                return true;
            }
            return false;

        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            oldX = x;
            oldY = y;
            return  (button == Input.Buttons.LEFT && selection != 0);

        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            float dx = x - oldX;
            float dy = y - oldY;

            switch (selection) {
                case Select.left:
                    setX(getX() + dx);
                    setWidth(getWidth() - dx);
                    break;
            }

            oldX = x;
            oldY = y;
        }
    };

    private void highlightBorder() {
        cLeft = ((selection & Select.left) != 0) ? cSelected : cNormal;
        cRight = ((selection & Select.right) != 0) ? cSelected : cNormal;
        cBottom = ((selection & Select.bottom) != 0) ? cSelected : cNormal;
        cTop = ((selection & Select.top) != 0) ? cSelected : cNormal;

        if ((selection & Select.top) != 0) Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);

        switch (selection) {
            case Select.left:
            case Select.right:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                break;
            case Select.bottom:
            case Select.top:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                break;
            default:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }


    public HitRectangle() {
        //addListener(Listener)
    }

    public HitRectangle(float x, float y, float width, float height) {
        super(x, y, width, height);

        cMain = new Color(255, 0, 0, 0.5F);


        cNormal = new Color(0,255,255,0.5F);
        cSelected = new Color(0,0,255,0.5F);

        cLeft = cBottom = cRight = cTop = cNormal;

        shapes = App.inst().getShapeRenderer();
    }



    public void draw() {
        scaleIn();

        shapes.setColor(cMain);
        shapes.rect(x, y, width, height);
        if (drawBorder) {
            shapes.setColor(cTop);
            shapes.rectLine(x, y+height, x+width, y+height, lineWidth);
            shapes.setColor(cRight);
            shapes.rectLine(x+width, y+height, x+width, y, lineWidth);
            shapes.setColor(cBottom);
            shapes.rectLine(x+width, y, x, y, lineWidth);
            shapes.setColor(cLeft);
            shapes.rectLine(x, y, x, y+height, lineWidth);
        }
        scaleOut();
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    private void scaleIn() {
        oldX = x;
        oldY = y;
        oldWidth = width;
        oldHeight = height;
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;
    }

    private void scaleOut() {
        x = oldX;
        y = oldY;
        width = oldWidth;
        height = oldHeight;
    }


    private class Select {
        static public final int move = 1 << 0;
        static public final int top = 1 << 1;
        static public final int right = 1 << 2;
        static public final int bottom = 1 << 3;
        static public final int left = 1 << 4;

        static public final int topLeft = top | left;
        static public final int topRight = top | right;
        static public final int bottomLeft = bottom | left;
        static public final int bottomRight = bottom | right;
    }

}
