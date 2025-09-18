package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating the world map UI components.
 * Separates UI creation from the main screen logic.
 */
public class WorldMapUIFactory {
    private static final Logger logger = LoggerFactory.getLogger(WorldMapUIFactory.class);
    
    public Entity create(GdxGame game) {
        Entity ui = new Entity();
        ui.addComponent(new WorldMapUIComponent(game));
        return ui;
    }
    
    private static class WorldMapUIComponent extends Component {
        private final GdxGame game;
        private Skin skin;
        
        public WorldMapUIComponent(GdxGame game) {
            this.game = game;
        }
        
        @Override
        public void create() {
            skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
            createBackButton();
            createNavigationButtons();
        }
        
        private void createBackButton() {
            Texture backTexture = ServiceLocator.getResourceService()
                .getAsset("images/back_button.png", Texture.class);
            
            ImageButton backBtn = new ImageButton(new TextureRegionDrawable(backTexture));
            backBtn.setTransform(true);
            backBtn.setSize(150, 150);
            backBtn.setOrigin(backBtn.getWidth() / 2f, backBtn.getHeight() / 2f);
            backBtn.setPosition(20, (float) Gdx.graphics.getHeight() - 170);
            
            backBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    logger.info("Back → MAIN_MENU");
                    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
                }
            });
            
            backBtn.addListener(new InputListener() {
                @Override
                public void enter(InputEvent e, float x, float y, int pointer, Actor from) {
                    backBtn.setScale(1.07f);
                }

                @Override
                public void exit(InputEvent e, float x, float y, int pointer, Actor to) {
                    backBtn.setScale(1f);
                }
            });
            
            ServiceLocator.getRenderService().getStage().addActor(backBtn);
        }
        
        private void createNavigationButtons() {
            // Shop button
            TextButton shopBtn = new TextButton("Shop", skin);
            shopBtn.setPosition((float) Gdx.graphics.getWidth() - 240, 20);
            shopBtn.setSize(100, 40);
            shopBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    logger.info("Opening Shop");
                    game.setScreen(GdxGame.ScreenType.SHOP);
                }
            });
            ServiceLocator.getRenderService().getStage().addActor(shopBtn);

            // Inventory button
            TextButton invBtn = new TextButton("Inventory", skin);
            invBtn.setPosition((float) Gdx.graphics.getWidth() - 120, 20);
            invBtn.setSize(100, 40);
            invBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    logger.info("Opening Inventory");
                    game.setScreen(GdxGame.ScreenType.INVENTORY);
                }
            });
            ServiceLocator.getRenderService().getStage().addActor(invBtn);
        }
        
        @Override
        public void dispose() {
            if (skin != null) {
                skin.dispose();
            }
        }
    }
}
