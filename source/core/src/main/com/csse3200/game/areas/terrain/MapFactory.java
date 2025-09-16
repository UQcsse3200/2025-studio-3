package com.csse3200.game.areas.terrain;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating game maps as single images instead of tiled maps.
 * Simplifies the map loading process by removing tiling logic.
 */
public class MapFactory {
    private static final Map<String, MapAssets> MAP_ASSETS = new HashMap<>();

    static {
        MAP_ASSETS.put("level1", new MapAssets(
            "images/level-1-map-v1.png",
            "audio/music/level1.mp3"
        ));
    }

    /**
     * Loads all assets required for a specific map
     * @param mapName the name of the map to load assets for
     */
    public static void loadAssets(String mapName) {
        MapAssets assets = MAP_ASSETS.get(mapName);
        if (assets == null) {
            throw new IllegalArgumentException("Unknown map: " + mapName);
        }

        ResourceService resourceService = ServiceLocator.getResourceService();

        resourceService.loadTextures(new String[]{assets.texturePath});

        resourceService.loadMusic(new String[]{assets.musicPath});

        resourceService.loadAll();
    }

    /**
     * Unloads all assets for a specific map
     */
    public static void unloadAssets(String mapName) {
        MapAssets assets = MAP_ASSETS.get(mapName);
        if (assets == null) {
            throw new IllegalArgumentException("Unknown map: " + mapName);
        }

        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(new String[]{assets.texturePath, assets.musicPath});
    }

    /**
     * Creates a map entity for the specified map
     */
    public static Entity createMap(String mapName, TerrainFactory terrainFactory) {
        MapAssets assets = MAP_ASSETS.get(mapName);
        if (assets == null) {
            throw new IllegalArgumentException("Unknown map: " + mapName);
        }

        ResourceService resourceService = ServiceLocator.getResourceService();
        Texture mapTexture = resourceService.getAsset(assets.texturePath, Texture.class);

        Entity mapEntity = new Entity();
        mapEntity.addComponent(new TextureRenderComponent(new TextureRegion(mapTexture).getTexture()));
        return mapEntity;
    }

    /**
     * Gets the background music path for a specific map
     */
    public static String getBackgroundMusic(String mapName) {
        MapAssets assets = MAP_ASSETS.get(mapName);
        if (assets == null) {
            throw new IllegalArgumentException("Unknown map: " + mapName);
        }
        return assets.musicPath;
    }

    /**
     * Helper class to store map asset paths
     */
    private static class MapAssets {
        public final String texturePath;
        public final String musicPath;

        public MapAssets(String texturePath, String musicPath) {
            this.texturePath = texturePath;
            this.musicPath = musicPath;
        }
    }
}