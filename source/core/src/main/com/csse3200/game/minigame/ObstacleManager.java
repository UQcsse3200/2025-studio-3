package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

import java.util.List;
import java.util.Random;

public class ObstacleManager {
    private final List<Entity> obstacles;
    private final LaneManager laneManager;
    public final Random random;
    private float spawnTimer;

    public ObstacleManager(LaneManager laneManager) {
        this.laneManager = laneManager;
        this.obstacles = new java.util.ArrayList<>();
        this.random = new Random();
        this.spawnTimer = 0f;
    }

    public void update(float deltaTime) {
        spawnTimer += deltaTime;
        if (spawnTimer >= LaneConfig.OBSTACLE_SPAWN_INTERVAL && obstacles.size() < LaneConfig.OBSTACLE_MAX_COUNT) {
            spawnObstacle();
            spawnTimer = 0f;
        }
        List <Entity> toRemove = new java.util.ArrayList<>();
        for (Entity obstacle : obstacles) {
            ObstacleComponent component = obstacle.getComponent(ObstacleComponent.class);
            if (component != null) {
                component.update();
            }
            if (obstacle.getPosition().y < 0.2f) {
                toRemove.add(obstacle);
            }

        }

        for (Entity obstacle : toRemove) {
            obstacles.remove(obstacle);
            ServiceLocator.getEntityService().unregister(obstacle);
            obstacle.dispose();
        }
    }
    private void spawnObstacle() {
        int LaneIndex = random.nextInt(laneManager.getNumLanes());
        float x = laneManager.getLaneCenter(LaneIndex);
        float y = Gdx.graphics.getHeight() ;
        Entity obstacle=ObstacleFactory.createObstacle(x, y, LaneConfig.OBSTACLE_BASE_SPEED);
        obstacles.add(obstacle);
        ServiceLocator.getEntityService().register(obstacle);
    }
    public boolean checkCollision(Entity player) {
        for (Entity obstacle : obstacles) {
            if (isColliding(player, obstacle)) {
                return true;
            }
        }
        return false;
    }
    private boolean isColliding(Entity player, Entity obstacle) {
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;
        float playerWidth = player.getScale().x;
        float playerHeight = player.getScale().y;

        float obstacleX = obstacle.getPosition().x;
        float obstacleY = obstacle.getPosition().y;
        float obstacleWidth = obstacle.getScale().x;
        float obstacleHeight = obstacle.getScale().y;

        return Math.abs(playerX-obstacleX) < (playerWidth + obstacleWidth) / 2 &&
                Math.abs(playerY-obstacleY) < (playerHeight + obstacleHeight) / 2;
    }

    public void clearObstacles() {
        for (Entity obstacle : obstacles) {
            obstacle.dispose();
        }
        obstacles.clear();
        spawnTimer = 0f;
    }
    public int getObstacleCount() {
        return obstacles.size();
    }

}
