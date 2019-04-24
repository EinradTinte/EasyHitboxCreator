package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageTextButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mygdx.hitboxcreator.App;
import com.mygdx.hitboxcreator.utils.HitCircle;
import com.mygdx.hitboxcreator.utils.HitRectangle;
import com.mygdx.hitboxcreator.utils.ProjectModel;

public class Editor extends Stack {

    private InfoPanel infoPanel;
    private CanvasHolder canvasHolder;
    private Image imgBackground;
    private VisImageButton btnRectangle, btnCircle;
    private ScaleGroup group;

    private final float DEFAULT_HITSHAPE_SIZE = 25;

    private final Rectangle widgetAreaBounds = new Rectangle();
    private final Rectangle scissorBounds = new Rectangle();

    private PopupMenu popupMenu;

    public Editor(Skin skin) {

        // Editor popupMenu to center its content
        popupMenu = new PopupMenu();
        String text = App.inst().getI18NBundle().format("menuItemCanvas");
        MenuItem menuItem = new MenuItem(text);
        menuItem.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                group.centerOnParent();
            }
        });
        popupMenu.addItem(menuItem);


        // Layout
        {
            // Background
            {

                imgBackground = new Image(skin.getTiledDrawable("custom/transparent-light"));
                addActor(imgBackground);
            }

            // HitShapes
            {
                canvasHolder = new CanvasHolder(skin);
                canvasHolder.setListener(new CanvasHolder.Listener() {
                    @Override
                    public void onZoomChanged(int percentage) {
                        infoPanel.setZoomLevel(percentage);
                    }
                });
                // Todo: Editor right click menu recieves the click too, although a HitShape answers that click
                // both menus would overlay each other so I have to do without
                //canvasHolder.addListener(popupMenu.getDefaultInputListener());

                addActor(canvasHolder);
                group = canvasHolder.getScaleGroup();
            }

            // Buttons
                {
                    //TODO: skin atlas
                VisImageButton.VisImageButtonStyle recStyle = new VisImageButton.VisImageButtonStyle();
                recStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("buttons/btnRectangle_Up.png")));
                recStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture("buttons/btnRectangle_Down.png")));
                recStyle.imageOver = new TextureRegionDrawable(new TextureRegion(new Texture("buttons/btnRectangle_Over.png")));

                btnRectangle = new VisImageButton(recStyle);
                String recTt = App.inst().getI18NBundle().format("tTaddRectangle");
                btnRectangle.addListener(new TextTooltip(recTt, VisUI.getSkin()));
                btnRectangle.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        Vector2 pos = convertToScaleGroupPos((getWidth() - 100) - DEFAULT_HITSHAPE_SIZE*group.getScaleX(), 100 - DEFAULT_HITSHAPE_SIZE*group.getScaleX());
                        group.addHitShape(new HitRectangle(pos.x, pos.y, DEFAULT_HITSHAPE_SIZE*2, DEFAULT_HITSHAPE_SIZE*2));
                    }
                });


                // ADD CIRCLE BUTTON

                VisImageButton.VisImageButtonStyle cirStyle = new VisImageButton.VisImageButtonStyle();
                cirStyle.imageUp = new TextureRegionDrawable(new TextureRegion(new Texture("buttons/btnCircle_Up.png")));
                cirStyle.imageDown = new TextureRegionDrawable(new TextureRegion(new Texture("buttons/btnCircle_Down.png")));
                cirStyle.imageOver = new TextureRegionDrawable(new TextureRegion(new Texture("buttons/btnCircle_Over.png")));

                btnCircle = new VisImageButton(cirStyle);
                String cirTt = App.inst().getI18NBundle().format("tTaddCircle");
                btnCircle.addListener(new TextTooltip(cirTt, VisUI.getSkin()));
                btnCircle.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        Vector2 pos = convertToScaleGroupPos(getWidth() - 100, 50);
                        group.addHitShape(new HitCircle(pos.x, pos.y, DEFAULT_HITSHAPE_SIZE));
                    }
                });


                VisTable table = new VisTable();
                table.add(btnRectangle);
                table.row().padTop(6);
                table.add(btnCircle);

                Container container = new Container<>(table);
                container.align(Align.bottomRight);
                container.padBottom(25);
                container.padRight(25);
                add(container);
            }

            // Info panel
            {
                infoPanel = new InfoPanel(App.inst().getParser());
                addActor(infoPanel);
            }
        }
    }

    private Vector2 convertToScaleGroupPos(float x, float y) {
        return new Vector2((x - group.getX())/group.getScaleX(), (y - group.getY())/group.getScaleY());
    }

    public InfoPanel getInfoPanel() { return infoPanel; }
    public CanvasHolder getCanvasHolder() { return canvasHolder; }

    // Apply scissors
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.flush();
        // calculating scisscors because we only want to draw in the editor window
        getStage().calculateScissors(widgetAreaBounds.set(getX(), getY(), getWidth(), getHeight()), scissorBounds);
        if (ScissorStack.pushScissors(scissorBounds)) {
            super.draw(batch, parentAlpha);
            batch.flush();
            ScissorStack.popScissors();
        }
    }

    public static class EditorLmlTagProvider implements LmlTagProvider {
        @Override
        public LmlTag create(LmlParser lmlParser, LmlTag lmlTag, StringBuilder stringBuilder) {
            return new EditorLmlTag(lmlParser, lmlTag, stringBuilder);
        }
    }

    public static class EditorLmlTag extends AbstractNonParentalActorLmlTag {
        public EditorLmlTag(LmlParser parser, LmlTag parentTag, StringBuilder rawTagData) {
            super(parser, parentTag, rawTagData);
        }

        @Override
        protected Actor getNewInstanceOfActor(LmlActorBuilder lmlActorBuilder) {
            return new Editor(getSkin(lmlActorBuilder));
        }
    }
}
