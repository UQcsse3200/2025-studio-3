package com.csse3200.game.components.tasks;

import com.badlogic.gdx.utils.Array;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * A specialized attack task for the Gunner Robot that targets defensive structures.
 * This task extends the base AttackTask but specifically targets entities with
 * DefenderStatsComponent, which are typically defensive structures.
 */
public class GunnerAttackTask extends AttackTask {
    private static final Logger logger = LoggerFactory.getLogger(GunnerAttackTask.class);
    private Entity currentTarget;

    /**
     * Creates a new GunnerAttackTask with the specified attack range.
     *
     * @param attackRange The range at which the gunner can attack targets
     */
    public GunnerAttackTask(float attackRange) {
        super(attackRange);
    }

    /**
     * Gets all potential defense targets that this gunner can attack.
     * Targets are identified by the presence of a DefenderStatsComponent.
     *
     * @return List of target defense entities
     */
    @Override
    protected List<Entity> getAllTargets() {
        Array<Entity> allEntities = ServiceLocator.getEntityService().getEntities();
        Array<Entity> copy = new Array<>(allEntities);
        List<Entity> targets = new ArrayList<>();

        for (Entity entity : copy) {
            // Check if the entity is a defense structure
            boolean isDefense = entity.getComponent(DefenderStatsComponent.class) != null;

            if (isDefense) {
                targets.add(entity);
            }
        }
        logger.info("GunnerAttackTask found {} targets", targets.size());
        return targets;
    }

    /**
     * Gets the current target of the attack.
     *
     * @return The current target entity, or null if no target is found
     */
    public Entity getCurrentTarget() {
        return currentTarget;
    }

    @Override
    public void update() {
        currentTarget = getNearestVisibleTarget();
        super.update();
    }

    @Override
    public void start() {
        super.start();
        this.owner.getEntity().getEvents().trigger("gunnerAttackStart");
    }

    @Override
    public void stop() {
        super.stop();
    }
}