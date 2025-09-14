package com.csse3200.game.components;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.areas.LevelGameArea;

public class AttackComponent extends Component {
    private final LevelGameArea level;
    private boolean isAttacking = false;
    private float attackInterval = 2f; // seconds between shots
    private float timeSinceLastAttack = 0f;

    public AttackComponent(LevelGameArea level) {
        this.level = level;
    }
}
