package com.csse3200.game.minigame;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.entities.Entity;


public class ObstacleSpawner {
    private final ObstacleEntity[] obstacles;
    private int obstacleCount = 0;
    private float timer = 0f;
    private final Texture obstacleTex;

    public ObstacleSpawner(Texture tex) {
        this.obstacleTex = tex;
        this.obstacles = new ObstacleEntity[LaneConfig.OBSTACLE_MAX_COUNT];
    }

    public void update(float delta, Entity player, LaneManager laneManager) {
        timer += delta;

        // Spawn new obstacle
        if (timer >= LaneConfig.OBSTACLE_SPAWN_INTERVAL && obstacleCount < LaneConfig.OBSTACLE_MAX_COUNT) {
            int laneIndex = (int) (Math.random() * laneManager.getNumLanes());
            float x = laneManager.getLaneCenter(laneIndex);
            float y = LaneConfig.SCREEN_WIDTH; // top of screen
            float speed = LaneConfig.OBSTACLE_BASE_SPEED;

            obstacles[obstacleCount] = new ObstacleEntity(
                    x, y,
                    LaneConfig.OBSTACLE_WIDTH,
                    LaneConfig.OBSTACLE_HEIGHT,
                    obstacleTex,
                    speed
            );
            obstacleCount++;
            timer = 0f;
        }

        // Update obstacles and check collision
        for (int i = 0; i < obstacleCount; i++) {
            obstacles[i].update(delta);

            if (obstacles[i].collidesWith(player)) {
                System.out.println("GAME OVER!");
            }
        }

        // Remove off-screen obstacles
        for (int i = 0; i < obstacleCount;) {
            if (obstacles[i].isOffScreen()) {
                removeObstacleAt(i);
            } else {
                i++;
            }
        }
    }

    private void removeObstacleAt(int index) {
        for (int i = index; i < obstacleCount - 1; i++) {
            obstacles[i] = obstacles[i + 1];
        }
        obstacles[obstacleCount - 1] = null;
        obstacleCount--;
    }

    public ObstacleEntity[] getObstacles() {
        return obstacles;
    }

    public int getObstacleCount() {
        return obstacleCount;
    }
}
