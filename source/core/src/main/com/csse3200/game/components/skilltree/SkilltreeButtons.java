package com.csse3200.game.components.skilltree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UI component responsible for creating and managing buttons and labels for the skill tree screen.
 * Includes functionality for back navigation, skill unlocking buttons, and displaying total skill
 * points.
 */
public class SkilltreeButtons extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(SkilltreeButtons.class);
  private static final float Z_INDEX = 2f;
  private Table table;
  private final GdxGame game;
  private final SkillSet skillSet;
  private Label skillPointLabel;
  private final SkilltreeDisplay display;

  // Texture path constants
  private static final String BASIC_LOCKED = "images/basic_locked.png";
  private static final String BASIC_UNLOCKED = "images/basic_unlocked.png";
  private static final String INTERMEDIATE_LOCKED = "images/intermediate_locked.png";
  private static final String INTERMEDIATE_UNLOCKED = "images/intermediate_unlocked.png";
  private static final String ADVANCED_LOCKED = "images/advanced_locked.png";
  private static final String ADVANCED_UNLOCKED = "images/advanced_unlocked.png";
  private static final String ATTACK_EXPERT_LOCKED = "images/attack_expert_locked.png";
  private static final String ATTACK_EXPERT_UNLOCKED = "images/attack_expert_unlocked.png";
  private static final String HEALTH_EXPERT_LOCKED = "images/health_expert_locked.png";
  private static final String HEALTH_EXPERT_UNLOCKED = "images/health_expert_unlocked.png";
  private static final String SPEED_EXPERT_LOCKED = "images/speed_expert_locked.png";
  private static final String SPEED_EXPERT_UNLOCKED = "images/speed_expert_unlocked.png";
  private static final String CRIT_EXPERT_LOCKED = "images/speed_expert_locked.png";
  private static final String CRIT_EXPERT_UNLOCKED = "images/speed_expert_unlocked.png";
  private static final String CURRENCY_EXPERT_LOCKED = "images/currency_expert_locked.png";
  private static final String CURRENCY_EXPERT_UNLOCKED = "images/currency_expert_unlocked.png";

  /** Configuration for a skill button. */
  private static class SkillButtonConfig {
    final String name;
    final String statType;
    final String lockedPath;
    final String unlockedPath;
    final float x;
    final float y;

    /**
     * Constructs a SkillButtonConfig.
     *
     * @param name the name of the skill
     * @param statType the type of stat the skill affects
     * @param lockedPath the path to the locked texture
     * @param unlockedPath the path to the unlocked texture
     * @param xPercent the x-coordinate of the skill button
     * @param yPercent the y-coordinate of the skill button
     */
    SkillButtonConfig(
        String name,
        String statType,
        String lockedPath,
        String unlockedPath,
        float xPercent,
        float yPercent) {
      this.name = name;
      this.statType = statType;
      this.lockedPath = lockedPath;
      this.unlockedPath = unlockedPath;
      this.x = xPercent;
      this.y = yPercent;
    }
  }

  /** Configuration for all skill buttons in the skill tree. */
  private static final SkillButtonConfig[] SKILL_BUTTONS = {
    // Basic
    new SkillButtonConfig(
        "Attack Basic", "Attack Damage", BASIC_LOCKED, BASIC_UNLOCKED, 0.09f, 0.10f),
    new SkillButtonConfig(
        "Firing Speed Basic", "Firing Speed", BASIC_LOCKED, BASIC_UNLOCKED, 0.23f, 0.11f),
    new SkillButtonConfig("Health Basic", "Health", BASIC_LOCKED, BASIC_UNLOCKED, 0.42f, 0.09f),
    new SkillButtonConfig(
        "Currency Basic", "Currency Generation", BASIC_LOCKED, BASIC_UNLOCKED, 0.69f, 0.105f),
    new SkillButtonConfig(
        "Crit Basic", "Critical Chance", BASIC_LOCKED, BASIC_UNLOCKED, 0.88f, 0.10f),

    // Intermediate
    new SkillButtonConfig(
        "Attack Intermediate", "", INTERMEDIATE_LOCKED, INTERMEDIATE_UNLOCKED, 0.06f, 0.25f),
    new SkillButtonConfig(
        "Firing Speed Intermediate", "", INTERMEDIATE_LOCKED, INTERMEDIATE_UNLOCKED, 0.24f, 0.27f),
    new SkillButtonConfig(
        "Health Intermediate", "", INTERMEDIATE_LOCKED, INTERMEDIATE_UNLOCKED, 0.46f, 0.26f),
    new SkillButtonConfig(
        "Currency Intermediate", "", INTERMEDIATE_LOCKED, INTERMEDIATE_UNLOCKED, 0.695f, 0.25f),
    new SkillButtonConfig(
        "Crit Intermediate", "", INTERMEDIATE_LOCKED, INTERMEDIATE_UNLOCKED, 0.875f, 0.25f),

    // Advanced
    new SkillButtonConfig("Attack Advanced", "", ADVANCED_LOCKED, ADVANCED_UNLOCKED, 0.05f, 0.42f),
    new SkillButtonConfig(
        "Firing Speed Advanced", "", ADVANCED_LOCKED, ADVANCED_UNLOCKED, 0.21f, 0.45f),
    new SkillButtonConfig("Health Advanced", "", ADVANCED_LOCKED, ADVANCED_UNLOCKED, 0.42f, 0.44f),
    new SkillButtonConfig(
        "Currency Advanced", "", ADVANCED_LOCKED, ADVANCED_UNLOCKED, 0.65f, 0.43f),
    new SkillButtonConfig("Crit Advanced", "", ADVANCED_LOCKED, ADVANCED_UNLOCKED, 0.85f, 0.41f),

    // Expert
    new SkillButtonConfig(
        "Attack Expert", "", ATTACK_EXPERT_LOCKED, ATTACK_EXPERT_UNLOCKED, 0.135f, 0.6295f),
    new SkillButtonConfig(
        "Firing Speed Expert", "", SPEED_EXPERT_LOCKED, SPEED_EXPERT_UNLOCKED, 0.23f, 0.629f),
    new SkillButtonConfig(
        "Crit Expert", "", CRIT_EXPERT_LOCKED, CRIT_EXPERT_UNLOCKED, 0.86f, 0.629f),
    new SkillButtonConfig(
        "Health Expert", "", HEALTH_EXPERT_LOCKED, HEALTH_EXPERT_UNLOCKED, 0.34f, 0.631f),
    new SkillButtonConfig(
        "Currency Expert", "", CURRENCY_EXPERT_LOCKED, CURRENCY_EXPERT_UNLOCKED, 0.60f, 0.631f),
  };

  /**
   * Constructs a SkilltreeButtons component.
   *
   * @param game the main game instance
   * @param display the skill tree display used to render skill connections
   */
  public SkilltreeButtons(GdxGame game, SkilltreeDisplay display) {
    this.skillSet = ServiceLocator.getProfileService().getProfile().getSkillset();
    this.game = game;
    this.display = display;
  }

  /** Initializes the component by adding all necessary UI actors. */
  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Adds all actors including back button, skill buttons, and skill points label. */
  private void addActors() {
    createBackButton();
    totalSkillPoints(stage);
    createSkillButtons();
    stage.addActor(skillPointLabel);
  }

  private void createSkillButtons() {
    float width = stage.getViewport().getWorldWidth();
    float height = stage.getViewport().getWorldHeight();
    for (SkillButtonConfig cfg : SKILL_BUTTONS) {
      Texture locked = new Texture(Gdx.files.internal(cfg.lockedPath));
      Texture unlocked = new Texture(Gdx.files.internal(cfg.unlockedPath));
      createSkillButton(
          cfg.name, cfg.statType, locked, unlocked, cfg.x * width, cfg.y * height, display);
    }
  }

  /** Creates a "Back" button that navigates back to the profile screen. */
  private void createBackButton() {
    // Create close button using close-icon.png
    ImageButton closeButton =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/close-icon.png", Texture.class)));

    // Position in top left with 20f padding
    closeButton.setSize(60f, 60f);
    closeButton.setPosition(
        20f, // 20f padding from left
        stage.getHeight() - 60f - 20f // 20f padding from top
        );

    // Trigger an event when the button is pressed
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            game.setScreen(GdxGame.ScreenType.MAIN_GAME);
          }
        });

    stage.addActor(closeButton);
  }

  /**
   * Creates a skill button with click handling for unlocking skills.
   *
   * @param skillName the name of the skill
   * @param labelText the label text shown under the button
   * @param lockedTexture texture displayed when skill is locked
   * @param unlockedTexture texture displayed when skill is unlocked
   * @param x x-coordinate on stage
   * @param y y-coordinate on stage
   * @param display the skill tree display instance
   */
  private void createSkillButton(
      String skillName,
      String labelText,
      Texture lockedTexture,
      Texture unlockedTexture,
      float x,
      float y,
      SkilltreeDisplay display) {

    boolean locked = !skillSet.checkIfUnlocked(skillName);
    Texture texture = locked ? lockedTexture : unlockedTexture;

    Button skillButton = new Button(new TextureRegionDrawable(new TextureRegion(texture)));
    setButtonSize(skillButton, skillName);
    skillButton.setPosition(x, y);

    skillButton.addListener(
        new SkilltreeActions(
            skillName, skillSet, unlockedTexture, skillButton, stage, skillPointLabel, display));

    stage.addActor(skillButton);
    stage.addActor(createLabel(labelText, skillButton));
    addSkillImage();
  }

  /**
   * Sets the size of a skill button based on its skill type.
   *
   * @param skillButton the button to set size for
   * @param skillName the name of the skill
   */
  private void setButtonSize(Button skillButton, String skillName) {
    float width = stage.getViewport().getWorldWidth();
    float height = stage.getViewport().getWorldHeight();

    if (skillName.contains("Intermediate")) {
      skillButton.setSize(width * 0.07f, height * 0.12f);
    } else if (skillName.contains("Advanced")) {
      skillButton.setSize(width * 0.13f, height * 0.17f);
    } else if (skillName.contains("Expert")) {
      skillButton.setSize(width * 0.08f, height * 0.35f);
      if (skillName.equals("Health Expert")) {
        skillButton.setSize(width * 0.25f, height * 0.18f);
      } else if (skillName.equals("Currency Expert")) {
        skillButton.setSize(width * 0.21f, height * 0.3f);
      }
    } else {
      skillButton.setSize(width * 0.1f, height * 0.1f);
    }
  }

  /**
   * Creates a label for a given button.
   *
   * @param label the text to display
   * @param button the associated button
   * @return a Label instance positioned below the button
   */
  private Label createLabel(String label, Button button) {
    Skin skin2 = new Skin(Gdx.files.internal("uiskin.json"));
    Label attackLabel = new Label(label, skin2);
    attackLabel.setColor(Color.WHITE);
    attackLabel.setPosition(
        button.getX() + button.getWidth() / 2 - attackLabel.getWidth() / 2, button.getY() - 20);
    return attackLabel;
  }

  /** Adds the skill point image icon to the stage. */
  private void addSkillImage() {
    Texture texture = new Texture(Gdx.files.internal("images/skillpoints.png"));
    Image image = new Image(texture);

    float width = stage.getViewport().getWorldWidth();
    float height = stage.getViewport().getWorldHeight();
    image.setSize(0.05f * width, 0.11f * height);
    image.setPosition(0.06f * width, 0.85f * height);
    stage.addActor(image);
  }

  /**
   * Creates and displays a label showing the total skill points.
   *
   * @param stage the stage to add the label to
   */
  private void totalSkillPoints(Stage stage) {
    Skin skin2 = new Skin(Gdx.files.internal("uiskin.json"));
    int points = ServiceLocator.getProfileService().getProfile().getWallet().getSkillsPoints();
    String skillPointsNumber = String.format("Skill Points: %d", points);
    skillPointLabel = new Label(skillPointsNumber, skin2);
    skillPointLabel.setColor(Color.WHITE);

    float width = stage.getViewport().getWorldWidth();
    float height = stage.getViewport().getWorldHeight();
    skillPointLabel.setFontScale(0.0013f * height);
    skillPointLabel.setPosition(0.04f * width, 0.8f * height);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Drawing handled by stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    table.clear();
    super.dispose();
  }
}
