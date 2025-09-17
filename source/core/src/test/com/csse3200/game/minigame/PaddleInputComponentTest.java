package com.csse3200.game.minigame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class PaddleInputComponentTest{
    private static TestPaddle paddle;
    private TestPaddleInputComponent inputComponent;
    private static class TestPaddle extends PaddleComponent{
        float x=200f;
        public TestPaddle(){
            super(null);
        }

     @Override
     public void moveLeft(float delta){
            x-=50*delta;
     }
     @Override
     public void moveRight(float delta){
            x+=50*delta;
        }
        public float getX(){
            return x;
        }

    }
    private static class TestPaddleInputComponent extends PaddleInputComponent{
        boolean leftPressed=false;
        boolean rightPressed=false;

        public TestPaddleInputComponent(Entity paddleEntity){
            super(paddleEntity);

        }
        @Override
        public void update(){
            float delta =0.1f;
            if(leftPressed)paddle.moveLeft(delta);
            if(rightPressed)paddle.moveRight(delta);

        }
        void pressLeft(){leftPressed=true;rightPressed=false;}
        void pressRight(){rightPressed=true;leftPressed=false;}
        void release(){leftPressed=false;rightPressed=false;}

    }
    @BeforeEach
    void setUp(){
        ServiceLocator.clear();
        Entity paddleEntity=new Entity();
        paddle=new TestPaddle();
        paddleEntity.addComponent(paddle);
        paddleEntity.create();

        inputComponent=new TestPaddleInputComponent(paddleEntity);
    }
    @Test
    void shouldMoveLeftWhenLeftKeyPressed(){
        float startX= paddle.getX();
        inputComponent.pressLeft();
        inputComponent.update();
        assertTrue(paddle.getX()<startX,"Paddle should move left when left key is pressed ");

    }
    @Test
    void shouldMoveRightWhenRightKeyPressed(){
        float startX= paddle.getX();
        inputComponent.pressRight();
        inputComponent.update();
        assertTrue(paddle.getX()>startX,"Paddle should move right when right key is pressed ");

    }
    @Test
    void shouldNotMoveWhenNoKeyPressed(){
        float startX= paddle.getX();
        inputComponent.release();
        inputComponent.update();
        assertTrue(paddle.getX()==startX,"Paddle should move right when right key is pressed ");

    }



}