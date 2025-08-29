package com.csse3200.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.services.ServiceLocator;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

public class NonDraggableCharacter extends UIComponent {

    private Image image;


    private String texturePath = "images/box_boy_leaf.png";
    private float offsetX = 0f;
    private float offsetY = 500f;
    private float scale = 0.15f;

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        image = new Image(ServiceLocator.getResourceService().getAsset(texturePath, Texture.class));
        image.setSize(image.getWidth() * scale, image.getHeight() * scale);
        image.setPosition(offsetX, offsetY);
        stage.addActor(image);


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (image != null) {
            image.remove();
        }
    }

    @Override
    protected void draw(SpriteBatch batch) {
        image.setPosition(offsetX, offsetY);
    }




    public void setTexture(String path) { this.texturePath = path; }
    public void setOffsets(float x, float y) { this.offsetX = x; this.offsetY = y; }
    public void setScale(float scale) {
        this.scale = scale;
        if (image != null) {
            image.setSize(image.getWidth() * scale, image.getHeight() * scale);
        }
    }
}