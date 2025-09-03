package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.areas.terrain.TerrainFactory.TerrainType;
import com.csse3200.game.components.InventoryUnitInputComponent;
import com.csse3200.game.components.tile.TileStatusComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.GridFactory;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.Gdx;
import com.csse3200.game.ui.DraggableCharacter;

import com.badlogic.gdx.Input;
public class LevelGameArea extends GameArea implements AreaAPI {
    private static final Logger logger = LoggerFactory.getLogger(LevelGameArea.class);
    private static final String[] levelTextures = {
            "images/box_boy_leaf.png",
            "images/level-1-map-v1.png",
            "images/ghost_king.png",
            "images/ghost_1.png",
            "images/olive_tile.png",
            "images/green_tile.png",
            "images/box_boy.png"
    };

    private static final String[] levelTextureAtlases = {
            "images/ghost.atlas", "images/ghostKing.atlas"
    };

    private static final String[] levelSounds = {"sounds/Impact4.ogg"};
    private static final String backgroundMusic = "sounds/BGM_03_mp3.mp3";
    private static final String[] levelMusic = {backgroundMusic};

    private final TerrainFactory terrainFactory;

    // Offset values from the bottom left corner of the screen for the grid's starting point
    private static final float X_OFFSET = 3.3f;
    private static final float Y_OFFSET = 2f;

    // Space occupied by the grid within the level game screen
    private static final float GRID_HEIGHT = 7f;
    private static final float GRID_WIDTH = 14f;
    private static final int LEVEL_ONE_ROWS = 5;
    private static final int LEVEL_ONE_COLS = 10;
    private static final float SCALE = GRID_HEIGHT / LEVEL_ONE_ROWS;
    private static final float INV_START_X = X_OFFSET;
    private static final float INV_START_Y = Y_OFFSET + 6 * SCALE;

    private LevelGameGrid grid;
    private Entity[] spawned_units;

    private Entity unit_selected;

    /**
     * Initialise this LevelGameArea to use the provided TerrainFactory.
     * @param terrainFactory TerrainFactory used to create the terrain for the GameArea.
     */
    public LevelGameArea(TerrainFactory terrainFactory) {
        super();
        this.terrainFactory = terrainFactory;
        unit_selected = null; // None selected at level load
        spawned_units = new Entity[LEVEL_ONE_ROWS * LEVEL_ONE_COLS];
    }

    @Override
    public void create() {
        loadAssets();

        displayUI();

        spawnMap();
        spawnGrid(LEVEL_ONE_ROWS, LEVEL_ONE_COLS);
        placeInventoryUnit(1, "images/ghost_1.png"); // start at one for 0 to represent none selected.
        testUI_1();

        playMusic();

    }

    private void testUI_1() {
        Entity ui = new Entity();
        DraggableCharacter dragUI = new DraggableCharacter(this);
        dragUI.setTexture("images/ghost_1.png");
        dragUI.setOffsets(0f, 500f);
        dragUI.setScale(0.1f);
        ui.addComponent(dragUI);
        spawnEntity(ui);

    }



    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(levelTextures);
        resourceService.loadTextureAtlases(levelTextureAtlases);
        resourceService.loadSounds(levelSounds);
        resourceService.loadMusic(levelMusic);

        while (!resourceService.loadForMillis(10)) {
            // This could be upgraded to a loading screen
            logger.info("Loading... {}%", resourceService.getProgress());
        }
    }

    private void displayUI() {
        Entity ui = new Entity();
        ui.addComponent(new GameAreaDisplay("Level One"));
        spawnEntity(ui);
    }

    private void spawnMap() {
        logger.debug("Spawning level one map");

        // Create the background terrain (single image map)
        terrain = terrainFactory.createTerrain(TerrainType.LEVEL_ONE_MAP);

        // Wrap in an entity
        Entity mapEntity = new Entity().addComponent(terrain);

        // Compute world size
        float tileWidth = terrain.getTileSize();
        float tileHeight = terrain.getTileSize();
        GridPoint2 bounds = terrain.getMapBounds(0);
        float worldWidth = bounds.x * tileWidth;
        float worldHeight = bounds.y * tileHeight;

        mapEntity.setPosition(worldWidth / 2f, worldHeight / 2f);

        spawnEntity(mapEntity);
    }

    private void spawnGrid(int rows, int cols) {
        grid = new LevelGameGrid(rows, cols);
        for (int i = 0; i < rows * cols; i++) {
            Entity tile;
            float tileX = X_OFFSET + SCALE * (i % cols);
            float tileY = Y_OFFSET + SCALE * (float) (i / cols);
            // logic for alternating tile images
            if ((i / cols) % 2 == 1) {
                tile = GridFactory.createTile(i % 2, SCALE, tileX, tileY, this);
            } else {
                tile = GridFactory.createTile(1 - (i % 2), SCALE, tileX, tileY, this);
            }
            tile.setPosition(tileX, tileY);
            tile.getComponent(TileStatusComponent.class).set_position(i);
            grid.addTile(i, tile);
            spawnEntity(tile);
        }
    }

    private void placeInventoryUnit(int pos, String image) {
        Entity unit = new Entity()
                .addComponent(new InventoryUnitInputComponent(this))
                .addComponent(new TextureRenderComponent(image));
        unit.setPosition(INV_START_X  + (pos - 1) * SCALE, INV_START_Y);
        spawnEntity(unit);
    }

    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(levelTextures);
        resourceService.unloadAssets(levelTextureAtlases);
        resourceService.unloadAssets(levelSounds);
        resourceService.unloadAssets(levelMusic);
    }


    @Override
    public void dispose() {
        super.dispose();
        ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class).stop();
        this.unloadAssets();
    }

    private void playMusic() {
        Music music = ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
    }
    @Override
    public LevelGameGrid getGrid() {
        return grid;
    }

    @Override
    public Entity getSelectedUnit(){
        return unit_selected;
    }

    @Override
    public void setSelectedUnit(Entity unit) {
        unit_selected = unit;
        if (unit == null) {
            logger.info("Unit deselected");
        } else {
            logger.info("Unit selected");
        }
    }

    @Override
    public void spawnUnit(int position){
        Entity unit = new Entity();
        unit.addComponent(new TextureRenderComponent("images/ghost_1.png"));
        float tileX = X_OFFSET + SCALE * (position % LEVEL_ONE_COLS);
        float tileY = Y_OFFSET + SCALE * (float) (position / LEVEL_ONE_COLS);
        unit.setPosition(tileX, tileY);
        spawned_units[position] = unit;
        unit.getComponent(TextureRenderComponent.class).scaleEntity();
        spawnEntity(unit);
        logger.info("Unit spawned at position {}", position);
    }

    @Override
    public void removeUnit(int position){
        spawned_units[position].dispose();
        logger.info("Unit deleted at position {}", position);
    }
}


