package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.mygdx.hitboxcreator.App;

public class HitRectangle extends HitShape {
    private Color cLeft, cRight, cBottom, cTop;

    public HitRectangle(float x, float y, float width, float height) {
        setBounds(x, y, width, height);

        initShapeRenderer();

        highlightBorder();
        drawBorder = true;
        grabArea = 6;

        //region --- inputListener ---
        addListener(new HitShapeInputListener() {


            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                selection = 0;

                if (x > grabArea && x < getWidth()-grabArea && y > grabArea && y < getHeight()-grabArea) {
                    selection = Selection.move;
                    highlightBorder();
                    return true;
                }
                if (-grabArea < x && x < grabArea) selection = Selection.left;
                else if (getWidth()-grabArea < x && x < getWidth()+grabArea) selection = Selection.right;
                if (-grabArea < y && y < grabArea) selection = selection | Selection.bottom;
                else if (getHeight()-grabArea < y && y < getHeight()+grabArea) selection = selection | Selection.top;
                highlightBorder();
                return true;
            }



            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                float dx = x - lastPos.x;
                float dy = y - lastPos.y;
                // when the object moves we have to take this into account for next dx/dy
                float mx = 0, my = 0;

                if ((selection & Selection.move) != 0) {
                    moveBy(dx, dy);
                    mx = dx;
                    my = dy;
                }
                if ((selection & Selection.left) != 0 && getWidth() - dx >= 2) {
                    moveBy(dx, 0);
                    setWidth(getWidth() - dx);
                    mx = dx;
                }
                if ((selection & Selection.right) != 0 && getWidth() + dx >= 2) {
                    setWidth(getWidth() + dx);
                }
                if ((selection & Selection.top) != 0 && getHeight() + dy >= 2) {
                    setHeight(getHeight() + dy);
                }
                if ((selection & Selection.bottom) != 0 && getHeight() - dy >= 2) {
                    moveBy(0, dy);
                    setHeight(getHeight() - dy);
                    my = dy;
                }


                lastPos.set(x-mx, y-my);
            }
        });
        //endregion
    }




    @Override
    boolean contains(float x, float y) {
        return x >= -grabArea && x < getWidth()+grabArea && y >= -grabArea && y < getHeight()+grabArea;
    }

    @Override
    void highlightBorder() {
        cLeft = ((selection & Selection.left) != 0) ? cBorderSelected : cBorderNormal;
        cRight = ((selection & Selection.right) != 0) ? cBorderSelected : cBorderNormal;
        cBottom = ((selection & Selection.bottom) != 0) ? cBorderSelected : cBorderNormal;
        cTop = ((selection & Selection.top) != 0) ? cBorderSelected : cBorderNormal;

        cBody = selection != 0 ? cBodySelected : cBodyNormal;

        switch (selection) {
            case Selection.move:
                App.inst().setCursor(App.CursorStyle.Move);
                break;
            case Selection.left:
            case Selection.right:
                App.inst().setCursor(App.CursorStyle.HorizontalResize);
                break;
            case Selection.bottom:
            case Selection.top:
                App.inst().setCursor(App.CursorStyle.VerticalResize);
                break;
            case Selection.topRight:
            case Selection.bottomLeft:
                App.inst().setCursor(App.CursorStyle.Diagonal_neResize);
                break;
            case Selection.topLeft:
            case Selection.bottomRight:
                App.inst().setCursor(App.CursorStyle.Diagonal_nwResize);
                break;
            default:
                App.inst().setCursor(App.CursorStyle.Arrow);
        }
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        //shapes.set(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(cBody);
        shapes.rect(getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        if (drawBorder) {
            shapes.setColor(cTop);
            shapes.rectLine(getX(), getTop(), getRight(), getTop(), borderWidth);
            shapes.setColor(cRight);
            shapes.rectLine(getRight(), getTop(), getRight(), getY(), borderWidth);
            shapes.setColor(cBottom);
            shapes.rectLine(getRight(), getY(), getX(), getY(), borderWidth);
            shapes.setColor(cLeft);
            shapes.rectLine(getX(), getY(), getX(), getTop(), borderWidth);
        }
    }



    private class Selection {
        static final int move = 1 << 0;
        static final int top = 1 << 1;
        static final int right = 1 << 2;
        static final int bottom = 1 << 3;
        static final int left = 1 << 4;

        static final int topRight = top | right;
        static final int topLeft = top | left;
        static final int bottomRight = bottom | right;
        static final int bottomLeft = bottom | left;
    }
}
