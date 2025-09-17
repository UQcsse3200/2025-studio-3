package com.csse3200.game.minigame;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.minigame.PaddleComponent;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;




@ExtendWith(GameExtension.class)
class PaddleInputComponentTest{
    private TestPaddle paddleComponent;
    private static class TestPaddle extends PaddleComponent{
        float x=200f;
        public TestPaddle() {
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
    @BeforeEach
    void setUp(){
        ServiceLocator.registerPhysicsService(new PhysicsService());
        Entity paddeleEntity=new Entity();
        paddleComponent=new TestPaddle();
        paddeleEntity.addComponent(paddleComponent);
        paddeleEntity.create();
    }
    @Test
    void shouldMoveLeft(){
        float startX=paddleComponent.getX();
        paddleComponent.moveLeft(0.1f);
        assertTrue(paddleComponent.getX()<startX,"paddle should move left");
    }
    @Test
    void shouldMoveRight(){
        float startX=paddleComponent.getX();
        paddleComponent.moveRight(0.1f);
        assertTrue(paddleComponent.getX()>startX,"paddle should move right");
    }





}