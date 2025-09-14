package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.areas.SlotMachineArea;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.currency.SunlightHudDisplay;
import com.csse3200.game.components.gamearea.PerformanceDisplay;
import com.csse3200.game.components.hud.HudDisplay;
import com.csse3200.game.components.maingame.MainGameActions;
import com.csse3200.game.components.waves.CurrentWaveDisplay;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.CurrencyService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.terminal.Terminal;
import com.csse3200.game.ui.terminal.TerminalDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The game screen containing the main game.
 *
 * <p>Details on libGDX screens: https://happycoding.io/tutorials/libgdx/game-screens
 */
public class SlotMachineScreen extends ScreenAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MainGameScreen.class);
    private static final String[] mainGameTextures = {
            "images/normal_sunlight.png", "images/heart.png", "images/coins.png", "images/profile.png"
    };
    private static final Vector2 CAMERA_POSITION = new Vector2(7.5f, 7.5f);

    private final GdxGame game;
    private final Renderer renderer;
    private final PhysicsEngine physicsEngine;
    private final WaveManager waveManager;
    private SlotMachineArea slotMachineArea;

    public SlotMachineScreen(GdxGame game) {
        this.game = game;
        this.waveManager = new WaveManager();


        logger.debug("Initialising main game screen services");
        ServiceLocator.registerTimeSource(new GameTime());

        PhysicsService physicsService = new PhysicsService();
        ServiceLocator.registerPhysicsService(physicsService);
        physicsEngine = physicsService.getPhysics();

        ServiceLocator.registerInputService(new InputService());
        ServiceLocator.registerResourceService(new ResourceService());

        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerRenderService(new RenderService());

        ServiceLocator.registerCurrencyService(new CurrencyService(50, Integer.MAX_VALUE));

        renderer = RenderFactory.createRenderer();
        renderer.getCamera().getEntity().setPosition(CAMERA_POSITION);
        renderer.getDebug().renderPhysicsWorld(physicsEngine.getWorld());

        loadAssets();
        createUI();

        Entity uiHud = new Entity().addComponent(new SunlightHudDisplay());
        ServiceLocator.getEntityService().register(uiHud);

        logger.debug("Initialising main game screen entities");
        TerrainFactory terrainFactory = new TerrainFactory(renderer.getCamera());
        slotMachineArea = new SlotMachineArea(terrainFactory);
        waveManager.setGameArea(slotMachineArea);
        slotMachineArea.create();


        snapCameraBottomLeft();
        waveManager.initialiseNewWave();
    }

    @Override
    public void render(float delta) {
        physicsEngine.update();
        ServiceLocator.getEntityService().update();
        renderer.render();
        waveManager.update();
        slotMachineArea.checkGameOver(); // check game-over state
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
        snapCameraBottomLeft();
        logger.trace("Resized renderer: ({} x {})", width, height);
        if (slotMachineArea != null) {
            slotMachineArea.resize();
        }
    }

    @Override
    public void pause() {
        logger.info("Game paused");
    }

    @Override
    public void resume() {
        logger.info("Game resumed");
    }

    @Override
    public void dispose() {
        logger.debug("Disposing main game screen");

        renderer.dispose();
        unloadAssets();

        ServiceLocator.getEntityService().dispose();
        ServiceLocator.getRenderService().dispose();
        ServiceLocator.getResourceService().dispose();

        ServiceLocator.clear();
    }

    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(mainGameTextures);
        ServiceLocator.getResourceService().loadAll();
    }

    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(mainGameTextures);
    }

    /**
     * Creates the main game's ui including components for rendering ui elements to* the screen and
     * capturing and handling ui input.
     */
    private void createUI() {
        logger.debug("Creating ui");
        Stage stage = ServiceLocator.getRenderService().getStage();
        InputComponent inputComponent =
                ServiceLocator.getInputService().getInputFactory().createForTerminal();

        Entity ui = new Entity();
        ui.addComponent(new InputDecorator(stage, 10))
                .addComponent(new PerformanceDisplay())
                .addComponent(new MainGameActions(this.game))
                .addComponent(new HudDisplay())
                .addComponent(new Terminal())
                .addComponent(inputComponent)
                .addComponent(new TerminalDisplay())
                .addComponent(new CurrentWaveDisplay());

        // Connect the UI entity to the WaveManager for event triggering
        WaveManager.setGameEntity(ui);

        ServiceLocator.getEntityService().register(ui);
    }

    private void snapCameraBottomLeft() {
        var cam = renderer.getCamera();

        float viewportWidth = cam.getCamera().viewportWidth;
        float viewportHeight = cam.getCamera().viewportHeight;

        cam.getEntity().setPosition(viewportWidth / 2f, viewportHeight / 2f);
    }
}
