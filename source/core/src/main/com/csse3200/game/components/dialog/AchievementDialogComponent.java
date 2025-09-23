package com.csse3200.game.components.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.TypographyFactory;
import com.csse3200.game.ui.UIComponent;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A specialized dialog component for displaying achievement unlock notifications. */
public class AchievementDialogComponent extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(AchievementDialogComponent.class);
  private static final float DISPLAY_DURATION = 4.0f;
  private static final float ANIMATION_DURATION = 0.5f;
  private String name;
  private String description;
  private int skillPoints;
  private String tier;
  private Window dialog;
  private boolean isVisible = false;
  private Consumer<AchievementDialogComponent> onCompletion;

  /**
   * Creates a new achievement dialog for the specified achievement.
   *
   * @param name the name of the achievement
   * @param description the description of the achievement
   * @param skillPoints the skill points of the achievement
   * @param tier the tier of the achievement
   */
  public AchievementDialogComponent(String name, String description, int skillPoints, String tier) {
    this.name = name;
    this.description = description;
    this.skillPoints = skillPoints;
    this.tier = tier;
  }

  @Override
  public void create() {
    super.create();
    createAchievementDialog();
  }

  /** Creates and configures the achievement dialog window. */
  private void createAchievementDialog() {
    Window.WindowStyle windowStyle = skin.get(Window.WindowStyle.class);
    try {
      Texture achievementTexture =
          ServiceLocator.getGlobalResourceService()
              .getAsset("images/ui/achievement.png", Texture.class);
      if (achievementTexture != null) {
        TextureRegionDrawable achievementBackground = new TextureRegionDrawable(achievementTexture);
        achievementBackground.setMinWidth(400f);
        achievementBackground.setMinHeight(200f);
        Window.WindowStyle customStyle = new Window.WindowStyle(windowStyle);
        customStyle.background = achievementBackground;
        dialog = new Window("", customStyle);
      } else {
        dialog = new Window("", windowStyle);
      }
    } catch (Exception e) {
      logger.warn(
          "[AchievementDialogComponent] Could not load achievement texture, using default background: {}",
          e.getMessage());
      dialog = new Window("", windowStyle);
    }

    dialog.setSize(400f, 200f);
    dialog.setMovable(false);
    dialog.setModal(false); // Don't block interaction

    Table contentTable = new Table();
    contentTable.pad(10f);
    Table textTable = new Table();

    // Achievement unlocked header
    Label headerLabel = TypographyFactory.createSubtitle("Achievement Unlocked!", Color.GOLD);
    headerLabel.setAlignment(Align.left);
    textTable.add(headerLabel).left().row();

    // Achievement name
    Label nameLabel = TypographyFactory.createParagraph(name, Color.WHITE);
    nameLabel.setAlignment(Align.left);
    textTable.add(nameLabel).left().padTop(2f).row();

    // Achievement description
    Label descLabel = TypographyFactory.createCustomSize(description, 14, Color.LIGHT_GRAY);
    descLabel.setWrap(true);
    descLabel.setAlignment(Align.left);
    textTable.add(descLabel).width(320f).left().padTop(2f).row();

    // Tier information
    if (tier != null && !tier.isEmpty()) {
      Label tierLabel = TypographyFactory.createCustomSize("Tier: " + tier, 12, Color.YELLOW);
      tierLabel.setAlignment(Align.left);
      textTable.add(tierLabel).left().padTop(2f).row();
    }

    // Skill points earned with icon
    if (skillPoints > 0) {
      Table skillPointTable = new Table();

      // Try to add skill point icon
      try {
        Texture skillPointTexture =
            ServiceLocator.getGlobalResourceService()
                .getAsset("images/entities/currency/skillpoints.png", Texture.class);
        if (skillPointTexture != null) {
          Drawable skillPointDrawable = new TextureRegionDrawable(skillPointTexture);
          Image skillPointIcon = new Image(skillPointDrawable);
          skillPointTable.add(skillPointIcon).size(12f, 12f).padRight(3f);
        }
      } catch (Exception e) {
        logger.debug(
            "[AchievementDialogComponent] Could not load skill point texture: {}", e.getMessage());
        // Continue without icon
      }

      Label pointsLabel =
          TypographyFactory.createCustomSize("+" + skillPoints + " Skill Points", 12, Color.CYAN);
      pointsLabel.setAlignment(Align.left);
      skillPointTable.add(pointsLabel);
      textTable.add(skillPointTable).left().padTop(2f).row();
    }

    contentTable.add(textTable).expand().fill();
    dialog.add(contentTable).expand().fill();
    dialog.pack();
    centerDialog();

    // Start hidden and animate in
    dialog.setVisible(false);
    dialog.setColor(1, 1, 1, 0);
    stage.addActor(dialog);
  }

  /** Positions the dialog in the bottom right corner. */
  private void centerDialog() {
    float x = stage.getWidth() - dialog.getWidth() - 20f;
    float y = 20f;
    dialog.setPosition(x, y);
  }

  /** Shows the achievement dialog with animation. */
  public void show() {
    if (isVisible) {
      return; // Already showing
    }

    isVisible = true;
    logger.info("[AchievementDialogComponent] Showing achievement dialog for: {}", name);

    // Slide in from bottom right
    float targetX = stage.getWidth() - dialog.getWidth() - 20f;
    float targetY = 20f;
    float startX = stage.getWidth(); // Start off-screen to the right
    float startY = targetY;

    dialog.setPosition(startX, startY);
    dialog.setVisible(true);

    dialog.addAction(
        Actions.sequence(
            Actions.parallel(
                Actions.moveTo(targetX, targetY, ANIMATION_DURATION),
                Actions.fadeIn(ANIMATION_DURATION)),
            Actions.delay(DISPLAY_DURATION),
            Actions.parallel(
                Actions.moveTo(startX, startY, ANIMATION_DURATION),
                Actions.fadeOut(ANIMATION_DURATION)),
            Actions.run(this::hide)));

    if (onCompletion != null) {
      onCompletion.accept(this);
    }
  }

  /**
   * Sets the callback for when the dialog is closed.
   *
   * @param onCompletion the callback function
   */
  public void setOnCompletion(Consumer<AchievementDialogComponent> onCompletion) {
    this.onCompletion = onCompletion;
  }

  /** Handles window resize by repositioning the dialog to bottom right. */
  public void resize() {
    if (dialog != null && isVisible) {
      centerDialog();
    }
  }

  /** Hides the achievement dialog. */
  public void hide() {
    if (!isVisible) {
      return;
    }
    isVisible = false;
    logger.debug("[AchievementDialogComponent] Hiding achievement dialog for: {}", name);
    if (dialog != null) {
      dialog.remove();
    }
    entity.dispose();
    if (onCompletion != null) {
      onCompletion.accept(this);
    }
  }

  /**
   * Checks if the dialog is currently visible.
   *
   * @return true if visible, false otherwise
   */
  public boolean isVisible() {
    return isVisible;
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Dialog is handled by the stage
  }

  @Override
  public void dispose() {
    hide();
    super.dispose();
  }
}
