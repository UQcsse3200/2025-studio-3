package com.csse3200.game.components.dossier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.Map;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** DossierDisplay is a class that displays the dossier of the game. */
public class DossierDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(DossierDisplay.class);
  private final GdxGame game;
  private Table rootTable;
  // Where true is robots, false is humans
  private boolean type;
  private Map<String, BaseEnemyConfig> enemyConfigs;
  private Map<String, BaseDefenderConfig> defenderConfigs;
  private Map<String, BaseGeneratorConfig> generatorConfigs;
  private String[] entities;
  private int currentEntity = 0;
  private boolean enemyMode = true;
  private Arsenal playerArsenal;
  private static final String CHANGE_TYPE = "change_type";
  private static final String CHANGE_INFO = "change_info";

  // Information display constants
  private static final String HEALTH_LABEL = "\n Health: ";
  private static final String ATTACK_LABEL = "\n Attack: ";
  private static final String COST_LABEL = "\nCost: ";
  private static final String SCRAP_LABEL = "\nScrap Value: ";
  private static final String INTERVAL_LABEL = "\nInterval: ";
  private static final String NO_ENTRIES_TEXT = "No entries";

  /**
   * Constructor to display the dossier.
   *
   * @param game the game instance
   */
  public DossierDisplay(GdxGame game) {
    this.game = game;
    this.enemyConfigs = ServiceLocator.getConfigService().getEnemyConfigs();
    this.defenderConfigs = ServiceLocator.getConfigService().getDefenderConfigs();
    this.generatorConfigs = ServiceLocator.getConfigService().getGeneratorConfigs();
    this.playerArsenal = ServiceLocator.getProfileService().getProfile().getArsenal();
    type = true;
    enemyMode = true;

    // Initialize with filtered entities instead of all enemy configs
    loadEnemyEntities();
  }

  @Override
  public void create() {
    super.create();
    changeTypeListener();
    DossierBackAction dossierBack = new DossierBackAction(game);
    entity.getEvents().addListener("back", dossierBack::backMenu);
    addActors();
  }

  /** Adds all tables to the stage. */
  private void addActors() {
    // add background back in between changes
    Texture bgTexture =
        ServiceLocator.getResourceService().getAsset("images/backgrounds/bg.png", Texture.class);
    Image bg = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));
    bg.setFillParent(true);
    bg.setScaling(Scaling.fill);
    stage.addActor(bg);

    Label title = ui.title("Dossier");
    createCloseButton();

    // create rootTable
    rootTable = new Table();
    rootTable.setFillParent(true);
    float uiScale = ui.getUIScale();
    rootTable.padTop(100f * uiScale);
    rootTable.padBottom(100f * uiScale);

    // title with proper scaling
    rootTable.add(title).expandX().top().padTop(20f * uiScale);

    // button row to swap between humans and robots
    rootTable.row().padTop(10f * uiScale);
    rootTable.add(makeSwapBtn()).expandX().expandY();

    // main information of entity
    rootTable.row().padTop(10f * uiScale);
    rootTable.add(navigateDossier()).expand().fill().row();

    rootTable.add(makeEntitiesButtons()).expand().fill().row();

    // add rootTable to stage
    stage.addActor(rootTable);
  }

  /** A listener to change the type of entity shown */
  private void changeTypeListener() {
    entity
        .getEvents()
        .addListener(
            CHANGE_TYPE,
            input -> {
              boolean value = (boolean) input;
              if (value == type) {
                return;
              }

              type = value;
              enemyMode = value;
              logger.info(
                  "[DossierDisplay] Mode changed - type: {}, enemyMode: {}", type, enemyMode);

              loadEntitiesForCurrentMode();
              currentEntity = 0;
              playPageTurnSound();
              rebuildUI();
            });
  }

  /** Loads entities based on the current mode (enemy or human) */
  private void loadEntitiesForCurrentMode() {
    if (type) {
      loadEnemyEntities();
    } else {
      loadHumanEntities();
    }
  }

  /** Loads enemy entities */
  private void loadEnemyEntities() {
    // Exclude gunner robot from the dossier
    java.util.List<String> filtered = new java.util.ArrayList<>();
    for (String key : enemyConfigs.keySet()) {
      if (!"gunnerRobot".equals(key)) {
        filtered.add(key);
      }
    }
    entities = filtered.toArray(new String[0]);
    logger.info("[DossierDisplay] Enemy mode - entities count: {}", entities.length);
    if (logger.isDebugEnabled()) {
      logger.debug("[DossierDisplay] Enemy mode entities: {}", java.util.Arrays.toString(entities));
    }
  }

  /** Loads human entities (defenders and generators, excluding wall and locked entities) */
  private void loadHumanEntities() {
    this.playerArsenal = ServiceLocator.getProfileService().getProfile().getArsenal();
    // Combine defenders and generators, excluding the wall and locked entities
    java.util.List<String> unlockedDefenderKeys = new java.util.ArrayList<>();
    java.util.List<String> unlockedGeneratorKeys = new java.util.ArrayList<>();

    // Filter defenders (excluding wall and locked entities)
    for (String defenderKey : defenderConfigs.keySet()) {
      if (!defenderKey.equals("wall") && playerArsenal.contains(defenderKey)) {
        unlockedDefenderKeys.add(defenderKey);
      }
    }

    // Filter generators (excluding locked entities)
    for (String generatorKey : generatorConfigs.keySet()) {
      if (playerArsenal.contains(generatorKey)) {
        unlockedGeneratorKeys.add(generatorKey);
      }
    }

    String[] defenderKeys = unlockedDefenderKeys.toArray(new String[0]);
    String[] generatorKeys = unlockedGeneratorKeys.toArray(new String[0]);
    entities = new String[defenderKeys.length + generatorKeys.length];
    System.arraycopy(defenderKeys, 0, entities, 0, defenderKeys.length);
    System.arraycopy(generatorKeys, 0, entities, defenderKeys.length, generatorKeys.length);
    logger.info(
        "[DossierDisplay] Human mode - unlocked defender count: {}, unlocked generator count: {}, total unlocked entities: {}",
        defenderKeys.length,
        generatorKeys.length,
        entities.length);
    if (logger.isDebugEnabled()) {
      logger.debug(
          "[DossierDisplay] Human mode unlocked entities: {}", java.util.Arrays.toString(entities));
    }
  }

  /** Plays the page turn sound effect */
  private void playPageTurnSound() {
    Sound pageTurn =
        ServiceLocator.getResourceService().getAsset("sounds/dossier_page_turn.mp3", Sound.class);
    if (pageTurn != null) {
      float volume = ServiceLocator.getSettingsService().getSoundVolume();
      pageTurn.play(volume);
      logger.info("Page turn sound played");
    }
  }

  /** Rebuilds the UI for the new entity type */
  private void rebuildUI() {
    stage.clear();
    addActors();
    // Trigger change_info event to update display with first entity
    if (entities.length > 0) {
      // Ensure currentEntity is within bounds
      if (currentEntity >= entities.length) {
        currentEntity = 0;
      }
      entity.getEvents().trigger(CHANGE_INFO, currentEntity);
    }
  }

  /** Sets up the buttons to swap between humans and robots. */
  private Table makeSwapBtn() {
    int swapButtonWidth = 300;
    TextButton robotsBtn = ui.primaryButton("Robots", swapButtonWidth);
    robotsBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            if (robotsBtn.isChecked()) {
              logger.info("Selected robot type button");
              entity.getEvents().trigger(CHANGE_TYPE, true);
            }
          }
        });

    TextButton humansBtn = ui.primaryButton("Humans", swapButtonWidth);
    humansBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            if (humansBtn.isChecked()) {
              logger.info("Selected human type button");
              entity.getEvents().trigger(CHANGE_TYPE, false);
            }
          }
        });

    Table table = new Table();
    table.defaults().expandX().fillX().space(50f);
    table.padTop(50f * ui.getUIScale());

    // Use UIFactory scaling for consistent button sizing
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(swapButtonWidth);

    table.add(humansBtn).size(buttonDimensions.getKey(), buttonDimensions.getValue());
    table.add(robotsBtn).size(buttonDimensions.getKey(), buttonDimensions.getValue());

    table.row();

    return table;
  }

  /**
   * Creates and returns a navigation UI table for browsing through a list of entities.
   *
   * @return a table containing navigation controls and the dossier display
   */
  private Table navigateDossier() {
    float uiScale = ui.getUIScale();
    float arrowSize = 140f * uiScale;

    Texture leftArrowTexture = new Texture(Gdx.files.internal("images/ui/arrow_left.png"));
    Texture rightArrowTexture = new Texture(Gdx.files.internal("images/ui/arrow_right.png"));

    Drawable leftArrowDrawable = new TextureRegionDrawable(new TextureRegion(leftArrowTexture));
    Drawable rightArrowDrawable = new TextureRegionDrawable(new TextureRegion(rightArrowTexture));

    ImageButton leftArrow = new ImageButton(leftArrowDrawable);
    ImageButton rightArrow = new ImageButton(rightArrowDrawable);

    leftArrow.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            if (entities.length > 0 && currentEntity > 0) {
              currentEntity--;
              entity.getEvents().trigger(CHANGE_INFO, currentEntity);
            }
          }
        });

    rightArrow.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            if (entities.length > 0 && currentEntity < entities.length - 1) {
              currentEntity++;
              entity.getEvents().trigger(CHANGE_INFO, currentEntity);
            }
          }
        });

    Table navigationTable = new Table();
    navigationTable.add(leftArrow).size(arrowSize);
    navigationTable.add(makeDossierTable()).center();
    navigationTable.add(rightArrow).size(arrowSize);

    return navigationTable;
  }

  private void updateDossierInfoListener(Label nameLabel, Label infoLabel, Image spriteImage) {
    entity
        .getEvents()
        .addListener(
            CHANGE_INFO,
            index -> {
              if (entities.length == 0) {
                nameLabel.setText(NO_ENTRIES_TEXT);
                infoLabel.setText("");
                spriteImage.setDrawable(null);
                return;
              }
              // Play page turn sound
              Sound pageTurn =
                  ServiceLocator.getResourceService()
                      .getAsset("sounds/dossier_page_turn.mp3", Sound.class);
              if (pageTurn != null) {
                float volume = ServiceLocator.getSettingsService().getSoundVolume();
                pageTurn.play(volume);
                logger.info("Page turn sound played");
              }
              // Update current entity index
              currentEntity = (int) index;

              // Bounds checking to prevent issues
              if (currentEntity < 0 || currentEntity >= entities.length) {
                logger.warn(
                    "Invalid entity index: {} (entities.length: {})",
                    currentEntity,
                    entities.length);
                nameLabel.setText(NO_ENTRIES_TEXT);
                infoLabel.setText("");
                spriteImage.setDrawable(null);
                return;
              }

              String currentEntityKey = entities[currentEntity];

              nameLabel.setText(getEntityName(currentEntityKey));
              infoLabel.setText(getEntityInfo(currentEntityKey));
              spriteImage.setDrawable(getEntitySprite(currentEntityKey).getDrawable());
            });
  }

  /**
   * Creates the main Dossier table that displays entity information over a book-style background.
   *
   * @return a Table containing the book background and entity information table
   */
  private Table makeDossierTable() {
    float stageWidth = stage.getWidth();
    float stageHeight = stage.getHeight();

    // Load book image as texture based on current type
    String backgroundPath = type ? "images/ui/robot-dossier.png" : "images/ui/human-dossier.png";
    Texture bookTexture = new Texture(Gdx.files.internal(backgroundPath));
    bookTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    // Create background image of dossier
    Image bookImage = new Image(new TextureRegionDrawable(new TextureRegion(bookTexture)));
    bookImage.setScaling(Scaling.fit);
    bookImage.setFillParent(false);

    // Scale book background to 60% of screen width and preserves aspect ratio
    float targetWidth = stageWidth * 0.6f;
    float targetHeight = targetWidth * ((float) bookTexture.getHeight() / bookTexture.getWidth());
    bookImage.setSize(targetWidth, targetHeight);

    // Create content table with adjusted positioning for dossier pages
    Table contentTable = new Table(skin);
    float uiScale = ui.getUIScale();

    // Adjust table positioning to better align with dossier background
    contentTable.defaults().pad(15f * uiScale); // Increased padding
    contentTable.padLeft(targetWidth * 0.08f); // Left margin for left page
    contentTable.padRight(targetWidth * 0.08f); // Right margin for right page
    contentTable.padTop(targetHeight * 0.12f); // Top margin
    contentTable.padBottom(targetHeight * 0.12f); // Bottom margin

    // 1st column for Entity Image (Left Page)
    String currentEntityKey = entities.length > 0 ? entities[currentEntity] : "";
    logger.debug(
        "[DossierDisplay] makeDossierTable - entities.length: {}, currentEntity: {}, currentEntityKey: '{}'",
        entities.length,
        currentEntity,
        currentEntityKey);
    Image entitySpriteImage = getEntitySprite(currentEntityKey);
    entitySpriteImage.setScaling(Scaling.fit);
    Table imageFrame = new Table(skin);
    imageFrame
        .add(entitySpriteImage)
        .width(stageWidth * 0.25f) // Reduced from 0.3f to 0.25f
        .height(stageHeight * 0.25f) // Reduced from 0.3f to 0.25f
        .pad(stageHeight * 0.02f); // Reduced padding

    // 2nd column for Entity Info (Right Page)
    Table infoTable = new Table(skin);

    String name = entities.length > 0 ? getEntityName(currentEntityKey) : NO_ENTRIES_TEXT;
    Label entityNameLabel = ui.subheading(name);
    entityNameLabel.setColor(Color.BLACK);
    entityNameLabel.setAlignment(Align.left);
    infoTable
        .add(entityNameLabel)
        .left()
        .expandX()
        .padRight(stageWidth * 0.05f)
        .row(); // Reduced padding

    String info = entities.length > 0 ? getEntityInfo(currentEntityKey) : "";
    Label entityInfoLabel = ui.text(info);
    entityInfoLabel.setColor(Color.BLACK);
    entityInfoLabel.setWrap(true);
    entityInfoLabel.setAlignment(Align.left);
    infoTable
        .add(entityInfoLabel)
        .fill()
        .left()
        .expandX()
        .padRight(stageWidth * 0.09f)
        .padTop(stageHeight * 0.03f)
        .row();

    // Set up listener with these UI components
    updateDossierInfoListener(entityNameLabel, entityInfoLabel, entitySpriteImage);

    // Add columns to contentTable
    contentTable.add(imageFrame).fillY();
    contentTable.add(infoTable).expand().fill();
    contentTable.row();

    // sizing content table
    Container<Table> contentContainer = new Container<>(contentTable);
    contentContainer.size(targetWidth, targetHeight);
    contentContainer.fill();

    // Stack containing book image and content
    Stack stack = new Stack();
    stack.setSize(targetWidth, targetHeight);
    stack.add(bookImage);
    stack.add(contentContainer);
    stack.setFillParent(false);

    // Wrap in outer table for positioning
    Table outerTable = new Table();
    outerTable.setSize(targetWidth, targetHeight);
    outerTable.add(stack).size(targetWidth, targetHeight);

    return outerTable;
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    TextButton closeButton = ui.createBackExitButton(entity.getEvents(), stage.getHeight(), "Back");

    stage.addActor(closeButton);
  }

  /**
   * Builds a horizontally scrollable table of buttons to access different entities within either
   * 'Human' or 'Robots' sections.
   *
   * @return a horizontally scrollable table with entity buttons
   */
  private Table makeEntitiesButtons() {
    float uiScale = ui.getUIScale();
    float buttonWidth = 280f;
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(buttonWidth);

    // Create button row with all entities
    Table buttonRow = createEntityButtonRow(buttonWidth, buttonDimensions, uiScale);

    // Create scroll pane and arrows
    ScrollPane scrollPane = createScrollPane(buttonRow);
    float arrowWidth = 60f * uiScale;
    float arrowHeight = buttonDimensions.getValue();
    Table arrowRow = createScrollArrows(scrollPane, arrowWidth, arrowHeight, uiScale);

    // Wrap everything in a root table
    return createButtonRootTable(arrowRow, uiScale);
  }

  /**
   * Creates a horizontally scrollable ScrollPane to contain UI content.
   *
   * @param content the Actor to be wrapped inside the ScrollPane
   * @return a configured ScrollPane for horizontal scrolling
   */
  private ScrollPane createScrollPane(Actor content) {
    // define ScrollPane style
    ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
    scrollStyle.background = null; // optional, if you don’t want any background
    scrollStyle.hScroll = null; // removes the horizontal scroll knob background
    scrollStyle.hScrollKnob = null; // removes the horizontal scroll knob
    scrollStyle.vScroll = null;
    scrollStyle.vScrollKnob = null;

    // wrap the row in a scroll pane (horizontal scrolling)
    ScrollPane scrollPane = new ScrollPane(content, scrollStyle);
    scrollPane.setScrollingDisabled(
        false, true); // allow horizontal scroll and disable vertical scroll
    scrollPane.setFadeScrollBars(false);
    scrollPane.setScrollbarsOnTop(true);
    scrollPane.setSmoothScrolling(true);

    return scrollPane;
  }

  /**
   * Creates a table containing left and right arrow buttons to manually control a ScrollPane.
   *
   * @param scrollPane the ScrollPane to be controlled by the arrows
   * @param arrowWidth the width of the arrow button
   * @param arrowHeight the height of the arrow button
   * @param uiScale the UI scale factor used for padding and sizing
   * @return a table containing the scroll arrows and the scrollable content
   */
  private Table createScrollArrows(
      ScrollPane scrollPane, float arrowWidth, float arrowHeight, float uiScale) {
    TextButton leftArrow = new TextButton("<", skin);
    TextButton rightArrow = new TextButton(">", skin);

    // add listener to move the scroll pane left/right
    leftArrow.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            float newScrollX = Math.max(scrollPane.getScrollX() - 200f, 0f);
            scrollPane.setScrollX(newScrollX);
          }
        });

    rightArrow.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            float newScrollX = Math.min(scrollPane.getScrollX() + 200f, scrollPane.getMaxX());
            scrollPane.setScrollX(newScrollX);
          }
        });

    // wrap everything in a table
    Table arrowRow = new Table();
    arrowRow.add(leftArrow).size(arrowWidth, arrowHeight).padRight(20f * uiScale);
    arrowRow.add(scrollPane).height(arrowHeight).expandX().fillX();
    arrowRow.add(rightArrow).size(arrowWidth, arrowHeight).padLeft(20f * uiScale);

    return arrowRow;
  }

  /**
   * Gets the display name for an entity key.
   *
   * @param entityKey the configuration key of the entity
   * @return the display name of the entity
   */
  private String getEntityName(String entityKey) {
    logger.debug("Getting name for entity key: '{}', enemyMode: {}", entityKey, enemyMode);
    if (enemyMode) {
      BaseEnemyConfig config = enemyConfigs.get(entityKey);
      String result = config != null ? config.getName() : "Unknown Enemy";
      logger.debug("Enemy mode - key: '{}' -> name: '{}'", entityKey, result);
      return result;
    } else {
      // Check defenders first, then generators
      BaseDefenderConfig defenderConfig = defenderConfigs.get(entityKey);
      if (defenderConfig != null) {
        logger.debug(
            "Defender mode - key: '{}' -> name: '{}'", entityKey, defenderConfig.getName());
        return defenderConfig.getName();
      }
      BaseGeneratorConfig generatorConfig = generatorConfigs.get(entityKey);
      if (generatorConfig != null) {
        logger.debug(
            "Generator mode - key: '{}' -> name: '{}'", entityKey, generatorConfig.getName());
        return generatorConfig.getName();
      }
      logger.warn("No config found for entity key: '{}' in defender/generator mode", entityKey);
      return "Unknown Entity";
    }
  }

  /**
   * Gets the sprite image for an entity key.
   *
   * @param entityKey the configuration key of the entity
   * @return the sprite image of the entity
   */
  private Image getEntitySprite(String entityKey) {
    logger.debug("Getting sprite for entity key: '{}', enemyMode: {}", entityKey, enemyMode);
    if (enemyMode) {
      BaseEnemyConfig config = enemyConfigs.get(entityKey);
      if (config != null && config.getAssetPath() != null) {
        Texture texture =
            ServiceLocator.getResourceService().getAsset(config.getAssetPath(), Texture.class);
        return new Image(texture);
      }
      // Fallback to placeholder if no asset
      return new Image(
          ServiceLocator.getResourceService()
              .getAsset("images/entities/placeholder.png", Texture.class));
    } else {
      // Check defenders first, then generators
      BaseDefenderConfig defenderConfig = defenderConfigs.get(entityKey);
      if (defenderConfig != null && defenderConfig.getAssetPath() != null) {
        Texture texture =
            ServiceLocator.getResourceService()
                .getAsset(defenderConfig.getAssetPath(), Texture.class);
        return new Image(texture);
      }

      BaseGeneratorConfig generatorConfig = generatorConfigs.get(entityKey);
      if (generatorConfig != null && generatorConfig.getAssetPath() != null) {
        Texture texture =
            ServiceLocator.getResourceService()
                .getAsset(generatorConfig.getAssetPath(), Texture.class);
        return new Image(texture);
      }

      // Fallback to placeholder if no asset
      return new Image(
          ServiceLocator.getResourceService()
              .getAsset("images/entities/placeholder.png", Texture.class));
    }
  }

  /**
   * Gets the information text for an entity key.
   *
   * @param entityKey the configuration key of the entity
   * @return the information text of the entity
   */
  private String getEntityInfo(String entityKey) {
    logger.debug("Getting info for entity key: '{}', enemyMode: {}", entityKey, enemyMode);
    if (enemyMode) {
      BaseEnemyConfig config = enemyConfigs.get(entityKey);
      if (config != null) {
        return " "
            + config.getDescription()
            + HEALTH_LABEL
            + config.getHealth()
            + ATTACK_LABEL
            + config.getAttack();
      }
      return "No information available";
    } else {
      // Check defenders first, then generators
      BaseDefenderConfig defenderConfig = defenderConfigs.get(entityKey);
      if (defenderConfig != null) {
        return " "
            + defenderConfig.getDescription()
            + HEALTH_LABEL
            + defenderConfig.getHealth()
            + ATTACK_LABEL
            + defenderConfig.getDamage();
      }

      BaseGeneratorConfig generatorConfig = generatorConfigs.get(entityKey);
      if (generatorConfig != null) {
        return " "
            + generatorConfig.getDescription()
            + HEALTH_LABEL
            + generatorConfig.getHealth()
            + COST_LABEL
            + generatorConfig.getCost()
            + SCRAP_LABEL
            + generatorConfig.getScrapValue()
            + INTERVAL_LABEL
            + generatorConfig.getInterval()
            + "s";
      }

      return "No information available";
    }
  }

  /**
   * Creates the button row containing all entity buttons.
   *
   * @param buttonWidth the width of each button
   * @param buttonDimensions the scaled dimensions of buttons
   * @param uiScale the UI scaling factor
   * @return a table containing all entity buttons
   */
  private Table createEntityButtonRow(
      float buttonWidth, Pair<Float, Float> buttonDimensions, float uiScale) {
    Table buttonRow = new Table();
    ButtonGroup<TextButton> group = new ButtonGroup<>();
    String[] allEntities = getAllEntitiesForButtons();

    for (int i = 0; i < allEntities.length; i++) {
      final int index = i;
      String entityKey = allEntities[i];
      TextButton btn = createEntityButton(entityKey, index, buttonWidth);

      group.add(btn);
      buttonRow
          .add(btn)
          .size(buttonDimensions.getKey(), buttonDimensions.getValue())
          .pad(5f * uiScale);
    }

    return buttonRow;
  }

  /**
   * Creates a single entity button with appropriate styling and listener.
   *
   * @param entityKey the entity key
   * @param index the button index
   * @param buttonWidth the button width
   * @return the created button
   */
  private TextButton createEntityButton(String entityKey, int index, float buttonWidth) {
    String displayName = getEntityName(entityKey);
    boolean isUnlocked = enemyMode || playerArsenal.contains(entityKey);

    TextButton btn;
    if (isUnlocked) {
      btn = ui.secondaryButton(displayName, buttonWidth);
    } else {
      btn = ui.secondaryButton(displayName + " (Locked)", buttonWidth);
      btn.setColor(0.5f, 0.5f, 0.5f, 0.7f);
      btn.setDisabled(true);
    }

    // Only add listener for unlocked entities
    if (isUnlocked) {
      btn.addListener(createEntityButtonListener(entityKey, index, displayName));
    }

    return btn;
  }

  /**
   * Creates a change listener for entity buttons.
   *
   * @param entityKey the entity key
   * @param index the button index
   * @param displayName the display name
   * @return the change listener
   */
  private ChangeListener createEntityButtonListener(
      String entityKey, int index, String displayName) {
    return new ChangeListener() {
      @Override
      public void changed(ChangeEvent changeEvent, Actor actor) {
        if (((TextButton) actor).isChecked()) {
          logger.info(
              "Selected entity button {} (key: {}, name: {})", index, entityKey, displayName);
          int unlockedIndex = findUnlockedEntityIndex(entityKey);
          if (unlockedIndex >= 0) {
            entity.getEvents().trigger(CHANGE_INFO, unlockedIndex);
          }
        }
      }
    };
  }

  /**
   * Creates the root table for the button section.
   *
   * @param arrowRow the arrow row table
   * @param uiScale the UI scaling factor
   * @return the root table
   */
  private Table createButtonRootTable(Table arrowRow, float uiScale) {
    Table root = new Table();
    root.bottom().padBottom(60f * uiScale);
    root.padLeft(200f * uiScale);
    root.padRight(200f * uiScale);
    root.add(arrowRow).expandX().fillX();
    return root;
  }

  /**
   * Gets all entities (including locked ones) for the button bar display. This is separate from the
   * navigation entities array which only contains unlocked entities.
   *
   * @return array of all entity keys for the current mode
   */
  private String[] getAllEntitiesForButtons() {
    if (type) {
      // For enemies, return all enemy entities excluding gunner robot
      java.util.List<String> filtered = new java.util.ArrayList<>();
      for (String key : enemyConfigs.keySet()) {
        if (!"gunnerRobot".equals(key)) {
          filtered.add(key);
        }
      }
      return filtered.toArray(new String[0]);
    } else {
      // For humans, return all defenders and generators (excluding wall)
      java.util.List<String> allDefenderKeys = new java.util.ArrayList<>();
      java.util.List<String> allGeneratorKeys = new java.util.ArrayList<>();

      // Get all defenders (excluding wall)
      for (String defenderKey : defenderConfigs.keySet()) {
        if (!defenderKey.equals("wall")) {
          allDefenderKeys.add(defenderKey);
        }
      }

      // Get all generators
      allGeneratorKeys.addAll(generatorConfigs.keySet());

      String[] defenderKeys = allDefenderKeys.toArray(new String[0]);
      String[] generatorKeys = allGeneratorKeys.toArray(new String[0]);
      String[] allEntities = new String[defenderKeys.length + generatorKeys.length];
      System.arraycopy(defenderKeys, 0, allEntities, 0, defenderKeys.length);
      System.arraycopy(generatorKeys, 0, allEntities, defenderKeys.length, generatorKeys.length);

      return allEntities;
    }
  }

  /**
   * Finds the index of an entity in the unlocked entities array.
   *
   * @param entityKey the entity key to find
   * @return the index in the unlocked entities array, or -1 if not found
   */
  private int findUnlockedEntityIndex(String entityKey) {
    for (int i = 0; i < entities.length; i++) {
      if (entities[i].equals(entityKey)) {
        return i;
      }
    }
    return -1;
  }

  /** Disposes of this UI component. */
  @Override
  public void dispose() {
    if (rootTable != null) {
      rootTable.clear();
      rootTable.remove();
    }
    super.dispose();
  }
}
