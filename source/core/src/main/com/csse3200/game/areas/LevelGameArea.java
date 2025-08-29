package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.areas.terrain.TerrainFactory.TerrainType;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.ObstacleFactory;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import com.csse3200.game.ui.DragAndDropDemo;
import com.csse3200.game.utils.math.RandomUtils;
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
        spawnTiles();
        testUI_1();

        testUI_2();
        testUI_3();

        playMusic();

    }

    private void testUI_1() {
//    Entity dragUI = new Entity();
//    dragUI.addComponent(new com.csse3200.game.ui.DragAndDropDemo());
//    spawnEntity(dragUI);
        Entity ui = new Entity();
        DragAndDropDemo dragUI = new DragAndDropDemo();
        dragUI.setTexture("images/ghost_1.png"); // choose which image
        dragUI.setOffsets(0f, 500f); // bottom-right-ish
        dragUI.setScale(0.1f); // smaller
        ui.addComponent(dragUI);
        spawnEntity(ui);

    }

    private void testUI_2() {
//    Entity dragUI = new Entity();
//    dragUI.addComponent(new com.csse3200.game.ui.DragAndDropDemo());
//    spawnEntity(dragUI);
        Entity ui = new Entity();
        DragAndDropDemo dragUI = new DragAndDropDemo();
        dragUI.setTexture("images/box_boy.png"); // choose which image
        dragUI.setOffsets(0f, 0f); // bottom-right-ish
        dragUI.setScale(0.1f); // smaller
        ui.addComponent(dragUI);
        spawnEntity(ui);

    }

    private void testUI_3() {
//    Entity dragUI = new Entity();
//    dragUI.addComponent(new com.csse3200.game.ui.DragAndDropDemo());
//    spawnEntity(dragUI);
        Entity ui = new Entity();
        DragAndDropDemo dragUI = new DragAndDropDemo();
        dragUI.setTexture("images/ghost_1.png"); // choose which image
        dragUI.setOffsets(0f, 250f); // bottom-right-ish
        dragUI.setScale(0.1f); // smaller
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


    private void playMusic() {
        Music music = ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
    }

    private void spawnTiles() {
        for (int i = 0; i < 50; i++) {
            Entity tile;
            if ((i / 10) % 2 == 1) {
                tile = ObstacleFactory.createTile(i % 2);
            } else {
                tile = ObstacleFactory.createTile(1- (i % 2));
            }
            //need to make scale dynamic to screen size
            float scale = 1.4F;
            tile.setPosition((float) (2.9 + scale * (i % 10)), (float) (1.45 + scale * (i / 10)));
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


