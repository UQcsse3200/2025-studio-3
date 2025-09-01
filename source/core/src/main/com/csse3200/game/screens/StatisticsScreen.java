package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.statistics.StatisticsDisplay;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The StatisticsScreen is a game screen containing the player's Statistics.
 *
 * It sets up the rendering, input and services for the UI to function and manages a StatisticsDisplay component that
 * displays the actual Statistics.
 */
public class StatisticsScreen extends ScreenAdapter {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsScreen.class);

    private final GdxGame game;
    private final Renderer renderer;

    /**
     * Creates a new StatisticsScreen and registers the services required, creates the renderer, and initialises the
     * Statistics UI.
     *
     * @param gdxGame current game instance
     */
    public StatisticsScreen(GdxGame gdxGame) {
        this.game = gdxGame;

        logger.debug("Initialising statistics screen services");
        ServiceLocator.registerInputService(new InputService());
        ServiceLocator.registerResourceService(new ResourceService());
        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerRenderService(new RenderService());
        ServiceLocator.registerTimeSource(new GameTime());

        renderer = RenderFactory.createRenderer();
        renderer.getCamera().getEntity().setPosition(5f, 5f);

        createUI();
    }

    /**
     * Updates entities and renders current frame.
     *
     * @param delta time elapsed since last frame
     */
    @Override
    public void render(float delta) {
        ServiceLocator.getEntityService().update();
        renderer.render();
    }

    /**
     * Adjusts the screen when window is resized.
     *
     * @param width new screen width
     * @param height new screen height
     */
    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    /**
     * Disposes of this screen's resources.
     */
    @Override
    public void dispose() {
        renderer.dispose();
        ServiceLocator.getRenderService().dispose();
        ServiceLocator.getEntityService().dispose();

        ServiceLocator.clear();
    }

    /**
     * Creates the StatisticsScreen's UI including components for rendering UI elements to the screen
     * and capturing and handling UI input.
     */
    private void createUI() {
        logger.debug("Creating ui");
        Stage stage = ServiceLocator.getRenderService().getStage();
        Entity ui = new Entity();
        ui.addComponent(new StatisticsDisplay(game)).addComponent(new InputDecorator(stage, 10));
        ServiceLocator.getEntityService().register(ui);
    }
}

