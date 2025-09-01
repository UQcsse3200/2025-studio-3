package com.csse3200.game.services;

import com.csse3200.game.components.currency.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private final Currency currency;

    public CurrencyService(int initialAmount, int maxAmount) {

        this.currency = new Currency(initialAmount, maxAmount);
    }

    public Currency getCurrency() {
        return currency;
    }

    public int get() {
        return currency.getSunlight();
    }

    public void set(int amount) {
        currency.setSunlight(amount);
    }

    public void add(int amount) {
        currency.addSunshine(amount);
    }

    public boolean spend(int amount) {
        return currency.spendSunshine(amount);
    }
}
