package com.csse3200.game.components.dossier;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.configs.StandardRobotConfig;
import com.csse3200.game.persistence.FileLoader;


public class DossierManager {

    private EntityConfigs entityData;

    private final TextureAtlas atlas;

    public DossierManager() {
        this.atlas = new TextureAtlas("images/robot_placeholder.atlas");
        this.entityData = FileLoader.readClass(EntityConfigs.class, "configs/entityData.json");
    }

    public String getName(String entityName) {
        return getEntity(entityName).name;
    }

    public Image getSprite() {
        TextureRegion characterMenuSprite = atlas.findRegion("default");
        return new Image(characterMenuSprite);
    }

    public String getInfo(String entityName) {
        BaseEntityConfig config = createConfigFromString(getEntity(entityName).config);
        return getEntity(entityName).description + '\n'
                + " Attack: " + config.getAttack() +
                "\n Health: " + config.getHealth() +
                "\n Movement Spd.: " + config.getMovementSpeed();
    }

    public static BaseEntityConfig createConfigFromString(String configName) {
        if (configName == null) {
            return null;
        }
        return switch (configName) {
            case "StandardRobotConfig" -> new StandardRobotConfig();

            default -> {
                System.err.println("Unknown config name in JSON: " + configName);
                yield null;
            }
        };
    }

    private EntityDataConfig getEntity(String entityName) {
        return switch (entityName) {
            case "standardRobot" -> entityData.standardRobot;
            default -> entityData.standardRobot;
        };
    }

    public void dispose() {
        atlas.dispose();
    }
}