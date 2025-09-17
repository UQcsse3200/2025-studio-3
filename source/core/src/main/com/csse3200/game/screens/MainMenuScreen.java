package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.mainmenu.MainMenuActions;
import com.csse3200.game.components.mainmenu.MainMenuDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The game screen containing the main menu.
 */
public class MainMenuScreen extends BaseScreen {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuScreen.class);

    private static final String[] MAIN_MENU_TEXTURES = {
            "images/bg.png",
            "images/bg-text.png",
            "images/btn-blue.png",
            "images/btn-blue.atlas"
//            "flat-earth/skin/fonts/pixel_32.png",
//            "flat-earth/skin/fonts/pixel_32.fnt"
    };

    public MainMenuScreen(GdxGame game) { super(game, MAIN_MENU_TEXTURES); }

  @Override
  public void pause() {
    logger.info("Game paused");
  }

  @Override
  public void resume() {
    logger.info("Game resumed");
  }

    /**
     * Creates the main menu's ui including components for rendering ui elements to the screen and
     * capturing and handling ui input.
     */
    @Override
    protected Entity createUIScreen(Stage stage) {
        // Register the UI entity that owns the display and actions
        logger.debug("Main menu screen ui is created");
        return new Entity()
                .addComponent(new MainMenuDisplay())
                .addComponent(new InputDecorator(stage, 10))
                .addComponent(new MainMenuActions(game));
    }
}
