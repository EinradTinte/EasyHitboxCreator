package com.einradtinte.hitboxcreator.hitshapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.services.OutputFormat;
import com.einradtinte.hitboxcreator.statehash.StateHashUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class HitCircle extends HitShape {
    private float radius;
    private Color cBorder;
    private PolygonSprite spBody, spBorder;
    private double angle;
    private int borderArea;
    private final float minRadius = 5;

    // for selection detection
    private Circle tmpCircle = new Circle();

    // Order in which attributes get passed
    public static ArrayList<String> attributes = new ArrayList<>(Arrays.asList("X", "Y", "RADIUS"));


    private final int top = 1;
    private final int topRight = 2;
    private final int right = 3;
    private final int bottomRight = 4;
    private final int bottom = 5;
    private final int bottomLeft = 6;
    private final int left = 7;
    private final int topLeft = 8;


    public HitCircle() {}

    public HitCircle(float x, float y, final float radus) {
        this.radius = radus;
        setBounds(x - radius, y - radius, radius * 2, radius * 2);
        init();
    }


    @Override
    public void write(Json json) {
        json.writeValue("x", getX());
        json.writeValue("y", getY());
        json.writeValue("radius", radius);
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        setX(jsonValue.getFloat("x"));
        setY(jsonValue.getFloat("y"));
        radius = jsonValue.getFloat("radius");

        init();
    }



    @Override
    boolean mouseMoved(float x, float y) {
        selection = 0;
        float dx = radius - x;
        float dy = radius - y;
        double q = Math.sqrt(dx * dx + dy * dy);

        selection = (isSelected || (q <= (radius - grabArea))) ? Selection.move : Selection.border;


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
    void touchDragged(Vector2 lastXY, float dxp, float dyp) {
        if ((selection & Selection.move) != 0) {
            moveBy(dxp, dyp);
        }
        if (selection == Selection.border) {
            float r = Math.round(Math.sqrt(((lastXY.x+dxp) - radius) * ((lastXY.x+dxp) - radius) + ((lastXY.y+dyp) - radius) * ((lastXY.y+dyp) - radius)));
            float oldRadius = radius;
            if (r >= minRadius) {
                radius = r;
                setSize(radius * 2, radius * 2);
                setPosition(getX() + oldRadius, getY() + oldRadius, Align.center);
            }
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

    @Override
    public OutputFormat.Type getType() {
        return OutputFormat.Type.CIRCLE;
    }

    @Override
    public int computeStateHash() {
        return StateHashUtils.computeHash(getX(), getY(), radius);
    }

    @Override
    void highlightBorder() {
        cBody = isSelected ? cSelected : selection != 0 ? cBodySelected : cBodyNormal;
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
    public boolean contains(Rectangle rec) {
        tmpCircle.set(getX() + radius, getY() + radius, radius);
        return Intersector.overlaps(tmpCircle, rec);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        spBody.setColor(cBody);
        spBody.draw((PolygonSpriteBatch) batch);
        if (drawBorder) {
            spBorder.setColor(cBorder);
            spBorder.draw((PolygonSpriteBatch) batch);
        }
    }

    @Override
    public float[] getData() {
        return new float[] {getX() + radius, getY() + radius, radius};
    }

    @Override
    public void somethingChanged() {
        super.somethingChanged();
        spBody = prepareSprite(drawCircle(getX() + radius, getY() + radius, radius));
        if (borderWidth != 1) {
            spBorder = prepareSprite(drawRing(getX() + radius, getY() + radius, radius, borderWidth));
        } else {
            spBorder = prepareSprite(drawRing(getX() + radius, getY() + radius, radius-0.5f, borderWidth));
        }
    }

    private PolygonSprite prepareSprite(PolygonRegion polygonRegion) {
        return new PolygonSprite(polygonRegion);
    }



    /** Creates a PolygonRegion of a ring with its width centered at radius */
    private PolygonRegion drawRing(float x, float y, float radius, float width) {
        int segmentCount = calculateSegmentCount(radius);
        float[] vertices = new float[(segmentCount*2 +2)* NUM_COMPONENTS];
        short[] triangles = new short[(segmentCount*2)*3];
        float segmentWidth = MathUtils.PI2 / segmentCount;
        float angle = 0;
        float radiusInner = radius - width/2;
        float radiusOuter = radius + width/2;
        float sin, cos;
        setVertex(vertices, 0, x, y + radiusInner);
        setVertex(vertices, 1, x, y + radiusOuter);

        for (int i = 0; i < segmentCount; i++) {
            angle += segmentWidth;
            sin = (float)Math.sin(angle);
            cos = (float)Math.cos(angle);
            setVertex(vertices, i*2 +2, x + sin * radiusInner, y + cos * radiusInner);
            setVertex(vertices, i*2 +3, x + sin * radiusOuter, y + cos * radiusOuter);
            //TODO: can I shorten this (maybe into one array per triangle) ??
            triangles[i*6] = (short)(i*2);
            triangles[i*6 +1] = (short)(i*2 +1);
            triangles[i*6 +2] = (short)(i*2 +2);
            triangles[i*6 +3] = (short)(i*2 +1);
            triangles[i*6 +4] = (short)(i*2 +3);
            triangles[i*6 +5] = (short)(i*2 +2);
        }

        return new PolygonRegion(region, vertices, triangles);
    }



    /**
     * Creates a PolygonRegion of a Circle consisting of indexed triangles.
     * @param x
     * @param y
     * @param radius
     * @return
     */
    private PolygonRegion drawCircle(float x, float y, float radius) {
        int segmentCount = calculateSegmentCount(radius);
        float[] vertices = new float[(segmentCount + 2)* NUM_COMPONENTS];
        short[] triangles = new short[segmentCount*3];
        float segmentWidth = MathUtils.PI2 / segmentCount;
        float angle = 0;
        setVertex(vertices, 0, x, y);
        setVertex(vertices, 1, x, y + radius);
        for(int i = 0; i < segmentCount; i++) {
            angle += segmentWidth;
            setVertex(vertices, i +2, x + (float)Math.sin(angle) * radius, y + (float)Math.cos(angle) * radius);
            triangles[i*3] = 0;
            triangles[i*3 +1] = (short)(i+1);
            triangles[i*3 +2] = (short)(i+2);
        }

        return new PolygonRegion(region, vertices, triangles);
    }

    /** Estimating the number of segments needed for a smooth circle */
    private int calculateSegmentCount(float radius) {
        if (getParent() == null) return 40;
        else return (int)(8 * (float)Math.cbrt(radius * getParent().getScaleX()));
    }

    private class Selection {
        static final int move = 1 << 0;
        static final int border = 1 << 1;
    }


}
