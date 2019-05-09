package com.einradtinte.hitboxcreator.hitshapes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.einradtinte.hitboxcreator.App;
import com.einradtinte.hitboxcreator.services.OutputFormat;
import com.einradtinte.hitboxcreator.statehash.StateHashUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class HitRectangle extends HitShape {
    private Color cLeft, cRight, cBottom, cTop;
    private PolygonSprite spBody, spLeft, spRight, spTop, spBottom;

    private Vector2 tmpV = new Vector2();
    private short[] triangles = {0,1,2, 0,2,3};

    private float minWidth = 2, minHeight = 2;

    // for selection detection
    private Rectangle tmpRec = new Rectangle();

    // Order in which attributes get passed
    public static ArrayList<String> attributes = new ArrayList<>(Arrays.asList("X", "Y", "WIDTH", "HEIGHT"));


    public HitRectangle() {}

    public HitRectangle(float x, float y, float width, float height) {
        setBounds(x, y, width, height);
        init();
    }

    @Override
    public void write(Json json) {
        json.writeValue("x", getX());
        json.writeValue("y", getY());
        json.writeValue("width", getWidth());
        json.writeValue("height", getHeight());
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        setX(jsonValue.getFloat("x"));
        setY(jsonValue.getFloat("y"));
        setWidth(jsonValue.getFloat("width"));
        setHeight(jsonValue.getFloat("height"));

        init();
    }


    @Override
    boolean mouseMoved(float x, float y) {
        selection = 0;

        if (isSelected || (x > grabArea && x < getWidth()-grabArea && y > grabArea && y < getHeight()-grabArea)) {
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
    void touchDragged(Vector2 lastXY, float dxp, float dyp) {
        if ((selection & Selection.move) != 0) {
            moveBy(dxp, dyp);
        }
        if ((selection & Selection.left) != 0 && getWidth() - dxp >= minWidth) {
            moveBy(dxp, 0);
            setWidth(getWidth() - dxp);
        }
        if ((selection & Selection.right) != 0 && getWidth() + dxp >= minWidth) {
            setWidth(getWidth() + dxp);
        }
        if ((selection & Selection.top) != 0 && getHeight() + dyp >= minHeight) {
            setHeight(getHeight() + dyp);
        }
        if ((selection & Selection.bottom) != 0 && getHeight() - dyp >= minHeight) {
            moveBy(0, dyp);
            setHeight(getHeight() - dyp);
        }
    }

    @Override
    public OutputFormat.Type getType() {
        return OutputFormat.Type.RECTANGLE;
    }

    @Override
    boolean contains(float x, float y) {
        return x >= -grabArea && x < getWidth()+grabArea && y >= -grabArea && y < getHeight()+grabArea;
    }

    @Override
    public boolean contains(Rectangle rec) {
        tmpRec.set(getX(), getY(), getWidth(), getHeight());
        return tmpRec.overlaps(rec);
    }

    @Override
    public int computeStateHash() {
        return StateHashUtils.computeHash(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    void highlightBorder() {
        cLeft = ((selection & Selection.left) != 0) ? cBorderSelected : cBorderNormal;
        cRight = ((selection & Selection.right) != 0) ? cBorderSelected : cBorderNormal;
        cBottom = ((selection & Selection.bottom) != 0) ? cBorderSelected : cBorderNormal;
        cTop = ((selection & Selection.top) != 0) ? cBorderSelected : cBorderNormal;


        cBody = isSelected ? cSelected : selection != 0 ? cBodySelected : cBodyNormal;

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
    public float[] getData() {
        return new float[] {getX(), getY(), getWidth(), getHeight()};
    }

    @Override
    public void somethingChanged() {

        spBody = prepareSprite(drawRect(getX(), getY(), getWidth(), getHeight()), triangles);
        if (borderWidth != 1) {
            spLeft = prepareSprite(drawRectLine(getX(), getY(), getX(), getTop(), borderWidth), triangles);
            spTop = prepareSprite(drawRectLine(getX(), getTop(), getRight(), getTop(), borderWidth), triangles);
            spRight = prepareSprite(drawRectLine(getRight(), getTop(), getRight(), getY(), borderWidth), triangles);
            spBottom = prepareSprite(drawRectLine(getX(), getY(), getRight(), getY(), borderWidth), triangles);
        } else {
            // when borderwidth = 1 the border should lay exactly on the edges. But by how the border meshes are created
            // we have to adjust the input values
            spLeft = prepareSprite(drawRectLine(getX()+0.5f, getY(), getX()+0.5f, getTop(), borderWidth), triangles);
            spTop = prepareSprite(drawRectLine(getX(), getTop()-0.5f, getRight(), getTop()-0.5f, borderWidth), triangles);
            spRight = prepareSprite(drawRectLine(getRight()-0.5f, getTop(), getRight()-0.5f, getY(), borderWidth), triangles);
            spBottom = prepareSprite(drawRectLine(getX(), getY()+0.5f, getRight(), getY()+0.5f, borderWidth), triangles);
        }
        super.somethingChanged();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        spBody.setColor(cBody);
        spBody.draw((PolygonSpriteBatch) batch);
        if (drawBorder) {
            spLeft.setColor(cLeft);
            spLeft.draw((PolygonSpriteBatch) batch);
            spTop.setColor(cTop);
            spTop.draw((PolygonSpriteBatch) batch);
            spRight.setColor(cRight);
            spRight.draw((PolygonSpriteBatch) batch);
            spBottom.setColor(cBottom);
            spBottom.draw((PolygonSpriteBatch) batch);
        }

    }

    /** Creating a PolygonSprite out of Triangles.
     *
     * @param vertices
     * @param triangles
     * @return
     */
    private PolygonSprite prepareSprite(float[] vertices, short[] triangles) {
        PolygonRegion polyReg = new PolygonRegion(region, vertices, triangles);
        return new PolygonSprite(polyReg);
    }

    /** Draws a rotated rectangle, where one edge is centered at x1, y1 and the opposite edge centered at x2, y2. */
    private float[] drawRectLine(float x1, float y1, float x2, float y2, float width) {
        float[] vertices = new float[4*HitShape.NUM_COMPONENTS];
            width *= 0.5f;
            Vector2 t = tmpV.set(y2 - y1, x1 - x2).nor();
            float tx = t.x * width;
            float ty = t.y * width;
            setVertex(vertices, 0, x1 + tx, y1 + ty);
            setVertex(vertices, 1, x1 - tx, y1 - ty);
            setVertex(vertices, 2, x2 - tx, y2 - ty);
            setVertex(vertices, 3, x2 + tx, y2 + ty);
            return vertices;
    }

    private float[] drawRect(float x, float y, float width, float height) {
        float[] vertices = new float[4*HitShape.NUM_COMPONENTS];
        setVertex(vertices, 0, x, y);
        setVertex(vertices, 1, x, y + height);
        setVertex(vertices, 2, x + width, y + height);
        setVertex(vertices, 3, x + width, y);
        return vertices;
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
