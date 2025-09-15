package com.csse3200.game.components.hotbar;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.ui.UIComponent;
import java.util.Map;
import java.util.function.Supplier;

public class HotbarDisplay extends UIComponent {

    private Table hotbarTable;
    private float scaling;
    private LevelGameArea game;
    private Map<String, Supplier<Entity>> unitList;


    public HotbarDisplay(LevelGameArea game, Float scaling, Map<String, Supplier<Entity>> unitList) {
        this.scaling = scaling;
        this.game = game;
        this.unitList = unitList;
    }

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        Group layered = new Group();

        Image base = new Image(new Texture("images/hotbar.png"));
        layered.addActor(base);

        layered.setSize(base.getPrefWidth(), base.getPrefHeight());

        float hotbarWidth = layered.getWidth();
        float cellWidth = hotbarWidth / 6;
        float x = cellWidth / 4;
        float y = 30;
        for (Map.Entry<String, Supplier<Entity>> unit : unitList.entrySet()) {
            Image tempUnit = new Image(new Texture(unit.getKey()));
            tempUnit.setSize(scaling, scaling);
            tempUnit.setPosition(x, y);
            tempUnit.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Entity tempPlaceableUnit =
                            new Entity()
                                    .addComponent(new DeckInputComponent(game, unit.getValue()))
                                    .addComponent(new TextureRenderComponent(unit.getKey()));
                    game.setSelectedUnit(tempPlaceableUnit);
                }
            });
            layered.addActor(tempUnit);
            x += cellWidth;
        }

        hotbarTable = new Table();
        hotbarTable.setFillParent(true);
        hotbarTable.center().top();
        float targetWidth = stage.getViewport().getWorldWidth() * 0.5f;
        float scale = targetWidth / layered.getWidth();

        layered.setScale(scale);

        hotbarTable.setTouchable(Touchable.childrenOnly);

        hotbarTable.add(layered)
                .size(layered.getWidth() * scale, layered.getHeight() * scale);

        stage.addActor(hotbarTable);
        hotbarTable.toBack();
    }

    @Override
    protected void draw(SpriteBatch batch) {
        // ignore
    }

    @Override
    public void dispose() {
        if (hotbarTable != null) {
            hotbarTable.remove();
            hotbarTable = null;
        }
        super.dispose();
    }
}
