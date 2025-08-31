package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.areas.terrain.TerrainFactory.TerrainType;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.GridFactory;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LevelGameArea extends GameArea{
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

        float scale = 1.4f;
        spawnTiles(scale);
        playMusic();

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

    private void spawnTiles(float scale) {
        for (int i = 0; i < 50; i++) {
            Entity tile;
            float tileX = (float) (2.9 + scale * (i % 10));
            float tileY = (float) (1.45 + scale * (i / 10));
            // logic for alternating tile images
            if ((i / 10) % 2 == 1) {
                tile = GridFactory.createTile(i % 2, scale, tileX, tileY);
            } else {
                tile = GridFactory.createTile(1- (i % 2), scale, tileX, tileY);
            }
            tile.setPosition(tileX, tileY);
            spawnEntity(tile);
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


