package com.csse3200.game.minigame;

import com.csse3200.game.components.Component;

public class PlayerMovementComponent extends Component {
    private final LaneManager laneManager;
    private int currentLane = 1; // Start in the middle lane (0, 1, 2)

    public PlayerMovementComponent(LaneManager laneManager) {
        this.laneManager = laneManager;
    }

    @Override
    public void create() {
        entity.getEvents().addListener("moveLeft", this::moveLeft);
        entity.getEvents().addListener("moveRight", this::moveRight);
    }

    private void moveLeft() {
        if (currentLane > 0) {
            currentLane--;
            updatePosition();
        }
    }
    private void moveRight() {
        if (currentLane < laneManager.getNumLanes() - 1) {
            currentLane++;
            updatePosition();
        }
    }
    private void updatePosition() {
        float newX = laneManager.getLaneCenter(currentLane);
        entity.setPosition(newX, entity.getPosition().y);
    }


}


