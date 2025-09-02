package com.csse3200.game.components.shop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A UI component for displaying the HUD with profile button and gold display.
 */
public class ShopButtons extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(ShopButtons.class);
  private static final float Z_INDEX = 2f;
  private static final int ICON_SIZE = 40;
  private static final int BACK_BUTTON_SIZE = 50;
  private Label goldLabel;
  private TextButton backButton;
  private Image goldIcon;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    createHudElements();
  }

  private void createHudElements() {
    float hudMargin = 20f;
    float elementSpacing = 10f;

    // Position elements in top right corner
    float rightEdge = stage.getWidth() - hudMargin;
    float topEdge = stage.getHeight() - hudMargin;

    // Create profile button
    createBackButton(rightEdge - (BACK_BUTTON_SIZE * 2), topEdge - BACK_BUTTON_SIZE);

    // Create gold display (to the left of profile button)
    float goldDisplayX = rightEdge - BACK_BUTTON_SIZE - elementSpacing - 100f;
    createGoldDisplay(goldDisplayX, topEdge - ICON_SIZE);
  }

  private void createBackButton(float x, float y) {
    backButton = new TextButton("Back", skin);
    backButton.setPosition(x, y);
    backButton.setSize(BACK_BUTTON_SIZE * 2, BACK_BUTTON_SIZE);

    // Add click listener
    backButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent changeEvent, Actor actor) {
        logger.debug("Back button clicked");
        entity.getEvents().trigger("back");
      }
    });

    stage.addActor(backButton);
  }

  private void createGoldDisplay(float x, float y) {
    // Create gold icon
    Texture goldTexture = ServiceLocator.getResourceService()
        .getAsset("images/coins.png", Texture.class);

    goldIcon = new Image(goldTexture);
    goldIcon.setPosition(x, y);
    goldIcon.setSize(ICON_SIZE, ICON_SIZE);
    stage.addActor(goldIcon);

    goldLabel = new Label("100", skin, "coins");
    goldLabel.setPosition(x + ICON_SIZE + 5f, y + (ICON_SIZE - goldLabel.getHeight()) / 2f);
    stage.addActor(goldLabel);

    // Initialize gold display
    updateGoldDisplay();
  }

  /**
   * Updates the gold display with current wallet amount
   */
  public void updateGoldDisplay() {
    try {
      // Get coins from profile wallet
      int coins = Persistence.profile().wallet().getCoins();
      goldLabel.setText(String.valueOf(coins));
    } catch (Exception e) {
      logger.warn("Could not get coins from profile: {}", e.getMessage());
      goldLabel.setText("0");
    }
  }

  /**
   * Manual method to set gold amount (alternative to profile integration)
   */
  public void setGold(int amount) {
    goldLabel.setText(String.valueOf(amount));
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Draw is handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    super.dispose();
  }
}