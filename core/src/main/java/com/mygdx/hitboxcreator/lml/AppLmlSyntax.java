package com.mygdx.hitboxcreator.lml;

import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax;
import com.mygdx.hitboxcreator.lml.attributes.TableTiledBackgroundLmlAttribute;
import com.mygdx.hitboxcreator.views.Editor;

public class AppLmlSyntax extends VisLmlSyntax {

    @Override
    protected void registerActorTags() {
        super.registerActorTags();

        addTagProvider(new GroupLmlTag.TagProvider(), "group");
        addTagProvider(new Editor.EditorLmlTagProvider(), "editor");
    }

    @Override
    protected void registerTableAttributes() {
        super.registerTableAttributes();

        addAttributeProcessor(new TableTiledBackgroundLmlAttribute(), "bgTiled", "backgroundTiled");
    }
}
