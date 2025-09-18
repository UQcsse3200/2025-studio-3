package com.csse3200.game.components.dossier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.configs.NPCConfigs;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.TypographyFactory;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DossierDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(DossierDisplay.class);
  private final GdxGame game;
  private Table rootTable;
  // Where true is robots, false is humans
  private boolean type;
  private String[] entities;
  private DossierManager dossierManager;
  private int currentEntity = 0;

  private Label entityInfoLabel;
  private Label entityNameLabel;
  private Image entitySpriteImage;
  private static final String CHANGE_TYPE = "change_type";

  /** Constructor to display the dossier. */
  public DossierDisplay(
      GdxGame game, NPCConfigs entityData, NPCConfigs defenceData, Texture[] textures) {
    super();
    this.game = game;
    type = true;
    this.dossierManager = new DossierManager(entityData, defenceData, textures);
    // All robot entities
    entities =
        new String[] {"standardRobot", "fastRobot", "tankyRobot", "bungeeRobot", "teleportRobot"};
  }

  @Override
  public void create() {
    super.create();
    updateDossierInfoListener();
    changeTypeListener();
    addActors();
  }

  /** Adds all tables to the stage. */
  private void addActors() {
    Label title = TypographyFactory.createTitle("Dossier");
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
              if (type) {
                dossierManager.changeMode();
                entities =
                    new String[] {
                      "standardRobot", "fastRobot", "tankyRobot", "bungeeRobot", "teleportRobot"
                    };
              } else {
                dossierManager.changeMode();
                entities = new String[] {"slingshot"};
              }
              currentEntity = 0;
              // Rebuild UI for the new type
              stage.clear();
              addActors();
            });
  }

  /** Sets up the buttons to swap between humans and robots. */
  private Table makeSwapBtn() {
    TextButton robotsBtn = ButtonFactory.createLargeButton("Robots");
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

    TextButton humansBtn = ButtonFactory.createLargeButton("Humans");
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

    float buttonWidth = 120f; // Fixed width
    float buttonHeight = 40f;

    table.add(humansBtn).size(buttonWidth, buttonHeight);
    table.add(robotsBtn).size(buttonWidth, buttonHeight);

    table.row();

    return table;
  }

  private void updateDossierInfoListener() {
    entity
        .getEvents()
        .addListener(
            "change_info",
            index -> {
              if (entities.length == 0) {
                entityNameLabel.setText("No entries");
                entityInfoLabel.setText("");
                entitySpriteImage.setDrawable(null); // Or a placeholder drawable
                return;
              }
              String currentEntityName = entities[(int) index];
              entityNameLabel.setText(dossierManager.getName(currentEntityName));
              entityInfoLabel.setText(dossierManager.getInfo(currentEntityName));
              entitySpriteImage.setDrawable(
                  dossierManager.getSprite(currentEntityName).getDrawable());
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
    entitySpriteImage =
        dossierManager.getSprite(entities.length > 0 ? entities[currentEntity] : "");
    entitySpriteImage.setScaling(Scaling.fit);
    Table imageFrame = new Table(skin);
    imageFrame
        .add(entitySpriteImage)
        .width(stageWidth * 0.3f)
        .height(stageHeight * 0.3f)
        .pad(stageHeight * 0.03f);

    // 2nd column for Entity Info
    Table infoTable = new Table(skin);

    String name =
        entities.length > 0 ? dossierManager.getName(entities[currentEntity]) : "No entries";
    entityNameLabel = TypographyFactory.createSubtitle(name);
    entityNameLabel.setAlignment(Align.left);
    infoTable.add(entityNameLabel).left().expandX().padRight(stageWidth * 0.09f).row();

    String info = entities.length > 0 ? dossierManager.getInfo(entities[currentEntity]) : "";
    entityInfoLabel = TypographyFactory.createParagraph(info);
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
    ImageButton closeButton =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));

    // Position in top left with 20f padding
    closeButton.setSize(60f, 60f);
    closeButton.setPosition(20f, stage.getHeight() - 60f - 20f);

    // Add listener for the close button
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Close button clicked");
            backMenu();
          }
        });

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
    ButtonGroup<TextButton> group = new ButtonGroup<>();

    for (int i = 0; i < entities.length; i++) {
      final int index = i; // capture index for listener
      TextButton btn = ButtonFactory.createLargeButton(dossierManager.getName(entities[i]));
      group.add(btn);
      buttonRow.add(btn).size(100f, 35f).pad(5);

      btn.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
              if (btn.isChecked()) {
                logger.info("Selected robot button {}", index);
                entity.getEvents().trigger("change_info", index);
              }
            }
          });
    }
    return buttonRow;
  }

  /** Handles navigation back to the World Map Screen. */
  private void backMenu() {
    game.setScreen(GdxGame.ScreenType.WORLD_MAP);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  /** Disposes of this UI component. */
  @Override
  public void dispose() {
    rootTable.clear();
    stage.dispose();
    super.dispose();
  }
}
