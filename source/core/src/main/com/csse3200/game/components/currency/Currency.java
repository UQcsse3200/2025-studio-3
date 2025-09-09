package com.csse3200.game.components.currency;

public class Currency {
    private int sunlight;
    private int maxSunlight;

    public Currency() {
        this.sunlight = 0;
        this.maxSunlight = Integer.MAX_VALUE;
    }

    public Currency(int initialAmount) {
        this.sunlight = initialAmount;
        this.maxSunlight = Integer.MAX_VALUE;
    }

    public Currency(int initialAmount, int maxSunshine) {
        this.sunlight = initialAmount;
        this.maxSunlight = maxSunshine;
    }

    public int getSunlight() {
        return sunlight;
    }

    public void setSunlight(int amount) {
        this.sunlight = Math.max(0,amount);
    }

    public void addSunshine(int amount) {
        if (amount > 0) {
            long sum = (long) sunlight + amount;
            if (sum > maxSunlight) {
                sunlight = maxSunlight;
            } else  {
                sunlight += amount;
            }
        }
    }

    public boolean canAffordSunshine(int amount) {
        return sunlight >= amount;
    }

    public boolean spendSunshine(int amount) {
        if (amount > 0 && canAffordSunshine(amount)) {
            sunlight -= amount;
            return true;
        }
        return false;
    }

}
