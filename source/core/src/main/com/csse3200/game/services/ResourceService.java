package com.csse3200.game.services;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for loading resources, e.g. textures, texture atlases, sounds, music, etc. Add new load
 * methods when new types of resources are added to the game.
 */
public class ResourceService implements Disposable {
  private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
  private final AssetManager assetManager;
  private final Map<String, FreeTypeFontGenerator> fontGenerators;

  /** Initialise this ResourceService to use the default AssetManager. */
  public ResourceService() {
    this(new AssetManager());
  }

  /**
   * Initialise this ResourceService to use the provided AssetManager.
   *
   * @param assetManager AssetManager to use in this service.
   * @requires assetManager != null
   */
  public ResourceService(AssetManager assetManager) {
    this.assetManager = assetManager;
    this.fontGenerators = new HashMap<>();
  }

  /**
   * Load an asset from a file.
   *
   * @param filename Asset path
   * @param type Class to load into
   * @param <T> Type of class to load into
   * @return Instance of class loaded from path
   * @see AssetManager#get(String, Class)
   */
  public <T> T getAsset(String filename, Class<T> type) {
    return assetManager.get(filename, type);
  }

  /**
   * Check if an asset has been loaded already
   *
   * @param resourceName path of the asset
   * @param type Class type of the asset
   * @param <T> Type of the asset
   * @return true if asset has been loaded, false otherwise
   * @see AssetManager#contains(String)
   */
  public <T> boolean containsAsset(String resourceName, Class<T> type) {
    return assetManager.contains(resourceName, type);
  }

  /**
   * Returns the loading completion progress as a percentage.
   *
   * @return progress
   */
  public int getProgress() {
    return (int) (assetManager.getProgress() * 100);
  }

  /**
   * Blocking call to load all assets.
   *
   * @see AssetManager#finishLoading()
   */
  public void loadAll() {
    logger.debug("[ResourceService] Loading all assets");
    try {
      assetManager.finishLoading();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("[ResourceService] Could not load all assets", e);
      // Log more details about what failed
      logger.error("[ResourceService] AssetManager errors: {}", assetManager.getDiagnostics());
    }
  }

  /**
   * Loads assets for the specified duration in milliseconds.
   *
   * @param duration duration to load for
   * @return finished loading
   * @see AssetManager#update(int)
   */
  public boolean loadForMillis(int duration) {
    logger.debug("[ResourceService] Loading assets for {} ms", duration);
    try {
      return assetManager.update(duration);
    } catch (Exception e) {
      logger.error("[ResourceService] Could not load assets for {} ms", duration, e);
    }
    return assetManager.isFinished();
  }

  /**
   * Clears all loaded assets and assets in the preloading queue.
   *
   * @see AssetManager#clear()
   */
  public void clearAllAssets() {
    logger.debug("[ResourceService] Clearing all assets");
    assetManager.clear();
  }

  /**
   * Loads a single asset into the asset manager.
   *
   * @param assetName asset name
   * @param type asset type
   * @param <T> type
   */
  private <T> void loadAsset(String assetName, Class<T> type) {
    logger.debug("[ResourceService] Loading {}: {}", type.getSimpleName(), assetName);
    try {
      assetManager.load(assetName, type);
    } catch (Exception e) {
      logger.error(
          "[ResourceService] Could not load {}: {} - {}",
          type.getSimpleName(),
          assetName,
          e.getMessage());
    }
  }

  /**
   * Loads multiple assets into the asset manager.
   *
   * @param assetNames list of asset names
   * @param type asset type
   * @param <T> type
   */
  private <T> void loadAssets(String[] assetNames, Class<T> type) {
    for (String resource : assetNames) {
      loadAsset(resource, type);
    }
  }

  /**
   * Loads a list of texture assets into the asset manager.
   *
   * @param textureNames texture filenames
   */
  public void loadTextures(String[] textureNames) {
    loadAssets(textureNames, Texture.class);
  }

  /**
   * Loads a list of texture atlas assets into the asset manager.
   *
   * @param textureAtlasNames texture atlas filenames
   */
  public void loadTextureAtlases(String[] textureAtlasNames) {
    loadAssets(textureAtlasNames, TextureAtlas.class);
  }

  /**
   * Loads a list of sounds into the asset manager.
   *
   * @param soundNames sound filenames
   */
  public void loadSounds(String[] soundNames) {
    loadAssets(soundNames, Sound.class);
  }

  /**
   * Loads a list of music assets into the asset manager.
   *
   * @param musicNames music filenames
   */
  public void loadMusic(String[] musicNames) {
    loadAssets(musicNames, Music.class);
  }

  /**
   * Loads FreeType font generators from TTF files. This allows for dynamic font generation with
   * custom sizes and parameters.
   *
   * @param fontPath path to the TTF font file
   * @param key key to store the font generator
   */
  public void loadFont(String fontPath, String key) {
    try {
      if (!fontGenerators.containsKey(key)) {
        fontGenerators.put(
            key, new FreeTypeFontGenerator(com.badlogic.gdx.Gdx.files.internal(fontPath)));
        logger.debug("[ResourceService] Loaded FreeType font generator: {}", fontPath);
      }
    } catch (Exception e) {
      logger.error("[ResourceService] Failed to load FreeType font: {}", fontPath, e);
    }
  }

  /**
   * Generates a BitmapFont from a loaded FreeType font generator.
   *
   * @param key key to the font generator
   * @param size font size in pixels
   * @return generated BitmapFont, or null if the font generator is not loaded
   */
  public BitmapFont generateFreeTypeFont(String key, int size) {
    return generateFreeTypeFont(key, size, null);
  }

  /**
   * Generates a BitmapFont from a loaded FreeType font generator with custom parameters.
   *
   * @param key key to the font generator
   * @param size font size in pixels
   * @param parameter custom font generation parameters (can be null for defaults)
   * @return generated BitmapFont, or null if the font generator is not loaded
   */
  public BitmapFont generateFreeTypeFont(
      String key, int size, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
    FreeTypeFontGenerator generator = fontGenerators.get(key);
    if (generator == null) {
      logger.error(
          "[ResourceService] FreeType font generator not found for: {}. Make sure to call loadFreeTypeFonts first.",
          key);
      return null;
    }

    try {
      FreeTypeFontGenerator.FreeTypeFontParameter param = parameter;
      if (param == null) {
        param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = size;
      }

      BitmapFont font = generator.generateFont(param);
      logger.debug("[ResourceService] Generated FreeType font: {} with size {}", key, size);
      return font;
    } catch (Exception e) {
      logger.error("[ResourceService] Failed to generate font from: {} with size {}", key, size, e);
      return null;
    }
  }

  /**
   * Check if a FreeType font generator has been loaded for the given path.
   *
   * @param key key to the font generator
   * @return true if the font generator is loaded, false otherwise
   */
  public boolean containsFreeTypeFont(String key) {
    return fontGenerators.containsKey(key);
  }

  /**
   * Unloads a list of assets from the asset manager.
   *
   * @param assetNames list of asset names
   */
  public void unloadAssets(String[] assetNames) {
    for (String assetName : assetNames) {
      logger.debug("[ResourceService] Unloading {}", assetName);
      try {
        assetManager.unload(assetName);
      } catch (Exception e) {
        logger.error("[ResourceService] Could not unload {}", assetName);
      }
    }
  }

  @Override
  public void dispose() {
    // Dispose all FreeType font generators
    for (Map.Entry<String, FreeTypeFontGenerator> entry : fontGenerators.entrySet()) {
      try {
        entry.getValue().dispose();
        logger.debug("[ResourceService] Disposed FreeType font generator: {}", entry.getKey());
      } catch (Exception e) {
        logger.error(
            "[ResourceService] Failed to dispose FreeType font generator: {}", entry.getKey(), e);
      }
    }
    fontGenerators.clear();
    assetManager.clear();
  }
}
