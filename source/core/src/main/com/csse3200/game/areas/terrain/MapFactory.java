package com.csse3200.game.areas.terrain;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.terrain.TerrainComponent;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.areas.terrain.TerrainFactory.TerrainType;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating and managing maps.
 */
public class MapFactory {
    private static final Logger logger = LoggerFactory.getLogger(MapFactory.class);

    /**
     * Data container for map-specific assets.
     */
    private static class MapAssets {
        String[] textures;
        String[] atlases;
        String[] sounds;
        String[] music;
        TerrainType terrainType;

        MapAssets(String[] textures, String[] atlases, String[] sounds, String[] music, TerrainType terrainType) {
            this.textures = textures;
            this.atlases = atlases;
            this.sounds = sounds;
            this.music = music;
            this.terrainType = terrainType;
        }
    }


    private static final Map<String, MapAssets> maps = new HashMap<>();

    static {

        maps.put("level1", new MapAssets(
            new String[] {
                "images/box_boy_leaf.png",
                "images/level-1-map-v1.png",
                "images/ghost_king.png",
                "images/ghost_1.png",
                "images/olive_tile.png",
                "images/green_tile.png",
                "images/box_boy.png",
                "images/selected_star.png"
            },
            new String[] {
                "images/ghost.atlas",
                "images/ghostKing.atlas",
                "images/robot_placeholder.atlas"
            },
            new String[] {
                "sounds/Impact4.ogg"
            },
            new String[] {
                "sounds/BGM_03_mp3.mp3"
            },
            TerrainType.LEVEL_ONE_MAP
        ));

        // maps.put("level2", new MapAssets(
        //     new String[] {"images/level-2-map.png"},
        //     new String[] {"images/level2.atlas"},
        //     new String[] {"sounds/Impact5.ogg"},
        //     new String[] {"sounds/BGM_04_mp3.mp3"},
        //     TerrainType.LEVEL_TWO_MAP
        // ));
    }

    /**
     * Loads all assets for the given map into the ResourceService.
     */
    public static void loadAssets(String mapName) {
        if (!maps.containsKey(mapName)) {
            throw new IllegalArgumentException("Unknown map: " + mapName);
        }
        MapAssets assets = maps.get(mapName);
        ResourceService resourceService = ServiceLocator.getResourceService();

        resourceService.loadTextures(assets.textures);
        resourceService.loadTextureAtlases(assets.atlases);
        resourceService.loadSounds(assets.sounds);
        resourceService.loadMusic(assets.music);

        // Wait until loading is complete (could be replaced with loading screen)
        while (!resourceService.loadForMillis(10)) {
            logger.info("Loading map {}... {}%", mapName, resourceService.getProgress());
        }
    }

    /**
     * Unloads all assets for the given map from the ResourceService.
     */
    public static void unloadAssets(String mapName) {
        if (!maps.containsKey(mapName)) {
            throw new IllegalArgumentException("Unknown map: " + mapName);
        }
        MapAssets assets = maps.get(mapName);
        ResourceService resourceService = ServiceLocator.getResourceService();

        resourceService.unloadAssets(assets.textures);
        resourceService.unloadAssets(assets.atlases);
        resourceService.unloadAssets(assets.sounds);
        resourceService.unloadAssets(assets.music);
    }

    /**
     * Creates the terrain entity for the given map.
     *
     * @param mapName the map key (e.g., "level1")
     * @param terrainFactory the terrain factory used to build terrain
     * @return a fully configured map entity
     */
    public static Entity createMap(String mapName, TerrainFactory terrainFactory) {
        if (!maps.containsKey(mapName)) {
            throw new IllegalArgumentException("Unknown map: " + mapName);
        }
        MapAssets assets = maps.get(mapName);

        TerrainComponent terrain = terrainFactory.createTerrain(assets.terrainType);

        Entity mapEntity = new Entity().addComponent(terrain);

        float tileWidth = terrain.getTileSize();
        float tileHeight = terrain.getTileSize();
        GridPoint2 bounds = terrain.getMapBounds(0);
        float worldWidth = bounds.x * tileWidth;
        float worldHeight = bounds.y * tileHeight;
        mapEntity.setPosition(worldWidth / 2f, worldHeight / 2f);

        return mapEntity;
    }

    /**
     * Gets the background music file path for the given map.
     * Useful for starting playback in LevelGameArea.
     */
    public static String getBackgroundMusic(String mapName) {
        if (!maps.containsKey(mapName)) {
            throw new IllegalArgumentException("Unknown map: " + mapName);
        }
        MapAssets assets = maps.get(mapName);
        return assets.music.length > 0 ? assets.music[0] : null;
    }
}
