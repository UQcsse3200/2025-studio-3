package com.csse3200.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.rendering.RenderComponent;
import com.csse3200.game.services.ServiceLocator;

/** A generic component for rendering onto the ui. */
public abstract class UIComponent extends RenderComponent {
  private static final int UI_LAYER = 2;
  protected static final Skin skin = new Skin(Gdx.files.internal("skin/tdwfb.json"));
  protected static final UIFactory ui = new UIFactory(skin, Settings.UIScale.MEDIUM);
  protected Stage stage;

  @Override
  public void create() {
    super.create();
    stage = ServiceLocator.getRenderService().getStage();
    if (getEntity() != null) {
      entity.getEvents().addListener("resize", this::resize);
    }
    ui.setUIScale(ServiceLocator.getSettingsService().getSettings().getCurrentUIScale());
  }

  @Override
  public int getLayer() {
    return UI_LAYER;
  }

  @Override
  public float getZIndex() {
    return 1f;
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // For most UI components, drawing is handled by the stage
  }

  /** Handle the resize event. This is called when the screen is resized. */
  protected void resize() {
    // To be implemented by subclasses.
    // ... should something else be here?
    ui.setUIScale(ServiceLocator.getSettingsService().getSettings().getCurrentUIScale());
  }
}
