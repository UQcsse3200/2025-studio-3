package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.files.FileHandle;
import com.csse3200.game.GdxGame;

public class WorldMapScreen implements Screen {

    private final GdxGame game;

    private com.badlogic.gdx.graphics.Texture worldMap;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    private Node[] nodes;
    private Vector2 playerPos;
    private float playerSpeed = 200f;

    private Node nearbyNode = null;

    public WorldMapScreen(GdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        worldMap = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("images/world_map.png"));

        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();


        FileHandle file = Gdx.files.internal("data/nodes.json");
        Json json = new Json();
        nodes = json.fromJson(Node[].class, file);


        playerPos = new Vector2(142, 269);

    }

    @Override
    public void render(float delta) {


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);


        batch.begin();
        batch.draw(worldMap, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Node node : nodes) {
            if (node.locked) {
                shapeRenderer.setColor(Color.RED);
            } else {
                shapeRenderer.setColor(Color.GREEN);
            }
            shapeRenderer.circle(node.px, node.py, 16);
        }


        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(playerPos.x, playerPos.y, 32, 32);
        shapeRenderer.end();


        nearbyNode = null;
        for (Node node : nodes) {
            Rectangle nodeRect = new Rectangle(node.px - 16, node.py - 16, 32, 32);
            Rectangle playerRect = new Rectangle(playerPos.x, playerPos.y, 32, 32);
            if (playerRect.overlaps(nodeRect)) {
                nearbyNode = node;
                break;
            }
        }


        batch.begin();
        if (nearbyNode != null) {
            font.draw(batch, "Press E to " + nearbyNode.label,
                    nearbyNode.px, nearbyNode.py + 50);
        }
        batch.end();


        if (nearbyNode != null && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (nearbyNode.locked) {
                System.out.println("Level is locked: " + nearbyNode.lockReason);
            } else {
                System.out.println("Loading level: " + nearbyNode.label);
            }
        }


        if (Gdx.input.justTouched()) {
            int x = Gdx.input.getX();
            int y = Gdx.graphics.getHeight() - Gdx.input.getY();
            System.out.println("Clicked at: (" + x + ", " + y + ")");
        }
    }



    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        worldMap.dispose();
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}

