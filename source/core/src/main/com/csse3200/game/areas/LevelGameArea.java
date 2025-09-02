package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.areas.terrain.TerrainFactory.TerrainType;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.GridFactory;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class LevelGameArea extends GameArea{
    private static final Logger logger = LoggerFactory.getLogger(LevelGameArea.class);
    private static final String[] levelTextures = {
            "images/box_boy_leaf.png",
            "images/level-1-map-v1.png",
            "images/ghost_king.png",
            "images/ghost_1.png",
            "images/olive_tile.png",
            "images/green_tile.png",
            "images/box_boy.png",
            "images/robot_placeholder.png"
    };

    private static final String[] levelTextureAtlases = {
            "images/ghost.atlas", "images/ghostKing.atlas", "images/robot_placeholder.atlas"
    };

    private static final String[] levelSounds = {"sounds/Impact4.ogg"};
    private static final String backgroundMusic = "sounds/BGM_03_mp3.mp3";
    private static final String[] levelMusic = {backgroundMusic};

    private final TerrainFactory terrainFactory;

    // Offset values from the bottom left corner of the screen for the grid's starting point
    private final float xOffset = 2.9f;
    private final float yOffset = 1.45f;

    // Space occupied by the grid within the level game screen
    private final float gridHeight = 7f;
    private final float gridWidth = 14f;

    private final int levelOneRows = 5;
    private final int levelOneCols = 10;

    private final int levelTwoRows = 7;
    private final int levelTwoCols = 14;

    private LevelGameGrid grid;
    private final ArrayList<Entity> robots = new ArrayList<>(); // keep track of robots

    /**
     * Initialise this LevelGameArea to use the provided TerrainFactory.
     * @param terrainFactory TerrainFactory used to create the terrain for the GameArea.
     */
    public LevelGameArea(TerrainFactory terrainFactory) {
        super();
        this.terrainFactory = terrainFactory;
    }

    @Override
    public void create() {
        loadAssets();

        displayUI();

        spawnMap();
        float scale = gridHeight / levelOneRows;
        spawnGrid(levelOneRows, levelOneCols, scale);
        //float scale = gridHeight / levelTwoRows;
        //spawnGrid(levelTwoRows, levelTwoCols, scale);
        spawnRobotAtTile(new GridPoint2(9,4), true, true);
        playMusic();

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


    private void playMusic() {
        Music music = ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
    }

    private void spawnGrid(int rows, int cols, float scale) {
        LevelGameGrid grid = new LevelGameGrid(rows, cols);
        for (int i = 0; i < rows * cols; i++) {
            Entity tile;
            float tileX = xOffset + scale * (i % cols);
            float tileY = yOffset + scale * (float)(i / cols);
            // logic for alternating tile images
            if ((i / cols) % 2 == 1) {
                tile = GridFactory.createTile(i % 2, scale, tileX, tileY);
            } else {
                tile = GridFactory.createTile(1 - (i % 2), scale, tileX, tileY);
            }
            tile.setPosition(tileX, tileY);
            grid.addTile(i, tile);
            spawnEntity(tile);
        }
        this.grid = grid;
    }


    /**
     * Check to see if the robot has reached the end
     * Print game over message iff robot has reached the end
     */
    public void checkGameOver() {

        float cellWidth = gridWidth / levelOneCols;  // match the spawnGrid scale
        float cellHeight = gridHeight / levelOneRows;
        // calculate grid position from the world position
        for (Entity robot : robots) {
            Vector2 worldPos = robot.getPosition();
            int gridX = (int) ((worldPos.x - xOffset) / cellWidth);
            int gridY = (int) ((worldPos.y - yOffset) / cellHeight);

            //check iff robot has reached the left boundary column (i.e the end)
            if (gridX <= 0) {
                // logger.info("Game Over! Robot reached the end at position: {},{}", gridX, gridY);
                System.out.println("Game Over! Robot reached the end at position");
                return;
            }
        }

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
}
