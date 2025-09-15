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
import com.csse3200.game.ui.UIComponent;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A specialized dialog component for displaying achievement unlock notifications. */
public class AchievementDialogComponent extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(AchievementDialogComponent.class);
  private static final float DISPLAY_DURATION = 4.0f;
  private static final float ANIMATION_DURATION = 0.5f;
  private static final String ACHIEVEMENT_TEXTURE = "images/achievement.png";
  private static final String SKILL_POINT_TEXTURE = "images/skillpoints.png";
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
    // Create window with custom styling and achievement background
    Window.WindowStyle windowStyle = skin.get(Window.WindowStyle.class);

    // Set the achievement background
    try {
      Texture achievementTexture =
          ServiceLocator.getResourceService().getAsset(ACHIEVEMENT_TEXTURE, Texture.class);
      if (achievementTexture != null) {
        Drawable achievementBackground = new TextureRegionDrawable(achievementTexture);
        // Create custom window style with achievement background
        Window.WindowStyle customStyle = new Window.WindowStyle(windowStyle);
        customStyle.background = achievementBackground;
        dialog = new Window("", customStyle);
      } else {
        dialog = new Window("", windowStyle);
      }
    } catch (Exception e) {
      logger.warn(
          "Could not load achievement texture, using default background: {}", e.getMessage());
      dialog = new Window("", windowStyle);
    }

    dialog.setSize(400f, 200f);
    dialog.setMovable(false);
    dialog.setModal(false); // Don't block interaction

    // Create main content table
    Table contentTable = new Table();
    contentTable.pad(20f);

    // Create text content table (no icon needed since background is the achievement box)
    Table textTable = new Table();

    // Achievement unlocked header
    Label.LabelStyle headerStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
    headerStyle.fontColor = Color.GOLD;
    Label headerLabel = new Label("Achievement Unlocked!", headerStyle);
    headerLabel.setFontScale(1.2f);
    headerLabel.setAlignment(Align.center);
    textTable.add(headerLabel).center().row();

    // Achievement name
    Label.LabelStyle nameStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
    nameStyle.fontColor = Color.WHITE;
    Label nameLabel = new Label(name, nameStyle);
    nameLabel.setFontScale(1.1f);
    nameLabel.setAlignment(Align.center);
    textTable.add(nameLabel).center().padTop(5f).row();

    // Achievement description
    Label.LabelStyle descStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
    descStyle.fontColor = Color.LIGHT_GRAY;
    Label descLabel = new Label(description, descStyle);
    descLabel.setWrap(true);
    descLabel.setAlignment(Align.center);
    textTable.add(descLabel).width(200f).center().padTop(5f).row();

    // Tier information
    if (tier != null && !tier.isEmpty()) {
      Label.LabelStyle tierStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
      tierStyle.fontColor = Color.YELLOW;
      Label tierLabel = new Label("Tier: " + tier, tierStyle);
      tierLabel.setAlignment(Align.center);
      textTable.add(tierLabel).center().padTop(5f).row();
    }

    // Skill points earned with icon
    if (skillPoints > 0) {
      Table skillPointTable = new Table();

      // Try to add skill point icon
      try {
        Texture skillPointTexture =
            ServiceLocator.getResourceService().getAsset(SKILL_POINT_TEXTURE, Texture.class);
        if (skillPointTexture != null) {
          Drawable skillPointDrawable = new TextureRegionDrawable(skillPointTexture);
          Image skillPointIcon = new Image(skillPointDrawable);
          skillPointTable.add(skillPointIcon).size(16f, 16f).padRight(5f);
        }
      } catch (Exception e) {
        logger.debug("Could not load skill point texture: {}", e.getMessage());
        // Continue without icon
      }

      Label.LabelStyle pointsStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
      pointsStyle.fontColor = Color.CYAN;
      Label pointsLabel = new Label("+" + skillPoints + " Skill Points", pointsStyle);
      pointsLabel.setAlignment(Align.center);
      skillPointTable.add(pointsLabel);

      textTable.add(skillPointTable).center().padTop(10f).row();
    }

    contentTable.add(textTable).expand().fill();
    dialog.add(contentTable).expand().fill();

    // Apply golden tint effect (since background is already the achievement box)
    applyAchievementStyling();

    dialog.pack();
    centerDialog();

    // Start hidden and animate in
    dialog.setVisible(false);
    dialog.setColor(1, 1, 1, 0);
    stage.addActor(dialog);
  }

  /** Applies special styling for the achievement dialog. */
  private void applyAchievementStyling() {
    // Add a subtle golden glow effect (since achievement.png is already the background)
    dialog.setColor(1f, 1f, 0.9f, 1f); // Subtle warm tint
  }

  /** Positions the dialog in the bottom right corner. */
  private void centerDialog() {
    float x = stage.getWidth() - dialog.getWidth() - 20f; // Bottom right with padding
    float y = 20f; // Bottom of screen with padding
    dialog.setPosition(x, y);
  }

  /** Shows the achievement dialog with animation. */
  public void show() {
    if (isVisible) {
      return; // Already showing
    }

    isVisible = true;
    logger.info("Showing achievement dialog for: {}", name);

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
    logger.debug("Hiding achievement dialog for: {}", name);

    if (dialog != null) {
      dialog.remove();
    }

    // Dispose of the entity
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
    // Dialog is handled by the stage, no custom drawing needed
  }

  @Override
  public void dispose() {
    hide();
    super.dispose();
  }
}
