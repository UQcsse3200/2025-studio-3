package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.components.Component;
public class BallComponent extends Component {

    private Image ballImage;
    private float velocityX;
    private float velocityY;

    private int score=0;
    public BallComponent(Image ballImage,float velocityX,float velocityY) {
        this.ballImage = ballImage;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
    public Image getImage() {
        return ballImage;
    }
    public int getScore() {
        return score;
    }
    public void update(float delta) {

        ballImage.setPosition(ballImage.getX()+velocityX*delta,ballImage.getY()+velocityY*delta);

        if(ballImage.getX()<=0){
            ballImage.setX(0);
            velocityX = Math.abs(velocityX);
            score++;
        }else if(ballImage.getX()+ballImage.getWidth()>=Gdx.graphics.getWidth()){
            ballImage.setX(Gdx.graphics.getWidth()-ballImage.getWidth());
            velocityX = -Math.abs(velocityX);
            score++;
        }

        if(ballImage.getY()+ballImage.getHeight()>=Gdx.graphics.getHeight()){
            ballImage.setY(Gdx.graphics.getHeight()-ballImage.getHeight());
            velocityY = -Math.abs(velocityY);
            score++;
        }

    }
    public void reverseY(){
        velocityY = -velocityY;
    }
}

