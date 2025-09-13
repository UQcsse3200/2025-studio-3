package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrickBreakerScreen extends MiniGame2 {

    private static final Logger logger = LoggerFactory.getLogger(BrickBreakerScreen.class);
    private final GdxGame game;
    private final Renderer renderer;

    private static final String[] BrickBreakerTextures = {
            "images/world_map.png",
            "images/bg.png",
            "images/ball.png",
            "images/paddle.png",
    };

    public BrickBreakerScreen(GdxGame game) {
        this.game = game;

        logger.debug("Initialising brick breaker mini game screen services");
        ServiceLocator.registerInputService(new InputService());
        ServiceLocator.registerResourceService(new ResourceService());
        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerRenderService(new RenderService());
        ServiceLocator.registerPhysicsService(new PhysicsService());

        renderer = RenderFactory.createRenderer();

        loadAssets();
        //createUI();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        //ServiceLocator.getEntityService().update();
        renderer.render();
    }

    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(BrickBreakerTextures);
        ServiceLocator.getResourceService().loadAll();
    }

    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(BrickBreakerTextures);
    }

    public void dispose() {
        logger.debug("Disposing brick breaker mini game screen");

        renderer.dispose();
        unloadAssets();
        //ServiceLocator.getRenderService().dispose();
        //ServiceLocator.getEntityService().dispose();

        ServiceLocator.clear();
    }

    private void createUI() {
        logger.debug("Creating ui");
        Stage stage = ServiceLocator.getRenderService().getStage();

        // Add the background image as a Stage actor
        Texture bgTex = ServiceLocator.getResourceService()
                .getAsset("images/bg.png", Texture.class);
        Image bg = new Image(new TextureRegionDrawable(new TextureRegion(bgTex)));
        bg.setFillParent(true);
        bg.setScaling(Scaling.fill);
        stage.addActor(bg);
        logger.debug("shows brick breaker mini game screen background");
    }

    @Override
    public void show() {
        logger.debug("Showing brick breaker mini game screen");
        super.show();
        createUI();
    }

}

