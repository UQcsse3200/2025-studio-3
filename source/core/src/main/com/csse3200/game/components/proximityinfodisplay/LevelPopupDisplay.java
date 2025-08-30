package com.csse3200.game.components.proximityinfodisplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.csse3200.game.components.player.PlayerActions;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.BodyUserData;
import com.csse3200.game.ui.UIComponent;

public class LevelPopupDisplay extends UIComponent {
    private Window popupDisplay;
    private boolean isDisplayed = false;

    @Override
    public void create() {
        super.create();

        // Listen to collisions with entity
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);

        // Creates popup display.
        popupDisplay = new Window ("Tree", skin);
        //popupDisplay.setMovable(false);
        popupDisplay.setSize(100, 200);
        popupDisplay.setPosition(
                (Gdx.graphics.getWidth() - popupDisplay.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - popupDisplay.getHeight()) / 2f
        );

        Label message = new Label("Tree", skin);
        popupDisplay.add(message).pad(10).row();

        popupDisplay.setVisible(false);
        stage.addActor(popupDisplay);
    }

    @Override
    public void update() {
        if (!isDisplayed) {
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Keys.E)) {
            popupDisplay.setVisible(false);
            isDisplayed = false;
        }
    }

    private void onCollisionStart(Fixture me, Fixture other) {
        Entity otherEntity = ((BodyUserData) other.getBody().getUserData()).entity;
        if (otherEntity.getComponent(PlayerActions.class) != null) {
            popupDisplay.setVisible(true);
            isDisplayed = true;
        }
    }

    @Override
    public void dispose() {
        if (popupDisplay != null) {
            popupDisplay.remove();
        }

        super.dispose();
    }

    @Override
    protected void draw(SpriteBatch batch) {

    }
}
