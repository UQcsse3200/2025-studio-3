package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.ui.UIComponent;

public class LevelMapTutorial extends UIComponent {
    private Table tutorialTable;
    private float alpha = 1f;

    @Override
    public void create() {
        super.create();
        tutorialTable = new Table();
        tutorialTable.top().left().pad(10f);
        tutorialTable.setFillParent(true);

        LabelStyle style = new LabelStyle();
        style.font = new BitmapFont();
        style.fontColor = new Color(1, 1, 1, alpha);

        Label step1 = new Label("Welcome to Level 1!", style);
        Label step2 = new Label("Drag units from the hotbar to the grid.", style);
        Label step3 = new Label("Generators produce currency over time.", style);
        Label step4 = new Label("Defenders attack incoming enemies.", style);

        tutorialTable.add(step1).left().row();
        tutorialTable.add(step2).left().row();
        tutorialTable.add(step3).left().row();
        tutorialTable.add(step4).left().row();

        stage.addActor(tutorialTable);
        tutorialTable.toFront();
    }

    @Override
    public void dispose() {
        super.dispose();
        tutorialTable.remove();
    }

    /**
     * Draw the renderable. Should be called only by the renderer, not manually.
     *
     * @param batch Batch to render to.
     */
    @Override
    protected void draw(SpriteBatch batch) {

    }
}
