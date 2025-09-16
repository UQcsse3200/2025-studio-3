package com.csse3200.game.components.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A UI component for displaying a plaque with skill points and coins in the top right corner. */
public class MainMapNavigationMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(MainMapNavigationMenu.class);
  private static final float Z_INDEX = 3f;
  private static final int ICON_SIZE = 32;
  private static final int PLAQUE_WIDTH = 240;
  private static final int PLAQUE_HEIGHT = 60;
  private static final int BUTTON_SIZE = 60;
  private static final int BUTTON_SPACING = 10;
  private Table plaqueTable;
  private Label skillPointsLabel;
  private Label coinsLabel;
  private Image plaqueBackground;
  private ImageButton settingsButton;
  private ImageButton menuButton;
  private Label settingsTooltip;
  private Label menuTooltip;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Adds the actors to the stage */
  private void addActors() {
    // Check if stage is available, if not, defer initialization
    if (stage == null) {
      return;
    }

    // Create the plaque background
    Texture plaqueTexture =
        ServiceLocator.getGlobalResourceService().getAsset("images/plaque.png", Texture.class);
    plaqueBackground = new Image(plaqueTexture);
    plaqueBackground.setSize(PLAQUE_WIDTH, PLAQUE_HEIGHT);

    // Position plaque background in top right corner
    float x = stage.getWidth() - PLAQUE_WIDTH - 20f - (2 * BUTTON_SIZE) - (2 * BUTTON_SPACING);
    float y = stage.getHeight() - PLAQUE_HEIGHT - 20f;
    plaqueBackground.setPosition(x, y);
    stage.addActor(plaqueBackground);

    // Create table for skill points and coins
    plaqueTable = new Table();
    plaqueTable.setSize(PLAQUE_WIDTH - 20f, PLAQUE_HEIGHT - 20f);
    plaqueTable.setPosition(x + 10f, y + 10f);

    // Create skill points icon
    Texture skillPointsTexture =
        ServiceLocator.getGlobalResourceService().getAsset("images/skillpoints.png", Texture.class);
    Image skillPointsIcon = new Image(skillPointsTexture);
    skillPointsIcon.setSize(ICON_SIZE, ICON_SIZE);

    // Create coins icon
    Texture coinsTexture =
        ServiceLocator.getGlobalResourceService().getAsset("images/coins.png", Texture.class);
    Image coinsIcon = new Image(coinsTexture);
    coinsIcon.setSize(ICON_SIZE, ICON_SIZE);

    // Create labels
    skillPointsLabel = new Label("0", skin);
    whiten(skillPointsLabel);
    skillPointsLabel.setFontScale(1f);
    coinsLabel = new Label("0", skin);
    whiten(coinsLabel);
    coinsLabel.setFontScale(1f);

    // Add elements to table
    plaqueTable.center();
    plaqueTable.add(skillPointsIcon).size(ICON_SIZE, ICON_SIZE).padRight(5f);
    plaqueTable.add(skillPointsLabel).padRight(10f);
    plaqueTable.add(coinsIcon).size(ICON_SIZE, ICON_SIZE).padRight(5f);
    plaqueTable.add(coinsLabel);
    stage.addActor(plaqueTable);
    createButtons(x, y);
    updateDisplay();
  }

  /**
   * Creates and positions the settings and menu buttons to the right of the plaque
   *
   * @param plaqueX The x position of the plaque
   * @param plaqueY The y position of the plaque
   */
  private void createButtons(float plaqueX, float plaqueY) {
    // Create settings button
    Texture settingsTexture =
        ServiceLocator.getGlobalResourceService()
            .getAsset("images/settings-icon.png", Texture.class);
    settingsButton = new ImageButton(new TextureRegionDrawable(settingsTexture));
    settingsButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
    settingsButton.setOrigin(BUTTON_SIZE / 2f, BUTTON_SIZE / 2f);

    // Position settings button to the right of the plaque
    float settingsX = plaqueX + PLAQUE_WIDTH + BUTTON_SPACING;
    float settingsY = plaqueY + (PLAQUE_HEIGHT - BUTTON_SIZE) / 2f; // Center vertically with plaque
    settingsButton.setPosition(settingsX, settingsY);
    settingsButton.setVisible(true);
    stage.addActor(settingsButton);

    // Create menu button
    Texture menuTexture =
        ServiceLocator.getGlobalResourceService().getAsset("images/menu-icon.png", Texture.class);
    menuButton = new ImageButton(new TextureRegionDrawable(menuTexture));
    menuButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
    menuButton.setOrigin(BUTTON_SIZE / 2f, BUTTON_SIZE / 2f);

    // Position menu button to the right of the settings button
    float menuX = settingsX + BUTTON_SIZE + BUTTON_SPACING;
    float menuY = settingsY; // Same Y as settings button
    menuButton.setPosition(menuX, menuY);
    stage.addActor(menuButton);

    // Create tooltips
    createTooltips(settingsX, settingsY, menuX, menuY);

    // Add hover effects and click listeners to buttons
    addHoverEffect(settingsButton);
    addHoverEffect(menuButton);
    addMenuButtonClickListener();
    addSettingsButtonClickListener();
  }

  /**
   * Adds hover effect to a button that shows a tooltip when hovered
   *
   * @param button The button to add the hover effect to
   */
  private void addHoverEffect(ImageButton button) {
    button.addListener(
        new InputListener() {
          @Override
          public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            if (button == settingsButton && settingsTooltip != null) {
              settingsTooltip.setVisible(true);
            } else if (button == menuButton && menuTooltip != null) {
              menuTooltip.setVisible(true);
            }
          }

          @Override
          public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            if (button == settingsButton && settingsTooltip != null) {
              settingsTooltip.setVisible(false);
            } else if (button == menuButton && menuTooltip != null) {
              menuTooltip.setVisible(false);
            }
          }
        });
  }

  /** Adds click listener to menu button to trigger dropdown menu */
  private void addMenuButtonClickListener() {
    menuButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            entity.getEvents().trigger("open_dropdown_menu");
          }
        });
  }

  /** Adds click listener to settings button to trigger dropdown menu */
  private void addSettingsButtonClickListener() {
    settingsButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            entity.getEvents().trigger("open_settings");
          }
        });
  }

  /**
   * Sets the label's font color to white
   *
   * @param label The label to set the font color of
   */
  private void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = Color.WHITE;
    label.setStyle(st);
  }

  /**
   * Creates tooltip labels for the buttons
   *
   * @param settingsX The x position of the settings button
   * @param settingsY The y position of the settings button
   * @param menuX The x position of the menu button
   * @param menuY The y position of the menu button
   */
  private void createTooltips(float settingsX, float settingsY, float menuX, float menuY) {
    // Create settings tooltip
    settingsTooltip = new Label("Settings", skin);
    whiten(settingsTooltip);
    settingsTooltip.setFontScale(0.8f);
    settingsTooltip.setPosition(
        settingsX + (BUTTON_SIZE - settingsTooltip.getPrefWidth()) / 2f, settingsY - 20f);
    settingsTooltip.setVisible(false);
    stage.addActor(settingsTooltip);

    // Create menu tooltip
    menuTooltip = new Label("Menu", skin);
    whiten(menuTooltip);
    menuTooltip.setFontScale(0.8f);
    menuTooltip.setPosition(menuX + (BUTTON_SIZE - menuTooltip.getPrefWidth()) / 2f, menuY - 20f);
    menuTooltip.setVisible(false);
    stage.addActor(menuTooltip);
  }

  /** Updates the display with current wallet amounts */
  public void updateDisplay() {
    try {
      // Get values from profile wallet
      int skillPoints =
          ServiceLocator.getProfileService().getProfile().getWallet().getSkillsPoints();
      int coins = ServiceLocator.getProfileService().getProfile().getWallet().getCoins();
      skillPointsLabel.setText((skillPoints > 999) ? "99+" : String.valueOf(skillPoints));
      coinsLabel.setText((coins > 9999) ? "9999+" : String.valueOf(coins));
    } catch (Exception e) {
      logger.warn(
          "[MainMapNavigationMenu] Could not get wallet data from profile: {}", e.getMessage());
      skillPointsLabel.setText("0");
      coinsLabel.setText("0");
    }
  }

  /**
   * Manual method to set skill points amount
   *
   * @param amount the amount of skill points to set
   */
  public void setSkillPoints(int amount) {
    skillPointsLabel.setText(String.valueOf(amount));
  }

  /**
   * Manual method to set coins amount
   *
   * @param amount the amount of coins to set
   */
  public void setCoins(int amount) {
    coinsLabel.setText(String.valueOf(amount));
  }

  @Override
  public void update() {
    super.update();

    // Initialize actors if stage is now available
    if (stage != null && plaqueBackground == null) {
      addActors();
    }

    updateDisplay();
    updatePlaquePosition();
  }

  /** Updates the plaque background position to stay in top right corner */
  private void updatePlaquePosition() {
    if (plaqueBackground != null) {
      float x = stage.getWidth() - PLAQUE_WIDTH - 20f - (2 * BUTTON_SIZE) - (2 * BUTTON_SPACING);
      float y = stage.getHeight() - PLAQUE_HEIGHT - 20f;
      plaqueBackground.setPosition(x, y);

      // Update table position relative to plaque background
      if (plaqueTable != null) {
        plaqueTable.setPosition(x + 10f, y + 10f);
      }

      // Update button positions relative to plaque
      updateButtonPositions(x, y);
    }
  }

  /** Updates the positions of the settings and menu buttons relative to the plaque */
  private void updateButtonPositions(float plaqueX, float plaqueY) {
    if (settingsButton != null) {
      float settingsX = plaqueX + PLAQUE_WIDTH + BUTTON_SPACING;
      float settingsY = plaqueY + (PLAQUE_HEIGHT - BUTTON_SIZE) / 2f;
      settingsButton.setPosition(settingsX, settingsY);

      // Update tooltip position
      if (settingsTooltip != null) {
        settingsTooltip.setPosition(
            settingsX + (BUTTON_SIZE - settingsTooltip.getPrefWidth()) / 2f, settingsY - 20f);
      }
    }

    if (menuButton != null) {
      float menuX = plaqueX + PLAQUE_WIDTH + BUTTON_SIZE + (2 * BUTTON_SPACING);
      float menuY = plaqueY + (PLAQUE_HEIGHT - BUTTON_SIZE) / 2f;
      menuButton.setPosition(menuX, menuY);

      // Update tooltip position
      if (menuTooltip != null) {
        menuTooltip.setPosition(
            menuX + (BUTTON_SIZE - menuTooltip.getPrefWidth()) / 2f, menuY - 20f);
      }
    }
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
    if (plaqueTable != null) {
      plaqueTable.remove();
      plaqueTable = null;
    }
    if (plaqueBackground != null) {
      plaqueBackground.remove();
      plaqueBackground = null;
    }
    if (settingsButton != null) {
      settingsButton.remove();
      settingsButton = null;
    }
    if (menuButton != null) {
      menuButton.remove();
      menuButton = null;
    }
    if (settingsTooltip != null) {
      settingsTooltip.remove();
      settingsTooltip = null;
    }
    if (menuTooltip != null) {
      menuTooltip.remove();
      menuTooltip = null;
    }
    super.dispose();
  }
}
