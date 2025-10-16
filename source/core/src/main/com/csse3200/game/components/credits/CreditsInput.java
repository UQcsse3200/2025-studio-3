package com.csse3200.game.components.credits;

import com.badlogic.gdx.Input;
import com.csse3200.game.GdxGame;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.services.ServiceLocator;

public class CreditsInput extends InputComponent {
  private final GdxGame game;

  public CreditsInput(GdxGame game) {
    super(1000);
    this.game = game;
  }

  @Override
  public boolean keyDown(int keyCode) {
    int skipKey = ServiceLocator.getSettingsService().getSettings().getSkipButton();
    if (keyCode == skipKey) {
      entity.getComponent(CreditsDisplay.class).setSpeed(400f);
      return true;
    }
    if (keyCode == Input.Keys.ESCAPE) {
      game.setScreen(GdxGame.ScreenType.MAIN_MENU);
      return true;
    }
    return false;
  }

  @Override
  public boolean keyUp(int keyCode) {
    int skipKey = ServiceLocator.getSettingsService().getSettings().getSkipButton();
    if (keyCode == skipKey) {
      entity.getComponent(CreditsDisplay.class).setSpeed(60f);
      return true;
    }
    return false;
  }
}
