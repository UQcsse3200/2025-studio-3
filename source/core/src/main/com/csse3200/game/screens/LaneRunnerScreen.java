package com.csse3200.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.csse3200.game.GdxGame;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaneRunnerScreen extends ScreenAdapter {

        private final GdxGame game;
        private final Renderer renderer;


        public LaneRunnerScreen(GdxGame game) {
            this.game = game;
            ServiceLocator.registerRenderService(new RenderService());

            renderer = RenderFactory.createRenderer();
        }

}
