    package com.csse3200.game.screens;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.ScreenAdapter;
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.scenes.scene2d.InputEvent;
    import com.badlogic.gdx.scenes.scene2d.Stage;
    import com.badlogic.gdx.scenes.scene2d.ui.Button;
    import com.badlogic.gdx.scenes.scene2d.ui.Image;
    import com.badlogic.gdx.scenes.scene2d.ui.Label;
    import com.badlogic.gdx.scenes.scene2d.ui.Skin;
    import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
    import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
    import com.csse3200.game.GdxGame;
    import com.csse3200.game.components.gamearea.PerformanceDisplay;
    import com.csse3200.game.components.maingame.MainGameActions;
    import com.csse3200.game.components.maingame.MainGameExitDisplay;
    import com.csse3200.game.entities.Entity;
    import com.csse3200.game.entities.EntityService;
    import com.csse3200.game.entities.factories.RenderFactory;
    import com.csse3200.game.input.InputComponent;
    import com.csse3200.game.input.InputDecorator;
    import com.csse3200.game.input.InputService;
    import com.csse3200.game.progression.wallet.Wallet;
    import com.csse3200.game.rendering.RenderService;
    import com.csse3200.game.rendering.Renderer;
    import com.csse3200.game.services.GameTime;
    import com.csse3200.game.services.ResourceService;
    import com.csse3200.game.services.ServiceLocator;
    import com.csse3200.game.progression.skilltree.Skill;
    import com.csse3200.game.progression.skilltree.SkillSet;
    import com.csse3200.game.ui.terminal.Terminal;
    import com.csse3200.game.ui.terminal.TerminalDisplay;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    /**
     * The screen that displays the Skill Tree and handles skill unlocking mechanics.
     * Provides UI for skill buttons, shows skill points, and handles user interaction.
     * It integrates with game services, rendering, input, and entity systems.
     */
    public class SkillTreeScreen extends ScreenAdapter {

        private static final Logger logger = LoggerFactory.getLogger(SkillTreeScreen.class);
        private final GdxGame game;
        private final Renderer renderer;
        private final Texture background;
        private final SpriteBatch batch;
        private final SkillSet skillSet = new SkillSet();
        private Label skillPointLabel;
        private Wallet wallet;

        /**
         * Constructs a SkillTreeScreen, initializing all necessary services and rendering components.
         * @param game the main game instance
         */
        public SkillTreeScreen(GdxGame game) {
            this.game = game;
            this.wallet = new Wallet();
            logger.debug("Initialising skill tree services");

            // Register required services
            ServiceLocator.registerInputService(new InputService());
            ServiceLocator.registerResourceService(new ResourceService());
            ServiceLocator.registerEntityService(new EntityService());
            ServiceLocator.registerRenderService(new RenderService());
            ServiceLocator.registerTimeSource(new GameTime());

            // Initialize renderer and camera
            renderer = RenderFactory.createRenderer();
            renderer.getCamera().getEntity().setPosition(5f, 5f);

            // Load assets and setup UI
            loadAssets();
            createUI();

            // Create batch and background texture
            batch = new SpriteBatch();
            background = new Texture(Gdx.files.internal("images/skilltree_art.png"));
        }

        /** Loads necessary game assets */
        private void loadAssets() {
            logger.debug("Loading assets");
            ResourceService resourceService = ServiceLocator.getResourceService();
            ServiceLocator.getResourceService().loadAll();
        }

        @Override
        public void render(float delta) {
            // Draw background
            batch.begin();
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();

            // Update entities and render scene
            ServiceLocator.getEntityService().update();
            renderer.render();
        }

        @Override
        public void resize(int width, int height) {
            renderer.resize(width, height);
        }

        /**
         * Sets up the UI elements: background, skill points display, input handling, and skill buttons.
         */
        private void createUI() {
            logger.debug("Creating UI");
            Stage stage = ServiceLocator.getRenderService().getStage();

            // Set background image
            Texture backgroundTexture = new Texture(Gdx.files.internal("images/skilltree_art.png"));
            Image backgroundImage = new Image(backgroundTexture);
            backgroundImage.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
            stage.addActor(backgroundImage);

            // Input handling for terminal and UI interactions
            InputComponent inputComponent = ServiceLocator.getInputService()
                    .getInputFactory().createForTerminal();

            // Create UI entity with various components
            Entity ui = new Entity();
            ui.addComponent(new InputDecorator(stage, 10))
                    .addComponent(new PerformanceDisplay())
                    .addComponent(new MainGameActions(this.game))
                    .addComponent(new MainGameExitDisplay())
                    .addComponent(new Terminal())
                    .addComponent(inputComponent)
                    .addComponent(new TerminalDisplay());

            ServiceLocator.getEntityService().register(ui);

            // Display total skill points
            totalSkillPoints(stage);
            stage.addActor(skillPointLabel);

            // Add all skill buttons
            createAllButtons();
        }

        /**
         * Creates a skill button with click handling for unlocking skills.
         *
         * @param stage the stage to add the button to
         * @param skillName the name of the skill
         * @param labelText the label text shown under the button
         * @param lockedTexture texture when skill is locked
         * @param unlockedTexture texture when skill is unlocked
         * @param x x-coordinate on stage
         * @param y y-coordinate on stage
         */
        private void createSkillButton(Stage stage, String skillName, String labelText,
                                       Texture lockedTexture, Texture unlockedTexture,
                                       float x, float y) {

            Button skillButton = new Button(new TextureRegionDrawable(new TextureRegion(lockedTexture)));
            skillButton.setSize(90, 130);
            skillButton.setPosition(x, y);

            skillButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float px, float py) {
                    System.out.println(skillName + " unlocked!");

                    Skill skill = skillSet.getSkill(skillName);
                    int cost = skill.getCost();
                    int points = wallet.getSkillsPoints();
                    // Check if player has enough skill points
                    if (points >= cost) { // REMOVE when Wallet is integrated
                        skillSet.addSkill(skill);
                        skill.unlock();
                        wallet.unlockSkill(cost);
                        skillPointLabel.setText("Skill Points: " + points);
                        // Replace button with unlocked image
                        Image unlockedImage = new Image(unlockedTexture);
                        unlockedImage.setSize(skillButton.getWidth(), skillButton.getHeight());
                        unlockedImage.setPosition(skillButton.getX(), skillButton.getY());
                        stage.getActors().removeValue(skillButton, true);
                        stage.addActor(unlockedImage);
                    }
                }
            });

            stage.addActor(skillButton);
            stage.addActor(createLabel(stage, labelText, skillButton));
            addSkillImage(stage);
        }

        /** Creates all skill buttons for the skill tree */
        private void createAllButtons() {
            Stage stage = ServiceLocator.getRenderService().getStage();
            float width = stage.getViewport().getWorldWidth();
            float height = stage.getViewport().getWorldHeight();

            // Load textures for skills
            Texture lockedTexture = new Texture(Gdx.files.internal("images/attackSkillLocked.png"));
            Texture unlockedTexture = new Texture(Gdx.files.internal("images/attackSkill.png"));

            Texture lockedTextureShield = new Texture(Gdx.files.internal("images/shieldSkillLocked.png"));
            Texture unlockedTextureShield = new Texture(Gdx.files.internal("images/shieldSkill.png"));

            // Damage Skills
            createSkillButton(stage, "Increase AD Basic", "Damage 10%", lockedTexture, unlockedTexture, 0.32f*width, 0.52f*height);
            createSkillButton(stage, "Increase AD Intermediate", "Damage 20%", lockedTexture, unlockedTexture, 0.19f*width, 0.38f*height);
            createSkillButton(stage, "Increase AD Advanced", "Damage 30%", lockedTexture, unlockedTexture, 0.07f*width, 0.16f * height);

            // Firing Skills
            createSkillButton(stage, "Increase firing Basic", "Firing Speed 10%", lockedTexture, unlockedTexture, 0.29f*width, 0.35f*height);
            createSkillButton(stage, "Increase firing Intermediate", "Speed 20%", lockedTexture, unlockedTexture, 0.305f*width, 0.18f*height);
            createSkillButton(stage, "Increase firing Advanced", "Firing Speed 30%", lockedTexture, unlockedTexture, 0.26f*width , 0.15f*height);

            // Crit Skills
            createSkillButton(stage, "Increase crit Basic", "Crit 10%", lockedTexture, unlockedTexture, 0.45f*width, 0.205f*height);
            createSkillButton(stage, "Increase crit Basic", "Crit 20%", lockedTexture, unlockedTexture, 0.835f*width, 0.20f*height);

            // Health Skills
            createSkillButton(stage, "Increase Health Basic", "Health 10%", lockedTextureShield, unlockedTextureShield, 0.56f*width, 0.575f*height);
            createSkillButton(stage, "Increase Health Intermediate", "Health 20%", lockedTextureShield, unlockedTextureShield, 0.475f*width, 0.46f*height);
            createSkillButton(stage, "Increase Health Advanced", "Health 30%", lockedTextureShield, unlockedTextureShield, 0.455f*width, 0.31f*height);

            // Armour Skills
            createSkillButton(stage, "Increase armour Basic", "Armour 10%", lockedTextureShield, unlockedTextureShield, 0.62f*width, 0.47f*height);
            createSkillButton(stage, "Increase armour Intermediate", "Armour 20%", lockedTextureShield, unlockedTextureShield, 0.565f*width, 0.25f*height);
            createSkillButton(stage, "Increase armour Advanced", "Armour 30%", lockedTextureShield, unlockedTextureShield, 0.665f*width, 0.25f*height);
        }

        /**
         * Creates a label centered below a button.
         *
         * @param stage the stage to add the label to
         * @param label the text to display
         * @param button the button to position the label beneath
         * @return a new Label instance
         */
        private Label createLabel(Stage stage, String label, Button button) {
            Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
            Label attackLabel = new Label(label, skin);
            attackLabel.setColor(Color.WHITE);
            attackLabel.setPosition(button.getX() + button.getWidth() / 2 - attackLabel.getWidth() / 2,
                    button.getY() - 20);
            return attackLabel;
        }

        /** Adds the skill point image to the stage */
        private void addSkillImage(Stage stage) {
            Texture texture = new Texture(Gdx.files.internal("images/skillpoint.png"));
            Image image = new Image(texture);
            image.setSize(70, 105);
            image.setPosition(100, 1000);
            stage.addActor(image);
        }

        /**
         * Creates the label showing total skill points.
         * <
         * Replace temporary points variable with Wallet integration as needed.
         * @param stage the stage to add the label to
         */
        private void totalSkillPoints(Stage stage) {
            Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
            String skillPointsNumber = String.format("Skill Points: %d", wallet.getSkillsPoints()); // REMOVE
            skillPointLabel = new Label(skillPointsNumber, skin);
            skillPointLabel.setColor(Color.WHITE);
            skillPointLabel.setPosition(80, 990);
        }

        @Override
        public void dispose() {
            // Dispose renderer, services, and clear ServiceLocator
            renderer.dispose();
            ServiceLocator.getRenderService().dispose();
            ServiceLocator.getEntityService().dispose();
            ServiceLocator.clear();
        }
    }