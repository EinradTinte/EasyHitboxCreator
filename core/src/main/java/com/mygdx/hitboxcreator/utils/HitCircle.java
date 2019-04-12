package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hitboxcreator.App;

public class HitCircle extends HitShape {
    private float radius;
    private Color cBorder;
    private double angle;
    private int borderArea;
    private final float minRadius = 5;


    private final int top = 1;
    private final int topRight = 2;
    private final int right = 3;
    private final int bottomRight = 4;
    private final int bottom = 5;
    private final int bottomLeft = 6;
    private final int left = 7;
    private final int topLeft = 8;

    public HitCircle(float x, float y, final float radus) {
        this.radius = radus;
        setBounds(x - radius, y - radius, radius * 2, radius * 2);

        initShapeRenderer();

        highlightBorder();
        drawBorder = true;
        grabArea = 6;

        //region --- inputListener ---
        addListener(new HitShapeInputListener() {


            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                selection = 0;
                float dx = radius - x;
                float dy = radius - y;
                double q = Math.sqrt(dx * dx + dy * dy);

                selection = (q <= (radius - grabArea)) ? Selection.move : Selection.border;


                angle = Math.toDegrees(Math.atan2(dx, dy)) + 180;

                if (angle >= 340 || angle < 20) borderArea = top;
                else if (angle >= 20 && angle < 70) borderArea = topRight;
                else if (angle >= 70 && angle < 110) borderArea = right;
                else if (angle >= 110 && angle < 160) borderArea = bottomRight;
                else if (angle >= 160 && angle < 200) borderArea = bottom;
                else if (angle >= 200 && angle < 250) borderArea = bottomLeft;
                else if (angle >= 250 && angle < 290) borderArea = left;
                else if (angle >= 290 && angle < 340) borderArea = topLeft;


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
                if (selection == Selection.border) {
                    float q = (float)Math.sqrt((x-radius)*(x-radius)+(y-radius)*(y-radius));
                    float oldRadius = radius;
                    if (q >= minRadius) {
                        radius = q;
                        setSize(radius * 2, radius * 2);
                        setPosition(getX() + oldRadius, getY() + oldRadius, Align.center);
                        mx -= q - oldRadius;
                        my -= q - oldRadius;
                    }
                    /*
                    // TODO: diagonal resize sets border always on mouse pointer, even if you grabbed slightly beside
                    switch (borderArea) {
                        case right:
                            dx = dx / 2;
                            radius += dx;
                            setBounds(getX(), getY()-dx, radius*2, radius*2);
                            my = dx;
                            break;
                        case left:
                            radius -= dx/2;
                            setBounds(getX()+dx, getY()+dx/2, radius*2, radius*2);
                            my = -dx/2;
                            mx = dx;
                            break;
                        case top:
                            dy = dy / 2;
                            radius += dy;
                            setBounds(getX()-dy, getY(), radius*2, radius*2);
                            mx = dy;
                            break;
                        case bottom:
                            radius -= dy/2;
                            setBounds(getX()+dy/2, getY()+dy, radius*2, radius*2);
                            mx = -dy/2;
                            my = dy;
                            break;
                        case topRight: // TODO: this is not really good
                            //float dq = (float)Math.sqrt(dx * dx + dy * dy);
                            float q = (float)Math.sqrt((x-radius)*(x-radius)+(y-radius)*(y-radius));
                            if (Math.sqrt(x * x + y * y) > Math.sqrt(oldX * oldX + oldY * oldY)) {
                                radius += (q-radius)/2;

                            } else {
                                radius -= (radius-q)/2;
                            }
                            setSize(radius*2, radius*2);
                            //System.out.println(String.format("Rv: %.1f | Rn: %.1f | dq: %.1f | xold: %.2f | x: %.2f | yold: %.2f | y: %.2f",radius-dq/2,radius,dq, oldX, x, oldY, y));
                            break;
                    }
                    */
                }


                lastPos.set(x-mx, y-my);
            }
        });
        //endregion
    }

    @Override
    void highlightBorder() {
        cBody = selection != 0 ? cBodySelected : cBodyNormal;
        cBorder = ((selection & Selection.border) != 0) ? cBorderSelected : cBorderNormal;

        switch (selection) {
            case Selection.move:
                App.inst().setCursor(App.CursorStyle.Move);
                break;
            case Selection.border:
                if (borderArea == top || borderArea == bottom)
                    App.inst().setCursor(App.CursorStyle.VerticalResize);
                else if (borderArea == right || borderArea == left)
                    App.inst().setCursor(App.CursorStyle.HorizontalResize);
                else if (borderArea == topRight || borderArea == bottomLeft)
                    App.inst().setCursor(App.CursorStyle.Diagonal_neResize);
                else if (borderArea == topLeft || borderArea == bottomRight)
                    App.inst().setCursor(App.CursorStyle.Diagonal_nwResize);

                break;
            default:
                App.inst().setCursor(App.CursorStyle.Arrow);
        }
    }

    @Override
    boolean contains(float x, float y) {
        x = radius - x;
        y = radius - y;
        return x * x + y * y <= (radius + grabArea) * (radius + grabArea);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //shapes.set(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(cBody);
        shapes.circle(getX() + radius, getY() + radius, radius);
        /*
        if (drawBorder) {
            shapes.set(ShapeRenderer.ShapeType.Line);
            shapes.setColor(cBorder);
            for (int i = MathUtils.round(-borderWidth/2); i < MathUtils.round(borderWidth/2); i++) {
                shapes.circle(getX() + radius, getY() + radius, radius+i);
            }
        }
        */
    }

    private class Selection {
        static final int move = 1 << 0;
        static final int border = 1 << 1;
    }


}
