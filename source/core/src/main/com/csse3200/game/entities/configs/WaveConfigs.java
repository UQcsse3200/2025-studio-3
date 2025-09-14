package com.csse3200.game.entities.configs;

/** Container for deserialized wave configurations. */
public class WaveConfigs {
  private BaseWaveConfig wave1 = new BaseWaveConfig();
  private BaseWaveConfig wave2 = new BaseWaveConfig();
  private BaseWaveConfig wave3 = new BaseWaveConfig();

  public BaseWaveConfig getWave1() {
    return wave1;
  }

  public BaseWaveConfig getWave2() {
    return wave2;
  }

  public BaseWaveConfig getWave3() {
    return wave3;
  }
}
