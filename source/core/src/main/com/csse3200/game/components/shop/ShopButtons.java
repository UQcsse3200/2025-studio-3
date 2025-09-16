package com.csse3200.game.components.shop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A UI component for displaying the HUD with profile button and gold display. */
public class ShopButtons extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(ShopButtons.class);
  private static final float Z_INDEX = 2f;
  private static final int ICON_SIZE = 40;
  private static final int BACK_BUTTON_SIZE = 50;
  private Label goldLabel;
  private boolean actorsCreated = false;

  @Override
  public void create() {
    super.create();
    logger.debug("ShopButtons created - stage available: {}", stage != null);

    entity
        .getEvents()
        .addListener(
            "purchased",
            () -> {
              logger.info("Item purchased");
              updateGoldDisplay();
            });
  }

  @Override
  public void update() {
    super.update();

    // Create actors on first update when stage is definitely available
    if (!actorsCreated && stage != null) {
      addActors();
      actorsCreated = true;
    }
  }

  private void addActors() {
    logger.debug("Adding ShopButtons actors to stage");
    createHudElements();
  }

  private void createHudElements() {
    if (stage == null) {
      logger.warn("Stage is null, cannot create HUD elements");
      return;
    }

    float hudMargin = 20f;
    float elementSpacing = 60f;

    // Position elements in top right corner
    float rightEdge = stage.getWidth() - hudMargin;
    float topEdge = stage.getHeight() - hudMargin;

    // Create profile button
    createBackButton();

    // Create gold display (to the left of profile button)
    float goldDisplayX = rightEdge - BACK_BUTTON_SIZE - elementSpacing - 100f;
    createGoldDisplay(goldDisplayX, topEdge - ICON_SIZE);
  }

  private void createBackButton() {
    // Create close button using close-icon.png
    ImageButton closeButton = new ImageButton(
        new TextureRegionDrawable(
            ServiceLocator.getGlobalResourceService().getAsset("images/close-icon.png", Texture.class)));
    
    // Position in top left with 20f padding
    closeButton.setSize(60f, 60f);
    closeButton.setPosition(
        20f,  // 20f padding from left
        stage.getHeight() - 60f - 20f  // 20f padding from top
    );

    // Add click listener
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            entity.getEvents().trigger("back");
          }
        });

    stage.addActor(closeButton);
  }

  private void createGoldDisplay(float x, float y) {
    // Create gold icon
    Texture goldTexture =
        ServiceLocator.getGlobalResourceService().getAsset("images/coins.png", Texture.class);

    Image goldIcon = new Image(goldTexture);
    goldIcon.setPosition(x, y);
    goldIcon.setSize(ICON_SIZE, ICON_SIZE);
    stage.addActor(goldIcon);

    goldLabel = new Label("100", skin, "coins");
    goldLabel.setPosition(x + ICON_SIZE + 5f, y + (ICON_SIZE - goldLabel.getHeight()) / 2f);
    stage.addActor(goldLabel);

    // Initialize gold display
    updateGoldDisplay();
  }

  /** Updates the gold display with current wallet amount */
  public void updateGoldDisplay() {
    try {
      // Get coins from profile wallet
      int coins = ServiceLocator.getProfileService().getProfile().getWallet().getCoins();
      goldLabel.setText(String.valueOf(coins));
    } catch (Exception e) {
      logger.warn("Could not get coins from profile: {}", e.getMessage());
      goldLabel.setText("0");
    }
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Do nothing, handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    super.dispose();
    actorsCreated = false;
  }
}
