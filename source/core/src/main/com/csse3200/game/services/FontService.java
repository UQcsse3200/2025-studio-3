package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Service for managing fonts, including TTF font loading and caching.
 * Provides methods to load TTF fonts with different sizes and styles.
 */
public class FontService implements Disposable {
    private final ObjectMap<String, BitmapFont> fontCache = new ObjectMap<>();
    private final ObjectMap<String, FreeTypeFontGenerator> generatorCache = new ObjectMap<>();

    /**
     * Loads a TTF font from the assets directory with specified parameters.
     * 
     * @param fontPath Path to the TTF file in assets (e.g., "fonts/arial.ttf")
     * @param size Font size in pixels
     * @return BitmapFont instance
     */
    public BitmapFont loadTTFFont(String fontPath, int size) {
        return loadTTFFont(fontPath, size, new FreeTypeFontParameter());
    }

    /**
     * Loads a TTF font with custom parameters.
     * 
     * @param fontPath Path to the TTF file in assets
     * @param size Font size in pixels
     * @param parameters Custom font parameters (color, border, etc.)
     * @return BitmapFont instance
     */
    public BitmapFont loadTTFFont(String fontPath, int size, FreeTypeFontParameter parameters) {
        String cacheKey = fontPath + "_" + size + "_" + parameters.hashCode();
        
        // Check if font is already cached
        if (fontCache.containsKey(cacheKey)) {
            return fontCache.get(cacheKey);
        }

        try {
            // Load or get cached generator
            FreeTypeFontGenerator generator = getGenerator(fontPath);
            
            // Set font size
            parameters.size = size;
            
            // Generate font
            BitmapFont font = generator.generateFont(parameters);
            fontCache.put(cacheKey, font);
            
            return font;
        } catch (Exception e) {
            Gdx.app.error("FontService", "Failed to load TTF font: " + fontPath, e);
            // Return default font as fallback
            return new BitmapFont();
        }
    }

    /**
     * Gets or loads a FreeType font generator for the given font path.
     */
    private FreeTypeFontGenerator getGenerator(String fontPath) {
        if (generatorCache.containsKey(fontPath)) {
            return generatorCache.get(fontPath);
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        generatorCache.put(fontPath, generator);
        return generator;
    }

    /**
     * Loads a TTF font with common styling options.
     * 
     * @param fontPath Path to the TTF file
     * @param size Font size
     * @param color Font color (RGBA)
     * @param borderWidth Border width (0 for no border)
     * @param borderColor Border color (RGBA)
     * @return BitmapFont instance
     */
    public BitmapFont loadStyledTTFFont(String fontPath, int size, 
                                       com.badlogic.gdx.graphics.Color color,
                                       int borderWidth, 
                                       com.badlogic.gdx.graphics.Color borderColor) {
        FreeTypeFontParameter parameters = new FreeTypeFontParameter();
        parameters.color = color;
        parameters.borderWidth = borderWidth;
        parameters.borderColor = borderColor;
        
        return loadTTFFont(fontPath, size, parameters);
    }

    /**
     * Gets a cached font or returns null if not found.
     */
    public BitmapFont getCachedFont(String fontPath, int size) {
        String cacheKey = fontPath + "_" + size;
        return fontCache.get(cacheKey);
    }

    /**
     * Clears the font cache and disposes of all cached fonts.
     */
    public void clearCache() {
        for (BitmapFont font : fontCache.values()) {
            font.dispose();
        }
        fontCache.clear();
        
        for (FreeTypeFontGenerator generator : generatorCache.values()) {
            generator.dispose();
        }
        generatorCache.clear();
    }

    @Override
    public void dispose() {
        clearCache();
    }
}
