package com.csse3200.game.components.slot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlotMachineDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(SlotMachineDisplay.class);

    private static final float Z_INDEX = 3f;
    private static final float ICON_SIZE = 200f;
    private static final float MARGIN = 20f;
    private static final float FRAME_WIDTH = 720f;
    private static final float FRAME_HEIGHT = 720f;

    private ImageButton slotIconBtn;
    private Group frameGroup;
    private Image frameImage;

    private TextureRegionDrawable frameUpDrawable;
    private TextureRegionDrawable frameDownDrawable;

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        createCornerIcon();
        createPopupFrame();
    }

    private void createCornerIcon() {
        Texture iconTex = ServiceLocator.getResourceService().getAsset("images/slot_icon.png", Texture.class);
        slotIconBtn = new ImageButton(new TextureRegionDrawable(iconTex));
        float x = stage.getWidth() - MARGIN - ICON_SIZE;
        float y = MARGIN;
        slotIconBtn.setSize(ICON_SIZE, ICON_SIZE);
        slotIconBtn.setPosition(x, y);
        slotIconBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean show = !frameGroup.isVisible();
                frameGroup.setVisible(show);
            }
        });
        stage.addActor(slotIconBtn);
    }


    private void createPopupFrame() {
        frameGroup = new Group();
        frameGroup.setVisible(false);

        // 尝试从 slot_frame.atlas 获取第一张 region
        TextureAtlas frameAtlas = ServiceLocator.getResourceService()
                .getAsset("images/slot_frame.atlas", TextureAtlas.class);
        TextureRegion up = frameAtlas.findRegion("slot_frame_up");
        TextureRegion down = frameAtlas.findRegion("slot_frame_down");

        frameUpDrawable = new TextureRegionDrawable(up);
        frameDownDrawable = new TextureRegionDrawable(down);

        frameImage = new Image(frameUpDrawable);
        frameImage.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frameImage.setPosition((stage.getWidth() - FRAME_WIDTH) / 2f,
                (stage.getHeight() - FRAME_HEIGHT) / 2f);

        frameImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                logger.info("Slot frame clicked");
                frameImage.clearActions();
                frameImage.setDrawable(frameDownDrawable);
                frameImage.addAction(Actions.sequence(
                        Actions.delay(1f),
                        Actions.run(() -> frameImage.setDrawable(frameUpDrawable))
                ));
            }
        });

        frameGroup.addActor(frameImage);
        stage.addActor(frameGroup);
    }

    @Override
    public float getZIndex() {
        return Z_INDEX;
    }

    @Override
    public void draw(SpriteBatch batch) {
    }


    @Override
    public void dispose() {
        super.dispose();
        if (slotIconBtn != null) slotIconBtn.remove();
        if (frameGroup != null) frameGroup.remove();
    }
}
