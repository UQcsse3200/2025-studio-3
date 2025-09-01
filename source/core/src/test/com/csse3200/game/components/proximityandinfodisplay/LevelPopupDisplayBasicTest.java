package com.csse3200.game.components.proximityandinfodisplay;


import com.csse3200.game.components.proximityinfodisplay.LevelPopupDisplay;
import com.csse3200.game.data.MenuSpriteData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(GameExtension.class)
public class LevelPopupDisplayBasicTest {
    
    @Test
    public void defaultPopupDisplayTest() {
        LevelPopupDisplay display = new LevelPopupDisplay();
        String displayTitle = display.setLevelTitle(null);
        String displayDescription = display.setLevelDescription(null);
        
        assertEquals("Title", displayTitle);
        assertEquals(
                "Description\nPress 'E' to enter level.\nPress 'Q' to close level information.",
                displayDescription);
    }

    @Test
    public void spriteDataPopupDisplayTest() {
        MenuSpriteData spriteData = null;
        LevelPopupDisplay display = new LevelPopupDisplay(spriteData);
        String displayTitle = display.setLevelTitle(spriteData);
        String displayDescription = display.setLevelDescription(spriteData);

        assertEquals("Title", displayTitle);
        assertEquals(
                "Description\nPress 'E' to enter level.\nPress 'Q' to close level information.",
                displayDescription);
    }


}
