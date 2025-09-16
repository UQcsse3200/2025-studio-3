package com.csse3200.game.minigame;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  ui component for displaying the Main menu.
 */
public class MiniGameDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(com.csse3200.game.components.mainmenu.MainMenuDisplay.class);
    private static final float Z_INDEX = 2f;
    private Table table;

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        table = new Table();
        table.setFillParent(true);
//        Image title =
//                new Image(
//                        ServiceLocator.getResourceService()
//                                .getAsset("images/box_boy_title.png", Texture.class));

        TextButton LaneRunnerBtn = new TextButton("Lane Runner", skin);
        TextButton BrickBreakerBtn = new TextButton("Brick Breaker", skin);
        TextButton BackBtn = new TextButton("Back", skin);
        // Triggers an event when the button is pressed
        LaneRunnerBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Lane Runner button clicked");
                        entity.getEvents().trigger("lanerunner");
                    }
                });
        BrickBreakerBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Brick Breaker button clicked");
                        entity.getEvents().trigger("brickbreaker");
                    }
                });
        BackBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Back button clicked");
                        entity.getEvents().trigger("back");
                    }
                });





//        table.add(title);

        table.add(LaneRunnerBtn).padTop(30f);
        table.row();
        table.add(BrickBreakerBtn).padTop(30f);
        table.row();
        table.add(BackBtn).padTop(15f);
        table.row();
        stage.addActor(table);
    }

    @Override
    public void draw(SpriteBatch batch) {
        // draw is handled by the stage
    }

    @Override
    public float getZIndex() {
        return Z_INDEX;
    }

    @Override
    public void dispose() {
        table.clear();
        super.dispose();
    }
}
