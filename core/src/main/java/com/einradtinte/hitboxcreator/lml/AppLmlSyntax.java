package com.einradtinte.hitboxcreator.lml;

import com.einradtinte.hitboxcreator.lml.attributes.TableTiledBackgroundLmlAttribute;
import com.einradtinte.hitboxcreator.views.Editor;
import com.github.czyzby.lml.vis.parser.impl.VisLmlSyntax;

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
