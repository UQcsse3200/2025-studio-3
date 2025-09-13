package com.csse3200.game.components.dossier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DossierDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(DossierDisplay.class);
  private final GdxGame game;
  private Table rootTable;
  // Where true is robots, false is humans
  private boolean type;
  private String[] entities;
  private DossierManager dossierManager;

  private Label entityInfoLabel;
  private Label entityNameLabel;
  private Image entitySpriteImage;
  private Texture bookTexture;
  private Drawable bookBackground;


  /** Constructor to display the dossier. */
  public DossierDisplay(GdxGame game) {
    super();
    this.game = game;
    type = true;
    this.dossierManager = new DossierManager();
    // All robot entities
    entities = new String[] {"Standard Robot"};
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Adds all tables to the stage. */
  private void addActors() {
    Label title = new Label("Dossier", skin, "title");
    Table backBtn = makeBackBtn();

    // create rootTable
    rootTable = new Table();
    rootTable.setFillParent(true);
    rootTable.padTop(100f);
    rootTable.padBottom(100f);
    backBtn.padTop(100f);
    backBtn.padLeft(75f);

    // title
    rootTable.add(title).expandX().top().padTop(20f);

    // button row to swap between humans and robots
    rootTable.row().padTop(10f);
    rootTable.add(makeSwapBtn()).expandX().expandY();

    // main information of entity
    rootTable.row().padTop(10f);
    rootTable.add(makeDossierTable()).expand().fill().row();

    rootTable.add(makeEntitiesButtons()).expand().fill().row();

    // add rootTable and back button to stage
    stage.addActor(rootTable);
    stage.addActor(backBtn);
  }

  /** Logic that changes the type. */
  private void changeTypeListener(TextButton button, boolean value) {
    button.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            type = value;
            System.out.println(type);
          }
        });
  }

  /** Sets up the buttons to swap between humans and robots. */
  private Table makeSwapBtn() {
    TextButton robotsBtn = new TextButton("Robots", skin);
    changeTypeListener(robotsBtn, true);
    TextButton humansBtn = new TextButton("Humans", skin);
    changeTypeListener(humansBtn, false);

    Table table = new Table();
    table.defaults().expandX().fillX().space(50f);
    table.padTop(50f);

    float buttonWidth = stage.getWidth() * 0.2f; // 20% of screen

    table.add(humansBtn).width(buttonWidth);
    table.add(robotsBtn).width(buttonWidth);

    table.row();

    return table;
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
    Texture bookTexture = new Texture(Gdx.files.internal("images/dossierBackground.png"));
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
    entitySpriteImage = dossierManager.getSprite();
    entitySpriteImage.setScaling(Scaling.fit);
    Table imageFrame = new Table(skin);
      imageFrame.add(entitySpriteImage).width(stageWidth * 0.3f).height(stageHeight * 0.3f).pad(stageHeight * 0.03f);

    // 2nd column for Entity Info
    Table infoTable = new Table(skin);

    entityNameLabel = new Label(dossierManager.getName(entities[0]), skin, "large");
    entityNameLabel.setAlignment(Align.left);
    infoTable.add(entityNameLabel).left().expandX().padRight(stageWidth * 0.09f).row();

    entityInfoLabel = new Label(dossierManager.getInfo(entities[0]), skin);
    entityInfoLabel.setWrap(true);
    entityInfoLabel.setAlignment(Align.left);
    infoTable.add(entityInfoLabel).fill().left().expandX().padRight(stageWidth * 0.09f).padTop(stageHeight * 0.03f).row();

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
    outerTable.add(stack).size(targetWidth, targetHeight).center();;

    return outerTable;
  }

  /**
   * Builds a table containing exit button.
   *
   * @return table with exit button
   */
  private Table makeBackBtn() {
    TextButton backBtn = new TextButton("Back", skin);

    // Add listener for the back button
    backBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            backMenu();
          }
        });

    // Place button in a table
    Table table = new Table();
    table.setFillParent(true);
    table.top().left().pad(15f);
    table.add(backBtn);
    return table;
  }

  /**
   * Builds a table containing buttons to access different entities within either 'Human' or 'Robots' sections.
   *
   * @return table with exit button
   */
  private Table makeEntitiesButtons() {
    // Example buttons to swap between types of robots
    Table buttonRow = new Table();
    ButtonGroup<TextButton> group = new ButtonGroup<>();
    for (int i = 1; i <= 5; i++) {
      TextButton btn = new TextButton("Robot" + i, skin, "default");
      group.add(btn);
      buttonRow.add(btn).pad(5);
    }

    return buttonRow;
  }

  /** Handles navigation back to the Profile Screen. */
  private void backMenu() {
    game.setScreen(GdxGame.ScreenType.PROFILE);
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
    if (bookTexture != null) {
      bookTexture.dispose();
    }
    super.dispose();
  }
}
