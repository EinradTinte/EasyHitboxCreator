package com.mygdx.hitboxcreator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.util.LmlApplicationListener;
import com.github.czyzby.lml.vis.util.VisLml;
import com.kotcrab.vis.ui.VisUI;
import com.mygdx.hitboxcreator.lml.AppLmlSyntax;
import com.mygdx.hitboxcreator.views.MainView;
import com.mygdx.hitboxcreator.views.Shader;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class App extends LmlApplicationListener {
    private static App instance;
    public static final int WIDTH = 900, HEIGHT = 400;

    private ShapeRenderer shapeRenderer;
    private PolygonSpriteBatch batch;
    private Viewport viewport;



    private Stage stage;

    private  Skin skin;

    private Table table;

    private BitmapFont font;
    private Cursor cMove, cResize_ne, cResize_nw;

    private Shader shader;






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

        shader = new Shader();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        batch = new PolygonSpriteBatch();
        viewport = new ScreenViewport();
        stage = new Stage(viewport, batch) {
            @Override
            public void draw() {
                super.draw();

                shader.setProjectionMatrix(getCamera().combined);
                shader.flush();
            }
        };



        font = new BitmapFont();
        font.setColor(Color.MAGENTA);
        // Loading custom Cursors
        cMove = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor/move.png")), 17, 17);
        cResize_ne = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor/resize-ne.png")), 9, 9);
        cResize_nw = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor/resize-nw.png")), 9, 9);




        //table.add(editor).align(Align.topRight).fillX();
        //stage.addActor(table);
        //stage.addActor(editor);
        //stage.setDebugAll(true);



        initiateSkin();


        super.create();
        setView(MainView.class);
        //saveDtdSchema(Gdx.files.local("lml.dtd"));
    }

    @Override
    protected LmlParser createParser() {
        return VisLml.parser()
                .syntax(new AppLmlSyntax())
                .i18nBundle(I18NBundle.createBundle(Gdx.files.internal("i18n/bundle"))).build();
    }

    public void initiateSkin() {
        skin = new Skin();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("skin/VisOpenSansKerned.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter paramsDefault = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //paramsDefault.color = new Color(0xffffffe8);
        paramsDefault.size = 15;
        //paramsDefault.renderCount = 1;
        paramsDefault.gamma = 1.0f;

        FreeTypeFontGenerator.FreeTypeFontParameter paramsSmall = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //paramsSmall.color = new Color(0xffffffe8);
        paramsSmall.size = 12;
        //paramsSmall.renderCount = 1;
        paramsSmall.gamma = 0.5f;

        FreeTypeFontGenerator.FreeTypeFontParameter paramsBig = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //paramsBig.color = new Color(0xffffffe8);
        paramsBig.size = 22;
        //paramsBig.renderCount = 1;
        paramsBig.gamma = 0.75f;

        BitmapFont fontDefault = generator.generateFont(paramsDefault);
        BitmapFont fontSmall = generator.generateFont(paramsSmall);
        BitmapFont fontBig = generator.generateFont(paramsBig);

        generator.dispose();

        skin.add("default-font", fontDefault, BitmapFont.class);
        skin.add("small-font", fontSmall, BitmapFont.class);
        skin.add("big-font", fontBig, BitmapFont.class);

        skin.addRegions(new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas")));
        skin.load(Gdx.files.internal("skin/uiskin.json"));
        VisUI.load(skin);
    }


    // this should return true when using ScreenViewport
    @Override
    protected boolean isCenteringCameraOnResize() {
        return true;
    }

    public Stage getStage() {return stage;}

    public Batch getBatch() {
        return batch;
    }

    public Viewport getViewport() { return viewport;}

    public ShapeRenderer getShapeRenderer() {return shapeRenderer;}

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

 /*   @Override
    public void render() {

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        batch.begin();
            font.draw(batch, Gdx.graphics.getFramesPerSecond()+" FPS", 10, 20);
        batch.end();


    } */

    public Shader getShader() {
        return shader;
    }



    @Override
    public void dispose() {
        batch.dispose();
        cMove.dispose();
        cResize_ne.dispose();
        cResize_nw.dispose();



        VisUI.dispose();
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