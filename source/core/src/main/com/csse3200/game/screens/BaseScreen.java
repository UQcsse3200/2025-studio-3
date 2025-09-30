package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base screen class for all screens. */
abstract class BaseScreen extends ScreenAdapter {
  private Logger logger = LoggerFactory.getLogger(BaseScreen.class);
  protected final GdxGame game;
  protected final Renderer renderer;
  protected final Optional<String> backgroundTexture;
  protected final Optional<String[]> additionalTextures;

  /**
   * Constructor for BaseScreen.
   *
   * @param game the game instance
   * @param backgroundTexture the background texture
   * @param additionalTextures the additional textures
   */
  BaseScreen(
      GdxGame game, Optional<String> backgroundTexture, Optional<String[]> additionalTextures) {
    this.game = game;
    this.backgroundTexture = backgroundTexture;
    this.additionalTextures = additionalTextures;

    logger.debug("[{}] Initialising services", getClass().getSimpleName());
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerTimeSource(new GameTime());

    renderer = RenderFactory.createRenderer();
    logger.debug("[{}] Renderer created", getClass().getSimpleName());
    renderer.getCamera().getEntity().setPosition(5f, 5f);

    loadAssets();
    createUI();
  }

  /**
   * Constructs the UI entity for the base screen.
   *
   * @param stage the stage to create the UI screen on
   * @return the UI entity
   */
  protected abstract Entity constructEntity(Stage stage);

  @Override
  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    logger.debug("[{}] Rendering", getClass().getSimpleName());
    renderer.render();
  }

  @Override
  public void resize(int width, int height) {
    logger.debug("[{}] Resized renderer: ({} x {})", getClass().getSimpleName(), width, height);
    renderer.resize(width, height);
    ServiceLocator.getEntityService()
        .getEntities()
        .forEach(entity -> entity.getEvents().trigger("resize", new Vector2(width, height)));
  }

  @Override
  public void dispose() {
    logger.debug("[{}] Disposing", getClass().getSimpleName());

    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();

    logger.debug("[{}] Services cleared", getClass().getSimpleName());
    ServiceLocator.clear();
  }

  /** Loads the assets for the base screen. */
  private void loadAssets() {
    logger.debug("[{}] Loading assets", getClass().getSimpleName());
    ResourceService resourceService = ServiceLocator.getResourceService();
    if (backgroundTexture.isPresent()) {
      logger.debug("[{}] Loading background texture", getClass().getSimpleName());
      resourceService.loadTextures(new String[] {backgroundTexture.get()});
    }
    if (additionalTextures.isPresent()) {
      logger.debug("[{}] Loading additional textures", getClass().getSimpleName());
      resourceService.loadTextures(additionalTextures.get());
    }
    resourceService.loadAll();
  }

  /** Creates the UI for the base screen. */
  private void createUI() {
    logger.debug("[{}] Creating UI", getClass().getSimpleName());
    Stage stage = ServiceLocator.getRenderService().getStage();

    // Add the background image as a Stage actor
    if (backgroundTexture.isPresent()) {
      Texture bgTexture =
          ServiceLocator.getResourceService().getAsset(backgroundTexture.get(), Texture.class);
      Image bg = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));
      bg.setFillParent(true);
      bg.setScaling(Scaling.fill);
      stage.addActor(bg);
    }

    // Construct the UI entity
    Entity ui = constructEntity(stage);
    ServiceLocator.getEntityService().register(ui);
    logger.debug("[{}] Registered and created", getClass().getSimpleName());
  }
}
