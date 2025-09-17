package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class BallComponentTest {

    private float screenWidth=800f;
    private float screenHeight=600f;

    @Before
    public void setUp() {
        Gdx.graphics=Mockito.mock(Graphics.class);
        Mockito.when(Gdx.graphics.getWidth()).thenReturn((int) screenWidth);
        Mockito.when(Gdx.graphics.getHeight()).thenReturn((int) screenHeight);

    }

    @Test
    public void ballMovesCorrectly() {
        Image ballImage = new Image();
        ballImage.setPosition(100,100);
        BallComponent ball= new BallComponent(ballImage, 50f, 50f);

        ball.update(1f);

        assertEquals(150f, ballImage.getX(), 0.01f);
        assertEquals(150f, ballImage.getY(), 0.01f);
    }

    @Test
    public void ballBouncesOffLeftWall() {
        Image ballImage = new Image();
        ballImage.setPosition(0,100);
        BallComponent ball= new BallComponent(ballImage, -50f, 0f);
        int initialScore= ball.getScore();

        ball.update(1f);

        assertEquals(0f, ballImage.getX(), 0.01f);
        assertTrue("velocity x should be positive", ballImage.getX() >=0);
        assertEquals(initialScore+1, ball.getScore());

    }

    @Test
    public void ballBouncesOffRightWall() {
        Image ballImage = new Image();
        ballImage.setSize(40,40);
        ballImage.setPosition(screenWidth-40,100);
        BallComponent ball= new BallComponent(ballImage, 50f, 0f);
        int initialScore= ball.getScore();

        ball.update(1f);

        assertEquals(screenWidth-40, ballImage.getX(), 0.01f);
        assertTrue("velocity x should be negative", ballImage.getX()<=screenWidth-40);
        assertEquals(initialScore+1, ball.getScore());
    }


}