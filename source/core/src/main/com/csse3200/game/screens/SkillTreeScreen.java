package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.skilltree.SkilltreeButtons;
import com.csse3200.game.components.skilltree.SkilltreeDisplay;
import com.csse3200.game.components.worldmap.AnimatedDropdownMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenu;
import com.csse3200.game.components.worldmap.WorldMapNavigationMenuActions;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The screen that displays the Skill Tree and handles skill unlocking mechanics. Provides UI for
 * skill buttons, shows skill points, and handles user interaction. It integrates with game
 * services, rendering, input, and entity systems.
 */
public class SkillTreeScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(SkillTreeScreen.class);
  private final GdxGame game;
  private final Renderer renderer;

  /**
   * Constructs a SkillTreeScreen, initializing all necessary services and rendering components.
   *
   * @param game the main game instance
   */
  public SkillTreeScreen(GdxGame game) {
    this.game = game;

    logger.debug("Initialising skill tree services");

    // Register required services
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerTimeSource(new GameTime());

    // Initialize renderer and camera
    renderer = RenderFactory.createRenderer();
    renderer.getCamera().getEntity().setPosition(5f, 5f);

    // Load assets and setup UI
    loadAssets();
    createUI();
  }

  /** Loads necessary game assets */
  private void loadAssets() {
    logger.debug("Loading assets");
    String[] textures = {"images/backgrounds/skilltree_background.png"};
    ServiceLocator.getResourceService().loadTextures(textures);
    ServiceLocator.getResourceService().loadSounds(new String[] {"sounds/button_unlock_skill.mp3"});
    ServiceLocator.getResourceService().loadAll();
    ServiceLocator.getMusicService().play("sounds/background-music/skilltree_background.mp3");
  }

  @Override
  public void render(float delta) {
    // Update entities and render scene
    ServiceLocator.getEntityService().update();
    renderer.render();
  }

  @Override
  public void resize(int width, int height) {
    Stage stage = ServiceLocator.getRenderService().getStage();
    stage.getViewport().update(width, height, true);
    stage.clear();
    createUI();
  }

  /**
   * Sets up the UI elements: background, skill points display, input handling, and skill buttons.
   */
  private void createUI() {
    logger.debug("Creating UI");
    Stage stage = ServiceLocator.getRenderService().getStage();

    // Set background image
    Texture backgroundTexture =
        ServiceLocator.getResourceService()
            .getAsset("images/backgrounds/skilltree_background.png", Texture.class);
    Image backgroundImage = new Image(backgroundTexture);
    backgroundImage.setSize(
        stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
    stage.addActor(backgroundImage);

    // Create UI entity with various components
    Entity ui = new Entity();
    SkilltreeDisplay display = new SkilltreeDisplay();
    ui.addComponent(new InputDecorator(stage, 10))
        .addComponent(display)
        .addComponent(new SkilltreeButtons(game, display))
        .addComponent(new WorldMapNavigationMenu())
        .addComponent(new WorldMapNavigationMenuActions(this.game))
        .addComponent(new AnimatedDropdownMenu())
        .addComponent(new Terminal())
        .addComponent(ServiceLocator.getInputService().getInputFactory().createForTerminal())
        .addComponent(new TerminalDisplay());

    ServiceLocator.getEntityService().register(ui);
  }

  @Override
  public void dispose() {
    // Dispose renderer, services, and clear ServiceLocator
    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.clear();
  }
}
