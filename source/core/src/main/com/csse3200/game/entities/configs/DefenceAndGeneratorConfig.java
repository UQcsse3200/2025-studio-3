package com.csse3200.game.entities.configs;

import java.util.HashMap;

public class DefenceAndGeneratorConfig {
    public Config config;

    public static class Config {
        public HashMap<String, BaseDefenderConfig> defenders = new HashMap<>();
        public HashMap<String, BaseGeneratorConfig> generators = new HashMap<>();
    }
}
