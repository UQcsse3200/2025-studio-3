package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


abstract class BaseScreen extends ScreenAdapter {
    private Logger logger = LoggerFactory.getLogger(BaseScreen.class);
    protected final GdxGame game;
    protected final Renderer renderer;
    protected final String[] backgroundTextures;

    BaseScreen(GdxGame game, String... backgroundTextures) {
        this.game = game;
        this.backgroundTextures = backgroundTextures != null ? backgroundTextures : new String[0];

        logger.debug("initialising {} services", getClass().getSimpleName());
        ServiceLocator.registerInputService(new InputService());
        ServiceLocator.registerResourceService(new ResourceService());
        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerRenderService(new RenderService());
        ServiceLocator.registerTimeSource(new GameTime());

        renderer = RenderFactory.createRenderer();
        logger.debug("{} renderer created", getClass().getSimpleName());
        renderer.getCamera().getEntity().setPosition(5f, 5f);

        loadAssets();
        createUI();
    }

    protected abstract Entity createUIScreen(Stage stage);

    @Override
    public void render(float delta) {
        ServiceLocator.getEntityService().update();
        logger.debug("rendering {}", getClass().getSimpleName());
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        logger.trace("Resized renderer: ({} x {})", width, height);
        renderer.resize(width, height);
    }

    @Override
    public void dispose() {
        logger.debug("{} is being disposed", getClass().getSimpleName());

        renderer.dispose();
        unloadAssets();
        ServiceLocator.getRenderService().dispose();
        ServiceLocator.getEntityService().dispose();

        logger.debug("{} services cleared", getClass().getSimpleName());
        ServiceLocator.clear();
    }

    private void loadAssets() {
        if (backgroundTextures.length == 0) return;
        logger.debug("loading {} assets", getClass().getSimpleName());
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(backgroundTextures);
        ServiceLocator.getResourceService().loadAll();
    }

    private void unloadAssets() {
        logger.debug("unloading {} assets", getClass().getSimpleName());
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(backgroundTextures);
    }

    private void createUI() {
        logger.debug("creating {} UI", getClass().getSimpleName());
        Stage stage = ServiceLocator.getRenderService().getStage();

        if (backgroundTextures.length > 0) {
            // use the first path as background
            Texture bgTexture = ServiceLocator.getResourceService()
                    .getAsset(backgroundTextures[0], Texture.class);
            Image bg = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));
            bg.setFillParent(true);
            bg.setScaling(Scaling.fill);
            stage.addActor(bg);
        }

        Entity ui = createUIScreen(stage);
        ServiceLocator.getEntityService().register(ui);
        logger.debug("{} registered and created", getClass().getSimpleName());
    }
}