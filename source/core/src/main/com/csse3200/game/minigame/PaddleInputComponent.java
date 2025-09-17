package com.csse3200.game.minigame;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

public class PaddleInputComponent extends Component{
    private PaddleComponent paddle;
    public PaddleInputComponent(Entity paddleEntity){
       this.paddle=paddleEntity.getComponent(PaddleComponent.class);
    }
    @Override
    public void update(){
        float delta=Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            paddle.moveLeft(delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
           paddle.moveRight(delta);
        }
    }


}
