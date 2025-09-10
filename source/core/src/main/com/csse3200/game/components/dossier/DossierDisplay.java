package com.csse3200.game.components.dossier;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class DossierDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(DossierDisplay.class);
    private final GdxGame game;
    private Table rootTable;
    // Where true is robots, false is humans
    private boolean type;

    private String entityInfo;
    private String entityName;
    private String entityImagePath;

    private Label entityInfoLabel;
    private Label entityNameLabel;
    private Image entityImage;

    public DossierDisplay(GdxGame game) {
        super();
        this.game = game;
        type = true;
        entityInfo = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdie.";
        entityImagePath = "images/coins.png";
        entityName = "Name: Robot 1";
    }

    @Override
    public void create() {
        super.create();
        addActors();
    }

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

    private void changeTypeListener(TextButton button, boolean value) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                type = value;
                System.out.println(type);
            }
        });
    }

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

    private Table makeDossierTable() {
        entityNameLabel = new Label(entityName, skin);
        entityInfoLabel = new Label(entityInfo, skin);
        entityImage = new Image(ServiceLocator.getResourceService().getAsset(entityImagePath, Texture.class));
        entityInfoLabel.setWrap(true);
        TextButton button1 = new TextButton("Robot1", skin);
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                entityInfo = dossierManager.getInfo(new Entity());
                entityInfoLabel.setText("erm changed info");
            }
        });
        TextButton button2 = new TextButton("Robot2", skin);
        TextButton button3 = new TextButton("Robot3", skin);
        TextButton button4 = new TextButton("Robot4", skin);
        TextButton button5 = new TextButton("Robot5", skin);

        Table table = new Table();
        table.defaults().pad(10);

        // First Row (name)
        table.add(entityNameLabel).colspan(2).left().padBottom(10);
        table.row();

        // Second Row (image and description)
        entityImage.setScaling(Scaling.fit); // keeps proportions
        table.add(entityImage).size(250, 250).center().padRight(10);
        table.add(entityInfoLabel).width(700);
        table.row();

        // Third Row (Buttons to switch between entities)
        Table buttonRow = new Table();
        buttonRow.defaults().expandX().fillX().pad(5);

        buttonRow.add(button1);
        buttonRow.add(button2);
        buttonRow.add(button3);
        buttonRow.add(button4);
        buttonRow.add(button5);

        table.row();
        table.add(buttonRow).colspan(2);

        table.pack();

        table.debug();
        return table;
    }

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
