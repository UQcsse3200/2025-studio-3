package com.csse3200.game.ui.terminal.commands;

import com.csse3200.game.services.ServiceLocator;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfiniteMoney implements Command{

    private static final Logger logger = LoggerFactory.getLogger(InfiniteMoney.class);

    @Override
    public boolean action(ArrayList<String> args) {
        try {
            ServiceLocator.getCurrencyService().add(999);
        }
        catch (NullPointerException e) {
            logger.debug("This service is not available on this screen.");
        }
        return true;
    }

    /**
     * Validates the command arguments.
     *
     * @param args command arguments
     * @return is valid
     */
    boolean isValid(ArrayList<String> args) {
        return args.size() == 1;
    }
}
