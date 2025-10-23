package com.csse3200.game.components;

public class CoinRewardedComponent extends Component {
  private final int coinAmount;

  public CoinRewardedComponent(int coinAmount) {
    this.coinAmount = coinAmount;
  }

  public int getCoinAmount() {
    return coinAmount;
  }
}
