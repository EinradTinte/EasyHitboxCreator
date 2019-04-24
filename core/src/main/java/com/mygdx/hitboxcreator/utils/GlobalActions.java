package com.mygdx.hitboxcreator.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.action.ActionContainer;

public class GlobalActions implements ActionContainer {

    @LmlAction("getScrollOnHover") public static void getScrollOnHover(final Actor scrollPane) {
        scrollPane.addListener( new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                event.getStage().setScrollFocus(scrollPane);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (event.getRelatedActor() != toActor)
                    event.getStage().setScrollFocus(null);
            }
        });
    }

    /** VisTextArea has a bug that crashes it when the first line is only a newline character and you
     * want to select that. So we check for it and delete it.
     */
    public static String proofString(String s) {
        /*
        String[] lines = s.split("\\n");
        String out = "";

            for (int i = 0; i < lines.length; i++) {
                if (!lines[i].equals("")) {
                    out += lines[i];
                    if (i < lines.length-1) out += '\n';
                }
            }

        return out;
        */
        int i = s.indexOf("\n");
        return i == 0 ? s.substring(1) : s;
    }
}
