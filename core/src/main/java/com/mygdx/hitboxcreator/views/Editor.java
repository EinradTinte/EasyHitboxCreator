package com.mygdx.hitboxcreator.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractNonParentalActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.mygdx.hitboxcreator.App;

public class Editor extends Stack {

    private  InfoPanel infoPanel;
    private  CanvasHolder canvasHolder;
    private  Image imgBackground;


    private final Rectangle widgetAreaBounds = new Rectangle();
    private final Rectangle scissorBounds = new Rectangle();

    public Editor(Skin skin) {


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

                addActor(canvasHolder);
            }

            // Buttons
            {

            }

            // Info pane
            {
                infoPanel = new InfoPanel(App.inst().getParser());
                addActor(infoPanel);
            }
        }
    }

    public void reloadProject() {

    }

    private void addHitRectangle() {

    }

    private void addHitCircle() {

    }


    // Apply scissors
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.flush();
        // calculating scisscors because we only want to draw in the editor window
        getStage().calculateScissors(widgetAreaBounds.set(getX(), getY(), getWidth(), getHeight()), scissorBounds);
        App.inst().getShader().setScissorBounds(scissorBounds);
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
