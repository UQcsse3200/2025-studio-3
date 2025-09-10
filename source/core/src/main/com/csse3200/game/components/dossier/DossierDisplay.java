package com.csse3200.game.components.dossier;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.GdxGame;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DossierDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(DossierDisplay.class);
    private final GdxGame game;
    private Table rootTable;

    public DossierDisplay(GdxGame game) {
        super();
        this.game = game;
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

        rootTable.row().padTop(30f);
        rootTable.add(makeDossierTable()).expandX().expandY();

        stage.addActor(rootTable);
        stage.addActor(backBtn);
    }

    private Table makeDossierTable() {
        Label nameLabel = new Label("Name: Robot 1", skin);
        Image robotImage = new Image(ServiceLocator.getResourceService().getAsset("images/coins.png", Texture.class));
        Label description = new Label("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdie.", skin);
        description.setWrap(true);
        TextButton button1 = new TextButton("Action 1", skin);
        TextButton button2 = new TextButton("Action 2", skin);
        TextButton button3 = new TextButton("Action 3", skin);
        TextButton button4 = new TextButton("Action 4", skin);
        TextButton button5 = new TextButton("Action 5", skin);



        Table table = new Table();
        table.defaults().pad(10);  // keep your cell formatting

        // First row: image name (centered)
        table.add(nameLabel).colspan(2).left().padBottom(10);
        table.row();

        // Second row: image (left) + description (right)
        robotImage.setScaling(Scaling.fit); // keeps proportions
        table.add(robotImage).size(250, 250).left().padRight(10);
        table.add(description).width(700);
        table.row();

        // --- Third row: buttons with padding from description ---
        Table buttonRow = new Table();
        buttonRow.defaults().expandX().fillX().pad(5);

        buttonRow.add(button1);
        buttonRow.add(button2);
        buttonRow.add(button3);
        buttonRow.add(button4);
        buttonRow.add(button5);

        table.row(); // move to next row in main table
        table.add(buttonRow).colspan(2); // buttons row spans across image+description

        // pack the table to calculate its preferred size
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
        super.dispose();
    }
}
