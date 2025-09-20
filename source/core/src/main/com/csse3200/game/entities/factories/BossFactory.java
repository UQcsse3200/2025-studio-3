package com.csse3200.game.entities.factories;


import com.csse3200.game.entities.configs.*;
import com.csse3200.game.persistence.FileLoader;


/**
 * Factory to create non-playable character (NPC) entities with predefined components.
 *
 * <p>Each NPC entity type should have a creation method that returns a corresponding entity.
 * Predefined entity properties can be loaded from configs stored as json files which are defined in
 * "NPCConfigs".
 *
 * <p>If needed, this factory can be separated into more specific factories for entities with
 * similar characteristics.
 */

public class BossFactory {
    /**
     * Loads boss config data from JSON. The configs object is populated at class-load time. If the
     * file is missing or deserialization fails, this will be null.
     */
    public enum BossTypes{
        SCRAPTITAN
    }
    private static final BossConfigs configs = FileLoader.readClass(BossConfigs.class,"configs/boss.json");
}
