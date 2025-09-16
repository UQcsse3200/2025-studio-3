package com.csse3200.game.minigame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.components.Component;

public class PaddleComponent extends Component {
    private Image paddleImage;
    private float speed=500f;

   /* public PaddleComponent(Image paddleImage){
        this.paddleImage=paddleImage;
    }
    @Override
    public void update(){

    }
    public void moveLeft(float delta){
        float newX=paddleImage.getX()-speed*delta;
        newX=Math.max(0,newX);
        paddleImage.setPosition(newX,paddleImage.getY());
    }
    public void moveRight(float delta){
        float newX=paddleImage.getX()+speed*delta;
        newX=Math.min(Gdx.graphics.getWidth()-paddleImage.getWidth(),newX);
        paddleImage.setPosition(newX,paddleImage.getY());
    }

*/

}
