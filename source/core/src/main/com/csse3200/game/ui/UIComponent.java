package com.csse3200.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.csse3200.game.rendering.RenderComponent;
import com.csse3200.game.rendering.Renderable;
import com.csse3200.game.services.ServiceLocator;

/** A generic component for rendering onto the ui. */
public abstract class UIComponent extends RenderComponent implements Renderable {
  private static final int UI_LAYER = 2;
  public static Skin skin;
  private static FreeTypeFontGenerator fontGenerator;
  
  // Static initialization block to initialize skin when class is first loaded
  static {
    initializeSkinWithTTF();
  }
  
  protected Stage stage;

  @Override
  public void create() {
    super.create();
    stage = ServiceLocator.getRenderService().getStage();
  }

  /**
   * Initializes the skin and replaces bitmap fonts with TTF-generated fonts.
   */
  private static void initializeSkinWithTTF() {
    try {
      // Load the default skin
      skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
      
      // Load TTF font generator
      fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/font2.ttf"));
      
      // Generate TTF fonts with different sizes
      BitmapFont ttfSmall = generateTTFFont(16, Color.BLACK);
      BitmapFont ttfMedium = generateTTFFont(18, Color.WHITE);
      BitmapFont ttfLarge = generateTTFFont(26, Color.BLACK);
      BitmapFont ttfTitle = generateTTFFont(32, Color.WHITE);
      
      // Replace existing fonts in the skin
      skin.add("font_small", ttfSmall);
      skin.add("font", ttfMedium);
      skin.add("font_large", ttfLarge);
      skin.add("title", ttfTitle);
      skin.add("button", ttfMedium);
      
      Gdx.app.log("UIComponent", "Successfully loaded skin with TTF fonts");
      
    } catch (Exception e) {
      Gdx.app.error("UIComponent", "Failed to load skin with TTF fonts", e);
      // Fallback to default skin
      skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
    }
  }

  /**
   * Generates a TTF font with specified size and color.
   */
  private static BitmapFont generateTTFFont(int size, Color color) {
    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    parameter.size = size;
    parameter.color = color;
    parameter.borderWidth = 1;
    parameter.borderColor = Color.BLACK;
    
    return fontGenerator.generateFont(parameter);
  }



  @Override
  public int getLayer() {
    return UI_LAYER;
  }

  @Override
  public float getZIndex() {
    return 1f;
  }
}