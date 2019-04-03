package com.mygdx.hitboxcreator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

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
}