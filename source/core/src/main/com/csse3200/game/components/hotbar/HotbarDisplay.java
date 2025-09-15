package com.csse3200.game.components.hotbar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.ui.UIComponent;

public class HotbarDisplay extends UIComponent {

    private Table table;

    public HotbarDisplay() {
        // ignore
    }

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        Label unitLabel = new Label("Units", skin, "title");
        unitLabel.setColor(Color.BLACK);
        Texture units = new Texture("images/hotbar.png");
        Image firstHotbar = new Image(units);

        Label itemLabel = new Label("Items", skin, "title");
        itemLabel.setColor(Color.BLACK);
        Texture items = new Texture("images/hotbar.png");
        Image secondHotbar = new Image(items);

        table = new Table();
        table.setFillParent(true);
        table.center().top();
        table.add(unitLabel);
        table.add(firstHotbar);
        table.row();
        table.add(itemLabel);
        table.add(secondHotbar);

        stage.addActor(table);
    }

    @Override
    protected void draw(SpriteBatch batch) {
        // ignore
    }

    @Override
    public void dispose() {
        if (table != null) {
            table.remove();
            table = null;
        }
        super.dispose();
    }
}
