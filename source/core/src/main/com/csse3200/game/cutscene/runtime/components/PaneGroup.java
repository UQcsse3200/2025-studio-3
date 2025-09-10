package com.csse3200.game.cutscene.runtime.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class PaneGroup extends WidgetGroup {

    public void relayout() {
        float paneWidth = getWidth();
        float paneHeight = getHeight();

        for (Actor actor : getChildren()) {
            if (!(actor instanceof Image image)) continue;

            Drawable drawable = image.getDrawable();
            if (drawable == null) continue;

            float imageWidth = drawable.getMinWidth();
            float imageHeight = drawable.getMinHeight();
            float width = paneWidth;
            float height = width * (imageHeight / imageWidth);

            if (height > paneHeight) {
                width = paneHeight * (imageWidth / imageHeight);
                height = paneHeight;
            }

            image.setSize(width, height);
            image.setPosition((paneWidth - width) * 0.5f, 0f);

            float offsetX = (float) image.getUserObject();
            image.moveBy(offsetX * image.getWidth(), 0);
        }
    }

    public void setOffsetX(Image image, float percentageOffset) {
        image.setUserObject(percentageOffset);
        relayout();
    }
}
