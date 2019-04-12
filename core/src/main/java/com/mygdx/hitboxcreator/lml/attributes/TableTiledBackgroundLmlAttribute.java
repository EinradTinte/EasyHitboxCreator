package com.mygdx.hitboxcreator.lml.attributes;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

public class TableTiledBackgroundLmlAttribute implements LmlAttribute<Table> {
    @Override
    public Class<Table> getHandledType() {
        return Table.class;
    }

    @Override
    public void process(LmlParser lmlParser, LmlTag lmlTag, Table table, String s) {
        Skin skin = lmlParser.getData().getDefaultSkin();
        TiledDrawable drawable = skin.getTiledDrawable(lmlParser.parseString(s, table));
        table.setBackground(drawable);
    }
}
