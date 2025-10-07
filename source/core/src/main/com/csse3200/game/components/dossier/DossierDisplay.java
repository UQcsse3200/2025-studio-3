package com.csse3200.game.components.dossier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
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
  private static final String CHANGE_TYPE = "change_type";
  private static final String CHANGE_INFO = "change_info";

  // Information display constants
  private static final String HEALTH_LABEL = "\n Health: ";
  private static final String ATTACK_LABEL = "\n Attack: ";
  private static final String COST_LABEL = "\nCost: ";
  private static final String SCRAP_LABEL = "\nScrap Value: ";
  private static final String INTERVAL_LABEL = "\nInterval: ";

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
    type = true;
    enemyMode = true;
    entities = this.enemyConfigs.keySet().toArray(new String[0]);
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
    rootTable.padTop(100f);
    rootTable.padBottom(100f);

    // title
    rootTable.add(title).expandX().top().padTop(20f);

    // button row to swap between humans and robots
    rootTable.row().padTop(10f);
    rootTable.add(makeSwapBtn()).expandX().expandY();

    // main information of entity
    rootTable.row().padTop(10f);
    rootTable.add(makeDossierTable()).expand().fill().row();

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
              if (type) {
                entities = enemyConfigs.keySet().toArray(new String[0]);
                logger.info(
                    "[DossierDisplay] Enemy mode - entities count: {}, entities: {}",
                    entities.length,
                    java.util.Arrays.toString(entities));
              } else {
                // Combine defenders and generators
                String[] defenderKeys = defenderConfigs.keySet().toArray(new String[0]);
                String[] generatorKeys = generatorConfigs.keySet().toArray(new String[0]);
                entities = new String[defenderKeys.length + generatorKeys.length];
                System.arraycopy(defenderKeys, 0, entities, 0, defenderKeys.length);
                System.arraycopy(
                    generatorKeys, 0, entities, defenderKeys.length, generatorKeys.length);
                logger.info(
                    "[DossierDisplay] Human mode - defender count: {}, generator count: {}, total entities: {}",
                    defenderKeys.length,
                    generatorKeys.length,
                    entities.length);
                logger.debug(
                    "[DossierDisplay] Human mode entities: {}",
                    java.util.Arrays.toString(entities));
              }
              currentEntity = 0;
              // Rebuild UI for the new type
              stage.clear();
              addActors();
              // Trigger change_info event to update display with first entity
              if (entities.length > 0) {
                entity.getEvents().trigger(CHANGE_INFO, currentEntity);
              }
            });
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
    table.padTop(50f);

    float buttonWidth = 200f; // Fixed width
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(buttonWidth);

    table.add(humansBtn).size(buttonDimensions.getKey(), buttonDimensions.getValue());
    table.add(robotsBtn).size(buttonDimensions.getKey(), buttonDimensions.getValue());

    table.row();

    return table;
  }

  private void updateDossierInfoListener(Label nameLabel, Label infoLabel, Image spriteImage) {
    entity
        .getEvents()
        .addListener(
            CHANGE_INFO,
            index -> {
              if (entities.length == 0) {
                nameLabel.setText("No entries");
                infoLabel.setText("");
                spriteImage.setDrawable(null);
                return;
              }
              // Update current entity index
              currentEntity = (int) index;
              String currentEntityKey = entities[currentEntity];
              logger.debug("Updating dossier info for entity key: {}", currentEntityKey);

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

    // Load book image as texture
    Texture bookTexture = new Texture(Gdx.files.internal("images/ui/dossierBackground.png"));
    bookTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    // Create background image of dossier
    Image bookImage = new Image(new TextureRegionDrawable(new TextureRegion(bookTexture)));
    bookImage.setScaling(Scaling.fit);
    bookImage.setFillParent(false);

    // Scale book background to 60% of screen width and preserves aspect ratio
    float targetWidth = stageWidth * 0.6f;
    float targetHeight = targetWidth * ((float) bookTexture.getHeight() / bookTexture.getWidth());
    bookImage.setSize(targetWidth, targetHeight);

    // Create content table
    Table contentTable = new Table(skin);
    contentTable.defaults().pad(10);

    // 1st column for Entity Image
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
        .width(stageWidth * 0.3f)
        .height(stageHeight * 0.3f)
        .pad(stageHeight * 0.03f);

    // 2nd column for Entity Info
    Table infoTable = new Table(skin);

    String name = entities.length > 0 ? getEntityName(currentEntityKey) : "No entries";
    Label entityNameLabel = ui.subheading(name);
    entityNameLabel.setColor(Color.BLACK);
    entityNameLabel.setAlignment(Align.left);
    infoTable.add(entityNameLabel).left().expandX().padRight(stageWidth * 0.09f).row();

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
    contentContainer.fill().center();

    // Stack containing book image and content
    Stack stack = new Stack();
    stack.add(bookImage);
    stack.add(contentContainer);
    stack.setFillParent(false);

    // Wrap in outer table for positioning
    Table outerTable = new Table();
    outerTable.add(stack).size(targetWidth, targetHeight).center();

    return outerTable;
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    TextButton closeButton = ui.createBackExitButton(entity.getEvents(), stage.getHeight(), "Back");

    stage.addActor(closeButton);
  }

  /**
   * Builds a table containing buttons to access different entities within either 'Human' or
   * 'Robots' sections.
   *
   * @return table with exit button
   */
  private Table makeEntitiesButtons() {
    Table buttonRow = new Table();
    buttonRow.bottom().padBottom(60f);
    ButtonGroup<TextButton> group = new ButtonGroup<>();
    float buttonWidth = 280f;
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(buttonWidth);
    for (int i = 0; i < entities.length; i++) {
      final int index = i; // capture index for listener
      String entityKey = entities[i];
      // Use the display name for button text
      String displayName = getEntityName(entityKey);
      TextButton btn = ui.secondaryButton(displayName, buttonWidth);
      group.add(btn);
      buttonRow.add(btn).size(buttonDimensions.getKey(), buttonDimensions.getValue()).pad(5);

      btn.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
              if (btn.isChecked()) {
                logger.info(
                    "Selected entity button {} (key: {}, name: {})", index, entityKey, displayName);
                entity.getEvents().trigger(CHANGE_INFO, index);
              }
            }
          });
    }
    return buttonRow;
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
            + defenderConfig.getAttack();
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
