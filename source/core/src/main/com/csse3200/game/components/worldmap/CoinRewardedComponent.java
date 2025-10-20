package com.csse3200.game.components.worldmap;

import com.csse3200.game.components.Component;

public class CoinRewardedComponent extends Component {
  private final int coinAmount;

  public CoinRewardedComponent(int coinAmount) {
    this.coinAmount = coinAmount;
  }

  public int getCoinAmount() {
    return coinAmount;
  }
}
