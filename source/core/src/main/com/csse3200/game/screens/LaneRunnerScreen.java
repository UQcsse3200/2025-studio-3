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
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaneRunnerScreen extends ScreenAdapter {

    private static final Logger logger = LoggerFactory.getLogger(com.csse3200.game.screens.MainMenuScreen.class);
    private final GdxGame game;
    private final Renderer renderer;
    private static final String[] laneRunnerTextures = {
            "images/bg.png",
    };

    public LaneRunnerScreen(GdxGame game) {
        this.game = game;

        logger.debug("Initialising lane runner mini game screen services");
        ServiceLocator.registerInputService(new InputService());
        ServiceLocator.registerResourceService(new ResourceService());
        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerRenderService(new RenderService());

        renderer = RenderFactory.createRenderer();

        loadAssets();
        createUI();
    }
    public void render(float delta) {
        ServiceLocator.getEntityService().update();
        renderer.render();
    }
    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(laneRunnerTextures);
        ServiceLocator.getResourceService().loadAll();
    }
    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(laneRunnerTextures);
    }
    public void dispose() {
        logger.debug("Disposing lane runner mini game screen");

        renderer.dispose();
        unloadAssets();
        ServiceLocator.getRenderService().dispose();
        ServiceLocator.getEntityService().dispose();

        ServiceLocator.clear();
    }

    private void createUI() {
        logger.debug("Creating ui");
        Stage stage = ServiceLocator.getRenderService().getStage();

        // Add the background image as a Stage actor
        Texture bgTex = ServiceLocator.getResourceService()
                .getAsset("images/bg.png", Texture.class);
        logger.debug("loads lane runner mini game screen background texture asset");
        Image bg = new Image(new TextureRegionDrawable(new TextureRegion(bgTex)));
        bg.setFillParent(true);
        bg.setScaling(Scaling.fill);
        stage.addActor(bg);
        logger.debug("shows lane runner mini game screen background");
    }

}
