package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class HitRectangle2 extends HitShape {
    private Color cLeft, cRight, cBottom, cTop;

    public HitRectangle2(float x, float y, float width, float height) {
        setBounds(x, y, width, height);
        initShapeRenderer();

        cBody = new Color(255, 0, 0, 0.5F);


        cNormal = new Color(0,0,0,0.5F);
        cSelected = new Color(0,0,255,1F);

        cLeft = cBottom = cRight = cTop = cNormal;
        lineWidth = 2;
        drawBorder = true;
        grabArea = 6;

        //region --- inputListener ---
        addListener(new InputListener() {
            float oldX, oldY;

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                selection = 0;
                if (isSelected) {
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
                return false;

            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                selection = 0;
                highlightBorder();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                oldX = x;
                oldY = y;
                if (button == Input.Buttons.LEFT) {
                    isSelected = true;
                    mouseMoved(null, x, y);
                    toFront();
                    return true;
                }
                else return false;

            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                float dx = x - oldX;
                float dy = y - oldY;
                // when the object moves we have to take this into count for next dx/dy
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


                oldX = x-mx;
                oldY = y-my;
            }
        });
        //endregion
    }

    // this is exactly like super.hit() only I define my own "Hitzone"
    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) return null;
        if (!isVisible()) return null;
        float border = isSelected ? grabArea : 0;
        return x >= -border && x < getWidth()+border && y >= -border && y < getHeight()+border ? this : null;
    }

    @Override
    void highlightBorder() {
        cLeft = ((selection & Selection.left) != 0) ? cSelected : cNormal;
        cRight = ((selection & Selection.right) != 0) ? cSelected : cNormal;
        cBottom = ((selection & Selection.bottom) != 0) ? cSelected : cNormal;
        cTop = ((selection & Selection.top) != 0) ? cSelected : cNormal;



        switch (selection) {
            case Selection.move:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                break;
            case Selection.left:
            case Selection.right:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                break;
            case Selection.bottom:
            case Selection.top:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                break;
            default:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
    }

    @Override
    void setColors() {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        shapes.setColor(cBody);
        shapes.rect(getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        if (drawBorder) {
            shapes.setColor(cTop);
            shapes.rectLine(getX(), getTop(), getRight(), getTop(), lineWidth);
            shapes.setColor(cRight);
            shapes.rectLine(getRight(), getTop(), getRight(), getY(), lineWidth);
            shapes.setColor(cBottom);
            shapes.rectLine(getRight(), getY(), getX(), getY(), lineWidth);
            shapes.setColor(cLeft);
            shapes.rectLine(getX(), getY(), getX(), getTop(), lineWidth);
        }
    }

    @Override
    void drawShape() {

    }

    private class Selection {
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
