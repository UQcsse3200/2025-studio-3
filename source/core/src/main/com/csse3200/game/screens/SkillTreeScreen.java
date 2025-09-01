    package com.csse3200.game.screens;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.ScreenAdapter;
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.GL20;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.scenes.scene2d.Actor;
    import com.badlogic.gdx.scenes.scene2d.InputEvent;
    import com.badlogic.gdx.scenes.scene2d.Stage;
    import com.badlogic.gdx.scenes.scene2d.ui.*;
    import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
    import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
    import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
    import com.csse3200.game.GdxGame;
    import com.csse3200.game.components.gamearea.PerformanceDisplay;
    import com.csse3200.game.components.maingame.MainGameActions;
    import com.csse3200.game.components.maingame.MainGameExitDisplay;
    import com.csse3200.game.components.settingsmenu.SettingsMenuDisplay;
    import com.csse3200.game.entities.Entity;
    import com.csse3200.game.entities.EntityService;
    import com.csse3200.game.entities.factories.RenderFactory;
    import com.csse3200.game.input.InputComponent;
    import com.csse3200.game.input.InputDecorator;
    import com.csse3200.game.input.InputService;
    import com.csse3200.game.persistence.Persistence;
    import com.csse3200.game.progression.wallet.Wallet;
    import com.csse3200.game.rendering.RenderService;
    import com.csse3200.game.rendering.Renderer;
    import com.csse3200.game.services.GameTime;
    import com.csse3200.game.services.ResourceService;
    import com.csse3200.game.services.ServiceLocator;
    import com.csse3200.game.skilltree.Skill;
    import com.csse3200.game.skilltree.SkillSet;
    import com.csse3200.game.ui.terminal.Terminal;
    import com.csse3200.game.ui.terminal.TerminalDisplay;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    /** The game screen containing the settings. */
    public class SkillTreeScreen extends ScreenAdapter {
        private static final Logger logger = LoggerFactory.getLogger(SkillTreeScreen.class);
        private final GdxGame game;
        private final Renderer renderer;
        private final Texture background;
        private final SpriteBatch batch;
        private final SkillSet skillSet = new SkillSet();
        //private int points = 100;                                    //REMOVE
        private Label skillPointLabel;


        public SkillTreeScreen(GdxGame game) {
            this.game = game;

            logger.debug("Initialising skill tree services");
            ServiceLocator.registerInputService(new InputService());
            ServiceLocator.registerResourceService(new ResourceService());
            ServiceLocator.registerEntityService(new EntityService());
            ServiceLocator.registerRenderService(new RenderService());
            ServiceLocator.registerTimeSource(new GameTime());

            renderer = RenderFactory.createRenderer();
            renderer.getCamera().getEntity().setPosition(5f, 5f);

            loadAssets();
            createUI();

            batch = new SpriteBatch();
            background = new Texture(Gdx.files.internal("images/skilltree_art.png"));
        }

        private void loadAssets() {
            logger.debug("Loading assets");
            ResourceService resourceService = ServiceLocator.getResourceService();
            ServiceLocator.getResourceService().loadAll();
        }

        @Override
        public void render(float delta) {

            batch.begin();
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();

            ServiceLocator.getEntityService().update();
            renderer.render();
        }

        @Override
        public void resize(int width, int height) {
            renderer.resize(width, height);
        }

        /**
         * load background image + exit button
         */
        private void createUI() {
            logger.debug("Creating ui");
            Stage stage = ServiceLocator.getRenderService().getStage();

            Texture backgroundTexture = new Texture(Gdx.files.internal("images/skilltree_art.png"));
            Image backgroundImage = new Image(backgroundTexture);
            backgroundImage.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());

            stage.addActor(backgroundImage);

            InputComponent inputComponent =
                    ServiceLocator.getInputService().getInputFactory().createForTerminal();

            Entity ui = new Entity();
            ui.addComponent(new InputDecorator(stage, 10))
                    .addComponent(new PerformanceDisplay())
                    .addComponent(new MainGameActions(this.game))
                    //.addComponent(new MainGameExitDisplay())
                    .addComponent(new Terminal())
                    .addComponent(inputComponent)
                    .addComponent(new TerminalDisplay());

            ServiceLocator.getEntityService().register(ui);

            totalSkillPoints(stage);
            stage.addActor(skillPointLabel);

            createAllButtons();
            Skin skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
            TextButton backBtn = new TextButton("Back", skin);

            // Add listener for the back button
            backBtn.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent changeEvent, Actor actor) {
                            logger.debug("Back button clicked");
                            game.setScreen(GdxGame.ScreenType.PROFILE);
                        }
                    });

            // Place button in a table
            Table table = new Table();
            table.setFillParent(true);
            table.top().right().pad(15f);
            table.add(backBtn).size(150, 60);
            stage.addActor(table);
        }

        private void createSkillButton(Stage stage, String skillName, String labelText,
                                       Texture lockedTexture, Texture unlockedTexture,
                                       float x, float y) {

            Skill skill = skillSet.getSkill(skillName);
            boolean locked = skill.getLockStatus();

            Texture texture = lockedTexture;
            if (!locked) {
                texture = unlockedTexture;
            }
            Button skillButton = new Button(new TextureRegionDrawable(new TextureRegion(texture)));
            skillButton.setSize(90, 130);
            skillButton.setPosition(x, y);

            skillButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float px, float py) {
                    System.out.println(skillName + " unlocked!");

                    int cost = skill.getCost();
                    int totalPoints = Persistence.profile().wallet().getSkillsPoints();
                    if (totalPoints >= cost) {
                        //if (points >= cost) {                     // REMOVE
                        skillSet.addSkill(skill);
                        skill.unlock();
                        Persistence.profile().wallet().unlockSkill(cost);
                        totalPoints = Persistence.profile().wallet().getSkillsPoints();
                        //    points -= cost;                          // REMOVE
                        skillPointLabel.setText("Skill Points: " + totalPoints);
                        // }
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
        };

        private void createAllButtons() {
            Stage stage = ServiceLocator.getRenderService().getStage();
            Texture lockedTexture = new Texture(Gdx.files.internal("images/attackSkillLocked.png"));
            Texture unlockedTexture = new Texture(Gdx.files.internal("images/attackSkill.png"));

            Texture lockedTextureShield = new Texture(Gdx.files.internal("images/shieldSkillLocked.png"));
            Texture unlockedTextureShield = new Texture(Gdx.files.internal("images/shieldSkill.png"));

            createSkillButton(stage, "Increase AD Basic", "Damage 10%",
                    lockedTexture, unlockedTexture, 620, 620);
            createSkillButton(stage, "Increase AD Intermediate", "Damage 20%",
                    lockedTexture, unlockedTexture, 370, 455);
            createSkillButton(stage, "Increase AD Advanced", "Damage 30%",
                    lockedTexture, unlockedTexture, 130, 195);

            createSkillButton(stage, "Increase firing Basic", "Firing Speed 10%",
                    lockedTexture, unlockedTexture, 545, 420);
            createSkillButton(stage, "Increase firing Intermediate", "Speed 20%",
                    lockedTexture, unlockedTexture, 590, 210);
            createSkillButton(stage, "Increase firing Advanced", "Firing Speed 30%",
                    lockedTexture, unlockedTexture, 500, 180);

            createSkillButton(stage, "Increase crit Basic", "Crit 10%",
                    lockedTexture, unlockedTexture, 870, 250);

            createSkillButton(stage, "Increase Health Basic", "Health 10%",
                    lockedTextureShield, unlockedTextureShield, 1070, 690);
            createSkillButton(stage, "Increase Health Intermediate", "Health 20%",
                    lockedTextureShield, unlockedTextureShield, 920, 555);
            createSkillButton(stage, "Increase Health Advanced", "Health 30%",
                    lockedTextureShield, unlockedTextureShield, 870, 375);

            createSkillButton(stage, "Increase armour Basic", "Amour 10%",
                    lockedTextureShield, unlockedTextureShield, 1190, 565);
            createSkillButton(stage, "Increase armour Intermediate", "Armour 20%",
                    lockedTextureShield, unlockedTextureShield, 1085, 304);
            createSkillButton(stage, "Increase armour Advanced", "Armour 30%",
                    lockedTextureShield, unlockedTextureShield, 1281, 305);
            createSkillButton(stage, "Increase crit Basic", "crit 20%",
                    lockedTexture, unlockedTexture, 1600, 240);
        }

        private Label createLabel(Stage stage, String label, Button button) {
            Skin skin = new Skin(Gdx.files.internal("uiskin.json")); // skin file
            Label attackLabel = new Label(label, skin);
            attackLabel.setColor(Color.WHITE);

            attackLabel.setPosition(
                    button.getX() + button.getWidth() / 2 - attackLabel.getWidth() / 2,
                    button.getY() - 20
            );
            return attackLabel;
        }

        private void addSkillImage(Stage stage) {

            Texture texture = new Texture(Gdx.files.internal("images/skillpoint.png"));
            Image image = new Image(texture);

            image.setSize(70, 105);
            image.setPosition(100, 1000);
            stage.addActor(image);
        }

        private void totalSkillPoints(Stage stage) {
            Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

            int points = Persistence.profile().wallet().getSkillsPoints();
            String skillPointsNumber = String.format("Skill Points: %d", points);
            skillPointLabel = new Label(skillPointsNumber,skin);
            skillPointLabel.setColor(Color.WHITE);
            skillPointLabel.setPosition( 80, 990);
        }

        @Override
        public void dispose() {
            renderer.dispose();
            ServiceLocator.getRenderService().dispose();
            ServiceLocator.getEntityService().dispose();
            ServiceLocator.clear();
        }
    }
