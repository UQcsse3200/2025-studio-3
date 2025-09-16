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
import com.csse3200.game.persistence.Persistence;
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

  /**
   * Constructs a SkilltreeButtons component.
   *
   * @param game the main game instance
   * @param display the skill tree display used to render skill connections
   */
  public SkilltreeButtons(GdxGame game, SkilltreeDisplay display) {
    assert Persistence.profile() != null : "Profile must not be null";
    this.skillSet = Persistence.profile().skillset();
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
    createAllButtons(display);
    stage.addActor(skillPointLabel);
  }

  /** Creates a "Back" button that navigates back to the profile screen. */
  private void createBackButton() {
    table = new Table();
    table.top().right();
    table.setFillParent(true);

    TextButton backButton = new TextButton("Back", skin);

    // Trigger an event when the button is pressed
    backButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            game.setScreen(GdxGame.ScreenType.MAIN_GAME);
          }
        });

    table.add(backButton).padTop(10f).padRight(10f);
    stage.addActor(table);
  }

  /**
   * Creates a skill button with click handling for unlocking skills.
   *
   * @param stage the stage to add the button to
   * @param skillName the name of the skill
   * @param labelText the label text shown under the button
   * @param lockedTexture texture displayed when skill is locked
   * @param unlockedTexture texture displayed when skill is unlocked
   * @param x x-coordinate on stage
   * @param y y-coordinate on stage
   * @param display the skill tree display instance
   */
  private void createSkillButton(
      Stage stage,
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
    addSkillImage(stage);
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
   * Creates all skill buttons for the skill tree including Basic, Intermediate, Advanced, and
   * Expert skills.
   *
   * @param display the skill tree display instance
   */
  private void createAllButtons(SkilltreeDisplay display) {
    Stage stage = ServiceLocator.getRenderService().getStage();
    float width = stage.getViewport().getWorldWidth();
    float height = stage.getViewport().getWorldHeight();

    // Load textures
    Texture basicLocked = new Texture(Gdx.files.internal("images/basic_locked.png"));
    Texture basicUnlocked = new Texture(Gdx.files.internal("images/basic_unlocked.png"));

    Texture intLocked = new Texture(Gdx.files.internal("images/intermediate_locked.png"));
    Texture intUnlocked = new Texture(Gdx.files.internal("images/intermediate_unlocked.png"));

    Texture advancedLocked = new Texture(Gdx.files.internal("images/advanced_locked.png"));
    Texture advancedUnlocked = new Texture(Gdx.files.internal("images/advanced_unlocked.png"));

    Texture lockedAttackExpert = new Texture(Gdx.files.internal("images/attack_expert_locked.png"));
    Texture unlockedAttackExpert =
        new Texture(Gdx.files.internal("images/attack_expert_unlocked.png"));

    Texture lockedHealthExpert = new Texture(Gdx.files.internal("images/health_expert_locked.png"));
    Texture unlockedHealthExpert =
        new Texture(Gdx.files.internal("images/health_expert_unlocked.png"));

    Texture lockedSpeedExpert = new Texture(Gdx.files.internal("images/speed_expert_locked.png"));
    Texture unlockedSpeedExpert =
        new Texture(Gdx.files.internal("images/speed_expert_unlocked.png"));

    Texture lockedCritExpert = new Texture(Gdx.files.internal("images/speed_expert_locked.png"));
    Texture unlockedCritExpert =
        new Texture(Gdx.files.internal("images/speed_expert_unlocked.png"));

    Texture lockedCurrencyExpert =
        new Texture(Gdx.files.internal("images/currency_expert_locked.png"));
    Texture unlockedCurrencyExpert =
        new Texture(Gdx.files.internal("images/currency_expert_unlocked.png"));

    // Basic, Intermediate, Advanced, Expert skill buttons
    createSkillButton(
        stage,
        "Attack Basic",
        "Attack Damage",
        basicLocked,
        basicUnlocked,
        0.09f * width,
        0.1f * height,
        display);
    createSkillButton(
        stage,
        "Firing Speed Basic",
        "Firing Speed",
        basicLocked,
        basicUnlocked,
        0.23f * width,
        0.11f * height,
        display);
    createSkillButton(
        stage,
        "Health Basic",
        "Health",
        basicLocked,
        basicUnlocked,
        0.42f * width,
        0.09f * height,
        display);
    createSkillButton(
        stage,
        "Currency Basic",
        "Currency Generation",
        basicLocked,
        basicUnlocked,
        0.69f * width,
        0.105f * height,
        display);
    createSkillButton(
        stage,
        "Crit Basic",
        "Critical Chance",
        basicLocked,
        basicUnlocked,
        0.88f * width,
        0.1f * height,
        display);

    // Intermediate Skills
    createSkillButton(
        stage,
        "Attack Intermediate",
        "",
        intLocked,
        intUnlocked,
        0.06f * width,
        0.25f * height,
        display);
    createSkillButton(
        stage,
        "Firing Speed Intermediate",
        "",
        intLocked,
        intUnlocked,
        0.24f * width,
        0.27f * height,
        display);
    createSkillButton(
        stage,
        "Health Intermediate",
        "",
        intLocked,
        intUnlocked,
        0.46f * width,
        0.26f * height,
        display);
    createSkillButton(
        stage,
        "Currency Intermediate",
        "",
        intLocked,
        intUnlocked,
        0.695f * width,
        0.25f * height,
        display);
    createSkillButton(
        stage,
        "Crit Intermediate",
        "",
        intLocked,
        intUnlocked,
        0.875f * width,
        0.25f * height,
        display);

    // Advanced Skills
    createSkillButton(
        stage,
        "Attack Advanced",
        "",
        advancedLocked,
        advancedUnlocked,
        0.05f * width,
        0.42f * height,
        display);
    createSkillButton(
        stage,
        "Firing Speed Advanced",
        "",
        advancedLocked,
        advancedUnlocked,
        0.21f * width,
        0.45f * height,
        display);
    createSkillButton(
        stage,
        "Health Advanced",
        "",
        advancedLocked,
        advancedUnlocked,
        0.42f * width,
        0.44f * height,
        display);
    createSkillButton(
        stage,
        "Currency Advanced",
        "",
        advancedLocked,
        advancedUnlocked,
        0.65f * width,
        0.43f * height,
        display);
    createSkillButton(
        stage,
        "Crit Advanced",
        "",
        advancedLocked,
        advancedUnlocked,
        0.85f * width,
        0.41f * height,
        display);

    // Expert Skills
    createSkillButton(
        stage,
        "Attack Expert",
        "",
        lockedAttackExpert,
        unlockedAttackExpert,
        0.135f * width,
        0.6295f * height,
        display);
    createSkillButton(
        stage,
        "Firing Speed Expert",
        "",
        lockedSpeedExpert,
        unlockedSpeedExpert,
        0.23f * width,
        0.629f * height,
        display);
    createSkillButton(
        stage,
        "Crit Expert",
        "",
        lockedCritExpert,
        unlockedCritExpert,
        0.86f * width,
        0.629f * height,
        display);
    createSkillButton(
        stage,
        "Health Expert",
        "",
        lockedHealthExpert,
        unlockedHealthExpert,
        0.34f * width,
        0.631f * height,
        display);
    createSkillButton(
        stage,
        "Currency Expert",
        "",
        lockedCurrencyExpert,
        unlockedCurrencyExpert,
        0.6f * width,
        0.631f * height,
        display);
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

  /**
   * Adds the skill point image icon to the stage.
   *
   * @param stage the stage to add the image to
   */
  private void addSkillImage(Stage stage) {
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
    assert Persistence.profile() != null;
    int points = Persistence.profile().wallet().getSkillsPoints();
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
