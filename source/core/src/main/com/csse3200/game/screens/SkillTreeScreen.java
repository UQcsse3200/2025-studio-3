package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.gamearea.PerformanceDisplay;
import com.csse3200.game.components.skilltree.SkilltreeButtons;
import com.csse3200.game.components.skilltree.SkilltreeDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputComponent;
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
  private final Texture background;
  private final SpriteBatch batch;
  protected static final Skin skin =
      new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));

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

    // Create batch and background texture
    batch = new SpriteBatch();
    background = new Texture(Gdx.files.internal("images/skilltree_background.png"));
  }

  /** Loads necessary game assets */
  private void loadAssets() {
    logger.debug("Loading assets");
    ServiceLocator.getResourceService().loadAll();
  }

  @Override
  public void render(float delta) {
    // Draw background
    batch.begin();
    batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    batch.end();

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
    Texture backgroundTexture = new Texture(Gdx.files.internal("images/skilltree_background.png"));
    Image backgroundImage = new Image(backgroundTexture);
    backgroundImage.setSize(
        stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
    stage.addActor(backgroundImage);

    // Input handling for terminal and UI interactions
    InputComponent inputComponent =
        ServiceLocator.getInputService().getInputFactory().createForTerminal();

    // Create UI entity with various components
    Entity ui = new Entity();
    ui.addComponent(new InputDecorator(stage, 10))
        .addComponent(new PerformanceDisplay())
        .addComponent(new Terminal())
        .addComponent(inputComponent)
        .addComponent(new SkilltreeButtons(game, new SkilltreeDisplay()))
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
