package com.csse3200.game.components.dossier;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.persistence.FileLoader;


public class DossierManager {

    private final EntityConfigs entityData;
    private final TextureAtlas atlas;

    public DossierManager() {
        this.atlas = new TextureAtlas("images/robot_placeholder.atlas");
        this.entityData = FileLoader.readClass(EntityConfigs.class, "configs/Enemies.json");
    }

    public String getName(String entityName) {
        return getEntity(entityName).name;
    }

    public Image getSprite() {
        TextureRegion characterMenuSprite = atlas.findRegion("default");
        return new Image(characterMenuSprite);
    }

    /**
     * Retrieves info directly from the loaded config object.
     */
    public String getInfo(String entityName) {
        EntityDataConfig config = getEntity(entityName);
        if (config == null) {
            return "Entity not found.";
        }
        return " " + config.description +
                "\n Attack: " + config.attack +
                "\n Health: " + config.health;
    }

    /**
     * Retrieves the specific enemy data object based on its name.
     * Expanded to include all enemy types.
     */
    private EntityDataConfig getEntity(String entityName) {
        return switch (entityName) {
            case "standardRobot" -> entityData.standardRobot;
            case "fastRobot" -> entityData.fastRobot;
            case "tankyRobot" -> entityData.tankyRobot;
            case "bungeeRobot" -> entityData.bungeeRobot;
            default -> entityData.standardRobot;
        };
    }

    public void dispose() {
        atlas.dispose();
    }
}