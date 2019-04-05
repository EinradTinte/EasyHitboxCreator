package com.mygdx.hitboxcreator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.hitboxcreator.views.Editor;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class App extends ApplicationAdapter {
    private static App instance;
    private final float WIDTH = 600, HEIGHT = 400;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private Editor editor;

    private Stage stage;
    private Table table;

    private BitmapFont font;
    private Pixmap pmCMove, pmCResize_ne, pmcResize_nw;
    private Cursor cMove, cResize_ne, cResize_nw;

    /** Singleton accessor */
    public static App inst() {
        if (instance == null) {
            throw new NullPointerException("App is not initialized yet");
        }
        return instance;
    }

    public App() {
        instance = this;
    }

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(WIDTH, HEIGHT), batch);
        table = new Table();
        table.setFillParent(true);

        font = new BitmapFont();
        font.setColor(Color.MAGENTA);
        // Loading custom Cursors
        pmCMove = new Pixmap(Gdx.files.internal("cursor/move.png"));
        pmCResize_ne = new Pixmap(Gdx.files.internal("cursor/resize-ne.png"));
        pmcResize_nw = new Pixmap(Gdx.files.internal("cursor/resize-nw.png"));
        cMove = Gdx.graphics.newCursor(pmCMove, 17, 17);
        cResize_ne = Gdx.graphics.newCursor(pmCResize_ne, 9, 9);
        cResize_nw = Gdx.graphics.newCursor(pmcResize_nw, 9, 9);
        pmCMove.dispose();
        pmcResize_nw.dispose();
        pmCResize_ne.dispose();

        Gdx.input.setInputProcessor(stage);

        editor = new Editor();
        stage.addActor(editor);
        //stage.setDebugAll(true);
    }

    public Batch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() {return shapeRenderer;}

    public Stage getStage() {
        return stage;
    }

    public void setCursor(int cursor) {
        switch (cursor) {
            case CursorStyle.HorizontalResize:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.HorizontalResize);
                break;
            case CursorStyle.VerticalResize:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.VerticalResize);
                break;
            case CursorStyle.Hand:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                break;
            case CursorStyle.Crosshair:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Crosshair);
                break;
            case CursorStyle.Arrow:
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                break;
            case CursorStyle.Move:
                Gdx.graphics.setCursor(cMove);
                break;
            case CursorStyle.Diagonal_neResize:
                Gdx.graphics.setCursor(cResize_ne);
                break;
            case CursorStyle.Diagonal_nwResize:
                Gdx.graphics.setCursor(cResize_nw);
                break;
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
            font.draw(batch, Gdx.graphics.getFramesPerSecond()+" FPS", 10, 20);
        batch.end();
        //editor.draw(batch);
        stage.act();
        stage.draw();

    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
    }

    static public class CursorStyle {
        static public final int HorizontalResize = 1;
        static public final int VerticalResize = 2;
        static public final int Diagonal_neResize = 3;
        static public final int Diagonal_nwResize = 4;
        static public final int Move = 5;
        static public final int Crosshair = 6;
        static public final int Arrow = 7;
        static public final int Hand = 8;
    }
}