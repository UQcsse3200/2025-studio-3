package com.csse3200.game.areas;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.areas.terrain.TerrainFactory.TerrainType;
import com.csse3200.game.components.InventoryUnitInputComponent;
import com.csse3200.game.components.currency.CurrencyGeneratorComponent;
import com.csse3200.game.components.tile.TileStatusComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.GridFactory;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.components.gamearea.GameAreaDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

/**
 * Creates a level in the game, creates the map, a tiled grid for the playing area and a
 * player unit inventory allowing the player to add units to the grid.
 */
public class LevelGameArea extends GameArea implements AreaAPI {
    private static final Logger logger = LoggerFactory.getLogger(LevelGameArea.class);
    private static final int LEVEL_ONE_ROWS = 5;
    private static final int LEVEL_ONE_COLS = 10;
    private static final String[] levelTextures = {
            "images/box_boy_leaf.png",
            "images/level-1-map-v1.png",
            "images/ghost_king.png",
            "images/ghost_1.png",
            "images/olive_tile.png",
            "images/green_tile.png",
            "images/box_boy.png",
            "images/robot_placeholder.png",
            "images/selected_star.png"
            "images/selected_star.png",
            "images/normal_sunlight.png"
    };

    private static final String[] levelTextureAtlases = {
            "images/ghost.atlas", "images/ghostKing.atlas", "images/robot_placeholder.atlas"
    };

    private static final String[] levelSounds = {"sounds/Impact4.ogg"};
    private static final String backgroundMusic = "sounds/BGM_03_mp3.mp3";
    private static final String[] levelMusic = {backgroundMusic};

    private final TerrainFactory terrainFactory;
    private CurrencyGeneratorComponent currencyGenerator;

    // Offset values
    private final float xOffset;
    private final float yOffset;
    private final float tileSize ;
    private final float invStartX;
    private final float invY;
    private final float invSelectedY;

    private LevelGameGrid grid;
    private final ArrayList<Entity> robots = new ArrayList<>(); // keep track of robots
    private final Entity[] spawned_units;
    private Entity selected_unit;
    private Entity selection_star;

    /**
     * Initialise this LevelGameArea to use the provided TerrainFactory.
     * @param terrainFactory TerrainFactory used to create the terrain for the GameArea.
     */
    public LevelGameArea(TerrainFactory terrainFactory) {
        super();

        // Calculate scaling
        float stageHeight = ServiceLocator.getRenderService().getStage().getHeight();
        float stageWidth = ServiceLocator.getRenderService().getStage().getWidth();
        float stageToWorldRatio = Renderer.GAME_SCREEN_WIDTH / stageWidth;


        float gridHeight = (stageHeight * stageToWorldRatio) / 8f * LEVEL_ONE_ROWS;
        tileSize = gridHeight / LEVEL_ONE_ROWS;
        xOffset = 2f * tileSize;
        yOffset = tileSize;
        invStartX = xOffset;
        invY = yOffset + (LEVEL_ONE_ROWS + 0.5f) * tileSize;
        invSelectedY = yOffset + (LEVEL_ONE_ROWS + 0.5f) * tileSize;

        this.terrainFactory = terrainFactory;
        selected_unit = null; // None selected at level load
        spawned_units = new Entity[LEVEL_ONE_ROWS * LEVEL_ONE_COLS];
        selection_star = null;
    }

    /**
     * Creates the game area by calling helper methods as required.
     */
    @Override
    public void create() {
        loadAssets();
        displayUI();

        spawnMap();

        spawnGrid(LEVEL_ONE_ROWS, LEVEL_ONE_COLS);

        spawnSun();

        playMusic();
    }

    /**
     * Uses the {@link ResourceService} to load the assets for the level.
     */
    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(levelTextures);
        resourceService.loadTextureAtlases(levelTextureAtlases);
        resourceService.loadSounds(levelSounds);
        resourceService.loadMusic(levelMusic);

        while (!resourceService.loadForMillis(10)) {
            // This could be upgraded to a loading screen
            logger.info("Loading... {}%", resourceService.getProgress());
        }
    }

    /**
     * Spawns the level UI
     */
    private void displayUI() {
        Entity ui = new Entity();
        // add components here for additional UI Elements
        ui.addComponent(new GameAreaDisplay("Level One"));
        spawnEntity(ui);
    }

    /**
     * Creates the map in the {@link TerrainFactory} and spawns it in the correct position.
     */
    private void spawnMap() {
        logger.debug("Spawning level one map");

        // Create the background terrain (single image map)
        terrain = terrainFactory.createTerrain(TerrainType.LEVEL_ONE_MAP);

        // Wrap in an entity
        Entity mapEntity = new Entity().addComponent(terrain);

        // Compute world size
        float tileWidth = terrain.getTileSize();
        float tileHeight = terrain.getTileSize();
        GridPoint2 bounds = terrain.getMapBounds(0);

        float worldWidth = bounds.x * tileWidth;
        float worldHeight = bounds.y * tileHeight;

        mapEntity.setPosition(worldWidth / 2f, worldHeight / 2f);

        spawnEntity(mapEntity);
    }

    /**
     * Spawns the grid of tiles for the game
     *
     * @param rows an int that is the number of rows wanted for the grid
     * @param cols an int that is the number of columns wanted for the grid
     */
    private void spawnGrid(int rows, int cols) {
        grid = new LevelGameGrid(rows, cols);
        for (int i = 0; i < rows * cols; i++) {
            Entity tile;
            // Calc tile position
            float tileX = xOffset + tileSize * (i % cols);
            float tileY = yOffset + tileSize * (float) (i / cols);
            // logic for alternating tile images
            if ((i / cols) % 2 == 1) {
                tile = GridFactory.createTile(i % 2, tileSize, tileX, tileY, this);
            } else {
                tile = GridFactory.createTile(1 - (i % 2), tileSize, tileX, tileY, this);
            }
            tile.setPosition(tileX, tileY);
            tile.getComponent(TileStatusComponent.class).set_position(i);
            grid.addTile(i, tile);
            spawnEntity(tile);
        }
        this.grid = grid;
    }

    /**
     * Check to see if the robot has reached the end
     * Print game over message iff robot has reached the end
     */
    public void checkGameOver() {


        // calculate grid position from the world position
        for (Entity robot : robots) {
            Vector2 worldPos = robot.getPosition();
            int gridX = (int) ((worldPos.x - xOffset) / tileSize);
            int gridY = (int) ((worldPos.y - yOffset) / tileSize);
            logger.info("Robot grid location: {}", gridX);

            //check iff robot has reached the left boundary column (i.e the end)
            if (gridX <= 0) {
                logger.info("Game Over! Robot reached the end at position: {},{}", gridX, gridY);
                System.out.println("Game Over! Robot reached the end at position");
                return;
            }
        }

    }

    /**
     * Unloads the level assets form the {@link ResourceService}
     */
    private void unloadAssets() {
        logger.debug("Unloading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.unloadAssets(levelTextures);
        resourceService.unloadAssets(levelTextureAtlases);
        resourceService.unloadAssets(levelSounds);
        resourceService.unloadAssets(levelMusic);
    }

    /**
    * Creates and Spawns the Units in the inventory
    *
    * @param pos the position of the unit in the inventory, pos >= 1
    * @param image the file path of the unit image
    */
    private void placeInventoryUnit(int pos, String image) {
        Entity unit = new Entity()
            .addComponent(new InventoryUnitInputComponent(this))
            .addComponent(new TextureRenderComponent(image));
        unit.setPosition(invStartX + (pos - 1) * (tileSize * 1.5f), invY);
        unit.scaleHeight(tileSize);
        spawnEntity(unit);
    }

    /**
     * Extends the super method to stop music and unload assets.
     */
    @Override
    public void dispose() {
        super.dispose();
        ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class).stop();
        this.unloadAssets();
    }

    /**
     * Starts the music
     */
    private void playMusic() {
        Music music = ServiceLocator.getResourceService().getAsset(backgroundMusic, Music.class);
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
    }

    /**
     * Getter for grid
     *
     * @return grid
     */
    @Override
    public LevelGameGrid getGrid() {
        return grid;
    }

    /**
     * Getter for selected_unit
     *
     * @return selected_unit
     */
    @Override
    public Entity getSelectedUnit(){
        return selected_unit;
    }

    /**
     * Setter for selected_unit
     *
     * @param unit Entity in the inventory
     */
    @Override
    public void setSelectedUnit(Entity unit) {
        selected_unit = unit;

        // if no star, create one
        if (selection_star == null) {
            selection_star = new Entity();
            selection_star.addComponent(new TextureRenderComponent("images/selected_star.png"));
            selection_star.scaleHeight(tileSize / 2f);
            spawnEntity(selection_star);
        }

        // if no unit selected remove star
        if (selected_unit == null) {
            selection_star.setPosition(-100f, -100f); // offscreen
            return; // break from method
        }

        //set star to correct position and size
        selection_star.setPosition(unit.getCenterPosition().x, invSelectedY);
    }

    /**
     * Adds a unit to the grid
     *
     * @param position the grid tile for spawning
     */
    @Override
    public void spawnUnit(int position){
        Entity unit = new Entity();

        // Match the texture of the inventory unit - placeholder
        Texture texture = selected_unit.getComponent(TextureRenderComponent.class).getTexture();
        unit.addComponent(new TextureRenderComponent(texture));

        // Get and set position coords
        float tileX = xOffset + tileSize * (position % LEVEL_ONE_COLS);
        float tileY = yOffset + tileSize * (float) (position / LEVEL_ONE_COLS);
        unit.setPosition(tileX, tileY);

        // Add to list of all spawned units
        spawned_units[position] = unit;

        // set scale to render as desired
        unit.getComponent(TextureRenderComponent.class).scaleEntity();
        unit.scaleHeight(tileSize);

        spawnEntity(unit);
        logger.info("Unit spawned at position {}", position);
    }

    /**
     * Remove a unit form a tile
     *
     * @param position of the tile
     */
    @Override
    public void removeUnit(int position){
        spawned_units[position].dispose();
        spawned_units[position] = null;

        logger.info("Unit deleted at position {}", position);
    }

    private void spawnSun() {
        Entity sunSpawner = new Entity();

        currencyGenerator = new CurrencyGeneratorComponent(
                5f,
                25,
                "images/normal_sunlight.png"
        );

        sunSpawner.addComponent(currencyGenerator);
        spawnEntity(sunSpawner);
    }

    /**
     * Getter for tile size in world units
     *
     * @return SCALE the size of the tiles
     */
    @Override
    public float getTileSize() {
        return tileSize;
    }
}


