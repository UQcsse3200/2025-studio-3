package com.csse3200.game.entities.configs;

import java.util.HashMap;
import java.util.Map;

/** Container for deserialized wave configurations. */
public class WaveConfigs {
  private Map<String, BaseWaveConfig> config;

  public WaveConfigs() {
    this.config = new HashMap<>();
  }

  public void setConfig(Map<String, BaseWaveConfig> config) {
    this.config = new HashMap<>(config);
  }

  public Map<String, BaseWaveConfig> getConfig() {
    return config;
  }

  public BaseWaveConfig getWave1() {
    return config.get("wave1");
  }

  public BaseWaveConfig getWave2() {
    return config.get("wave2");
  }

  public BaseWaveConfig getWave3() {
    return config.get("wave3");
  }

  /** WaveConfigWrapper is a wrapper class for the WaveConfigs class. */
  public static class WaveConfigWrapper {
    private HashMap<String, BaseWaveConfig> config;

    /** Creates a new WaveConfigWrapper. */
    public WaveConfigWrapper() {
      this.config = new HashMap<>();
    }

    /**
     * Sets the config map for the wave configs.
     *
     * @param config the config map for the wave configs
     */
    public void setConfig(Map<String, BaseWaveConfig> config) {
      this.config = new HashMap<>(config);
    }

    /**
     * Gets the config map for the wave configs.
     *
     * @return the config map for the wave configs
     */
    public Map<String, BaseWaveConfig> getConfig() {
      return config;
    }
  }
}
