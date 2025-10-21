package com.csse3200.game.entities.configs;

import java.util.Map;

public class DefenceAndGeneratorConfig {
    public Config config;

    public static class Config {
        public Map<String, BaseDefenderConfig> defenders;
        public Map<String, BaseGeneratorConfig> generators;
    }
}
