package com.csse3200.game.components.dossier;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.configs.WaveConfigs;
import com.csse3200.game.persistence.FileLoader;

public class DossierManager {

    private static final EntityConfigs entityData =
            FileLoader.readClass(EntityConfigs.class, "configs/entityData.json");

    public DossierManager() {}

    public String getName(String entityName) {
        return getEntity(entityName).name;
    }

    public String getSprite() {
        return "";
    }

    public String getInfo(String entityName) {
        return getEntity(entityName).description;
    }

    private EntityDataConfig getEntity(String entityName) {
        return switch (entityName) {
            case "StandardRobot" -> entityData.standardRobot;
            default -> entityData.standardRobot;
        };
    }

}
