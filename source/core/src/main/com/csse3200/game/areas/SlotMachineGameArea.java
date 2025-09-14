package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.GridFactory;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Slot Machine level area:
 * - Grid/layout/scaling are identical to LevelGameArea
 * - No robots/waves/inventory for now
 */
public class SlotMachineGameArea extends GameArea implements AreaAPI {
    private static final Logger logger = LoggerFactory.getLogger(SlotMachineGameArea.class);

    // Same grid size as LevelGameArea
    private static final int ROWS = 5;
    private static final int COLS = 10;

    // Minimal resources for this level (extend as needed)
    private static final String BG_MUSIC = "sounds/BGM_03_mp3.mp3";
    private static final String[] levelTextures = {
            "images/level-1-map-v2.png",     // Map background
            "images/selected_star.png"       // Selection marker
    };
    private static final String[] levelMusic = {BG_MUSIC};

    private final TerrainFactory terrainFactory;

    // Scaling and offset values (identical to LevelGameArea)
    private float xOffset;
    private float yOffset;
    private float tileSize;
    private float invStartX;
    private float invY;
    private float invSelectedY;
    private float stageHeight;
    private float stageToWorldRatio;

    private LevelGameGrid grid;
    private Entity selectedUnit;
    private Entity selectionStar;

    public SlotMachineGameArea(TerrainFactory terrainFactory) {
        super();
        this.terrainFactory = terrainFactory;
        setScaling();  // Apply scaling/offset based on current stage
    }

    /** Same scaling logic as LevelGameArea */
    public void setScaling() {
        stageHeight = ServiceLocator.getRenderService().getStage().getHeight();
        float stageWidth = ServiceLocator.getRenderService().getStage().getWidth();
        stageToWorldRatio = Renderer.GAME_SCREEN_WIDTH / stageWidth;

        // Grid fills most of the screen height
        float gridHeight = stageHeight * 0.8f;
        tileSize = gridHeight / ROWS;

        // Center horizontally
        float gridWidth = tileSize * COLS;
        xOffset = (stageWidth - gridWidth) / 2f;
        yOffset = tileSize;  // leave a margin at bottom
    }


    @Override
    public void create() {
        loadAssets();

        displayUI();     // Title or UI for Slot Machine
        spawnMap();      // Map background (same logic as LevelGameArea)
        spawnGrid(ROWS, COLS); // Playable grid
        playMusic();
    }

    /** Load textures and music */
    private void loadAssets() {
        ResourceService rs = ServiceLocator.getResourceService();
        rs.loadTextures(levelTextures);
        rs.loadMusic(levelMusic);
        rs.loadAll();
    }

    /** Simple UI header for Slot Machine area */
    private void displayUI() {
        Entity ui = new Entity();
        ui.addComponent(new GameAreaDisplay("Slot Machine"));
        spawnEntity(ui);
    }

    /** Spawns a full-screen background image for the slot machine */
    private void spawnMap() {
        Entity mapEntity = new Entity()
                .addComponent(new TextureRenderComponent("images/level-1-map-v2.png"));

        // Stretch to fit the whole screen
        float stageWidth = ServiceLocator.getRenderService().getStage().getWidth();
        float stageHeight = ServiceLocator.getRenderService().getStage().getHeight();
        mapEntity.setScale(stageWidth, stageHeight);

        // Center at (0,0)
        mapEntity.setPosition(0, 0);

        spawnEntity(mapEntity);
    }


    /** Spawns a grid of tiles (identical positioning as LevelGameArea) */
    private void spawnGrid(int rows, int cols) {
        grid = new LevelGameGrid(rows, cols);
        for (int i = 0; i < rows * cols; i++) {
            float tileX = xOffset + tileSize * (i % cols);
            int col = i / cols;
            float tileY = yOffset + tileSize * col;

            Entity tile = GridFactory.createTile(tileSize, tileX, tileY, this);
            tile.setPosition(tileX, tileY);
            tile.getComponent(TileStorageComponent.class).setPosition(i);
            grid.addTile(i, tile);
            spawnEntity(tile);
        }
    }

    /** Starts background music */
    private void playMusic() {
        Music music = ServiceLocator.getResourceService().getAsset(BG_MUSIC, Music.class);
        if (music != null) {
            music.setLooping(true);
            music.setVolume(0.3f);
            music.play();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        ResourceService rs = ServiceLocator.getResourceService();
        Music music = rs.getAsset(BG_MUSIC, Music.class);
        if (music != null) music.stop();
        rs.unloadAssets(levelTextures);
        rs.unloadAssets(levelMusic);
    }

    // === AreaAPI methods ===

    @Override
    public LevelGameGrid getGrid() {
        return grid;
    }

    @Override
    public Entity getSelectedUnit() {
        return selectedUnit;
    }

    @Override
    public void setSelectedUnit(Entity unit) {
        this.selectedUnit = unit;

        if (selectionStar == null) {
            selectionStar = new Entity().addComponent(new TextureRenderComponent("images/selected_star.png"));
            selectionStar.scaleHeight(tileSize / 2f);
            spawnEntity(selectionStar);
        }

        if (unit == null) {
            selectionStar.setPosition(-100f, -100f); // hide off-screen
        } else {
            selectionStar.setPosition(unit.getCenterPosition().x, invSelectedY);
        }
    }

    @Override
    public void spawnUnit(int position) {
        // Not needed for slot machine yet
    }

    @Override
    public void removeUnit(int position) {
        // Not needed for slot machine yet
    }

    @Override
    public float getTileSize() {
        return tileSize;
    }

    @Override
    public GridPoint2 stageToWorld(GridPoint2 pos) {
        float x = pos.x * stageToWorldRatio;
        float y = (stageHeight - pos.y) * stageToWorldRatio;
        return new GridPoint2((int) x, (int) y);
    }

    @Override
    public GridPoint2 worldToStage(GridPoint2 pos) {
        float x = pos.x / stageToWorldRatio;
        float y = stageHeight - (pos.y / stageToWorldRatio);
        return new GridPoint2((int) x, (int) y);
    }

    /** Re-apply scaling when window is resized */
    public void resize() {
        setScaling();
    }
}