package com.csse3200.game.cutscene;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.cutscene.models.object.Cutscene;
import com.csse3200.game.cutscene.runtime.CutsceneOrchestrator;
import com.csse3200.game.cutscene.runtime.DefaultOrchestrator;
import com.csse3200.game.cutscene.runtime.components.CutsceneHudComponent;
import com.csse3200.game.cutscene.runtime.components.CutsceneInputComponent;
import com.csse3200.game.cutscene.runtime.components.CutsceneTickComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

public class CutsceneUI {

    /**
     * Starts a cutscene
     * @param cutscene The {@link Cutscene} to be played
     */
    public static void play(Cutscene cutscene) {
        CutsceneOrchestrator orchestrator = new DefaultOrchestrator();
        orchestrator.stop();
        orchestrator.load(cutscene);
        Entity cutsceneEntity = new Entity()
                .addComponent(new CutsceneTickComponent(orchestrator))
                .addComponent(new CutsceneHudComponent(orchestrator))
                .addComponent(new CutsceneInputComponent(orchestrator));
        ServiceLocator.getEntityService().register(cutsceneEntity);
    }
}
