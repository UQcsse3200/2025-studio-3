package com.csse3200.game.components.inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

public class InventoryDisplay extends UIComponent {
    private Table table;
    private static final String[] inventoryTextures = {
        "images/inventory.png",
    };

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        table = new Table();
        table.top();
        table.setFillParent(true);

        // Load inventory slot textures
        for (String texturePath : inventoryTextures) {
            Texture texture = ServiceLocator.getResourceService().getAsset(texturePath, Texture.class);
            Image slotImage = new Image(new TextureRegionDrawable(texture));
            table.add(slotImage).pad(5);
        }

        stage.addActor(table);
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Drawing is handled by the stage actors
    }

    @Override
    public void dispose() {
        super.dispose();
        table.clear();
    }
}