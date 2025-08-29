package com.csse3200.game.ui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;



public class DragAndDropDemo extends UIComponent {

    private DragAndDrop dragAndDrop;
    private Image image;

    // Customizable fields
    private String texturePath = "images/box_boy_leaf.png";
    private float offsetX = 0f;
    private float offsetY = 500f;
    private float scale = 0.15f;

    @Override
    public void create() {
        // I should probably change the skin tbh instead of using default skin on UIComponent
        super.create();
        addActors();
        setupDragAndDrop();
    }

    private void addActors() {

        image = new Image(ServiceLocator.getResourceService().getAsset(texturePath, Texture.class));
        //float scale = 0.15f;
        image.setSize(image.getWidth() * scale, image.getHeight() * scale);
        image.setPosition(100, 100); // this is somewhere
        stage.addActor(image);
        Gdx.input.setInputProcessor(stage);
    }

    //    private void setupDragAndDrop() {
//        dragAndDrop = new DragAndDrop();
//
//        dragAndDrop.addSource(new DragAndDrop.Source(boxBoy) {
//            @Override
//            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
//                DragAndDrop.Payload payload = new DragAndDrop.Payload();
//                payload.setDragActor(new Image(boxBoy.getDrawable())); // this seems super wrong
//                return payload;
//            }
//
//            @Override
//            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
//                boxBoy.setPosition(x - boxBoy.getWidth() / 2, y - boxBoy.getHeight() / 2);
//            }
//        });
//    }
    private void setupDragAndDrop() {
        dragAndDrop = new DragAndDrop();

        dragAndDrop.addSource(new DragAndDrop.Source(image) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();


                Image dragImg = new Image(image.getDrawable());
                dragImg.setSize(image.getWidth(), image.getHeight());


                dragAndDrop.setDragActorPosition(-dragImg.getWidth() / 2f, -dragImg.getHeight() / 2f);


                payload.setDragActor(dragImg);
                payload.setValidDragActor(dragImg);
                payload.setInvalidDragActor(dragImg);


                payload.setObject("boxBoy"); // come back to this line to store whats being dragged

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer,
                                 DragAndDrop.Payload payload, DragAndDrop.Target target) {
                // Drop the original image where released
                image.setPosition(x - image.getWidth() / 2f, y - image.getHeight() / 2f);
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        image.remove();
    }

    @Override
    protected void draw(SpriteBatch batch) {
//        float offsetX = 0f;
//        float offsetY = 500f;
        image.setPosition(offsetX, offsetY);
    }



    @Override
    public void update() {
        super.update();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (mx >= image.getX() && mx <= image.getX() + image.getWidth()
                    && my >= image.getY() && my <= image.getY() + image.getHeight()) {

                Gdx.app.postRunnable(() -> {
                    image.remove();
                    entity.dispose();
                });
            }
        }
    }


    public void setTexture(String path) { this.texturePath = path; }
    public void setOffsets(float x, float y) { this.offsetX = x; this.offsetY = y; }
    public void setScale(float scale) { this.scale = scale; }
}