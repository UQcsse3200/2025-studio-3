package com.csse3200.game.components.dossier;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
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

    private Label entityInfoLabel;
    private Label entityNameLabel;
    private Image entitySpriteImage;


    /** Constructor to display the dossier. */
    public DossierDisplay(GdxGame game) {
        super();
        this.game = game;
        type = true;
        this.dossierManager = new DossierManager();
        // All robot entities
        entities = new String[]{"Standard Robot"};
    }

    @Override
    public void create() {
        super.create();
        registerEntityListener();
        addActors();
    }

    /** Adds all tables to the stage. */
    private void addActors() {
        Label title = new Label("Dossier", skin, "title");
        Table backBtn = makeBackBtn();

        rootTable = new Table();
        rootTable.setFillParent(true);

        rootTable.add(title).expandX().top().padTop(20f);

        rootTable.row().padTop(5f);
        rootTable.add(makeSwapBtn()).expandX().expandY();

        rootTable.row().padTop(20f);
        rootTable.add(makeDossierTable()).expandX().expandY();

        stage.addActor(rootTable);
        stage.addActor(backBtn);
    }

    /** Logic that changes the type. */
    private void changeTypeListener(TextButton button, boolean value) {
        button.addListener(new ClickListener() {
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
        table.defaults().pad(10);

        table.add(humansBtn).colspan(2).left();
        table.add(robotsBtn).colspan(2).left();
        table.row();

        return table;
    }


    private void registerEntityListener() {
        // Event listener
        entity.getEvents().addListener("change_info", () -> {
            entityNameLabel.setText("dossierManager.getName(entities[0])");
            entityInfoLabel.setText("dossierManager.getInfo(entities[0])");
        });
    }

    private Table makeDossierTable() {
        // Set up the labels and image to display
        entityNameLabel = new Label("Name: " + dossierManager.getName(entities[0]), skin);
        entityInfoLabel = new Label(dossierManager.getInfo(entities[0]), skin);
        entitySpriteImage = dossierManager.getSprite();
        entityInfoLabel.setWrap(true);

        TextButton button = new TextButton(dossierManager.getName(entities[0]), skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                logger.info("Selected robot: {} from button {}", entityNameLabel, 0);
                entity.getEvents().trigger("change_info");
            }
        });


        // Handle creating the main table
        Table table = new Table();
        table.defaults().pad(10);

        // First Row (name)
        table.add(entityNameLabel).colspan(2).left().padBottom(10);
        table.row();

        // Second Row (image and description)
        entitySpriteImage.setScaling(Scaling.fit);
        table.add(entitySpriteImage).size(250, 250).center().padRight(10);
        table.add(entityInfoLabel).width(700);
        table.row();

        // Third Row (Buttons to switch between entities)
        Table buttonRow = new Table();
        buttonRow.defaults().expandX().fillX().pad(5);

        buttonRow.add(button);

        table.row();
        table.add(buttonRow).colspan(2);

        table.pack();

        table.debug();
        return table;
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
        super.dispose();
    }
}
