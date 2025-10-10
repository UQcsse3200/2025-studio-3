package com.csse3200.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/** Utility class for creating buttons with a consistent style. */
public class ButtonFactory {

  // Load the skin once (replace the path if your skin is stored elsewhere)
  private static final Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

  /**
   * Creates a text button with the given label.
   *
   * @param text The text to display on the button.
   * @return A new Button instance.
   */
  public static Button createButton(String text) {
    return new TextButton(text, skin);
  }
}
