package com.csse3200.game.components.hotbar;

import com.badlogic.gdx.Input;
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
    private final float scaling;
    private final LevelGameArea game;
    private final Map<String, Supplier<Entity>> unitList;

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

    /**
     * This method creates the ui for the hotbar and the units
     * that are selectable within its slots
     */
    private void addActors() {
        Group layered = new Group();

        // create hotbar image
        Image hotbar = new Image(new Texture("images/hotbar.png"));
        layered.addActor(hotbar);

        layered.setSize(hotbar.getPrefWidth(), hotbar.getPrefHeight());

        // create selection star image and set it off screen
        Image star = new Image(new Texture("images/selected_star.png"));
        layered.addActor(star);
        star.setPosition(-1000, -1000);
        star.setSize(0.5f * scaling, 0.5f * scaling);
        star.toFront();

        // initialise the values needed for placing unit images in slots
        float hotbarWidth = layered.getWidth();
        float cellWidth = hotbarWidth / 6;
        float x = cellWidth / 4;
        float y = 30;
        // creates unit images and places in slots
        for (Map.Entry<String, Supplier<Entity>> unit : unitList.entrySet()) {
            Image tempUnit = new Image(new Texture(unit.getKey()));
            tempUnit.setSize(scaling, scaling);
            tempUnit.setPosition(x, y);
            // creates listener that allows for selection and unselection of units
            tempUnit.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (event.getButton() == Input.Buttons.LEFT) {
                        Entity tempPlaceableUnit =
                                new Entity()
                                        .addComponent(new DeckInputComponent(game, unit.getValue()))
                                        .addComponent(new TextureRenderComponent(unit.getKey()));
                        game.setSelectedUnit(tempPlaceableUnit);
                        star.setPosition(x + 1.5f * cellWidth / 4, 100);
                    } else if (event.getButton() == Input.Buttons.RIGHT) {
                        game.setSelectedUnit(null);
                        star.setPosition(-1000, -1000);
                    }
                    return false;
                }
            });
            layered.addActor(tempUnit);
            x += cellWidth;
        }

        // sets the position to the top middle of screen
        hotbarTable = new Table();
        hotbarTable.setFillParent(true);
        hotbarTable.center().top();
        float targetWidth = stage.getViewport().getWorldWidth() * 0.5f;
        float scale = targetWidth / layered.getWidth();

        layered.setScale(scale);

        // makes only the images touchable
        hotbarTable.setTouchable(Touchable.childrenOnly);

        // changes size to fit screen
        hotbarTable.add(layered)
                .size(layered.getWidth() * scale, layered.getHeight() * scale);

        stage.addActor(hotbarTable);
        hotbarTable.toBack();
    }

    @Override
    protected void draw(SpriteBatch batch) {
        // draw is handled by the stage
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
