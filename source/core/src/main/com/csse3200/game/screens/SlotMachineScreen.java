package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.csse3200.game.GdxGame;
import com.csse3200.game.areas.SlotMachineGameArea;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;


/**
 * Screen wrapper for the Slot Machine level.
 * Sets up required services, creates SlotMachineGameArea,
 * and handles rendering & disposal.
 */
public class SlotMachineScreen extends ScreenAdapter {
    private final GdxGame game;
    private final Renderer renderer;
    private SlotMachineGameArea slotArea;

    public SlotMachineScreen(GdxGame game) {
        this.game = game;

        // Register core services required for this screen
        ServiceLocator.registerInputService(new InputService());
        ServiceLocator.registerResourceService(new ResourceService());
        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerRenderService(new RenderService());

        renderer = RenderFactory.createRenderer();

        // Create the slot machine game area
        TerrainFactory terrainFactory = new TerrainFactory(renderer.getCamera());
        slotArea = new SlotMachineGameArea(terrainFactory);
        slotArea.create();

        snapCameraBottomLeft();
    }


    @Override
    public void render(float delta) {
        // Update all entities and render them
        ServiceLocator.getEntityService().update();
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
        snapCameraBottomLeft();
        if (slotArea != null) slotArea.resize();
    }

    private void snapCameraBottomLeft() {
        var cam = renderer.getCamera();
        float vw = cam.getCamera().viewportWidth;
        float vh = cam.getCamera().viewportHeight;
        cam.getEntity().setPosition(vw / 2f, vh / 2f);
    }

    @Override
    public void dispose() {
        // Clean up renderer, area, and services
        renderer.dispose();
        slotArea.dispose();
        ServiceLocator.getEntityService().dispose();
        ServiceLocator.getRenderService().dispose();
        ServiceLocator.getResourceService().dispose();
        ServiceLocator.clear();
    }
}

