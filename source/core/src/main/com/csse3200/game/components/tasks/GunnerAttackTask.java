package com.csse3200.game.components.tasks;

import com.badlogic.gdx.utils.Array;
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
    private static final int GUNNER_TASK_PRIORITY = 10;

    /**
     * Creates a new GunnerAttackTask with the specified attack range.
     *
     * @param attackRange The range at which the gunner can attack targets
     */
    public GunnerAttackTask(float attackRange) {
        super(attackRange);
    }

    @Override
    public void start() {
        super.start();
        //this.owner.getEntity().getEvents().trigger("gunnerAttackStart");
        logger.info("GunnerAttackTask started for {}", owner.getEntity());
    }

    @Override
    public void update() {
        currentTarget = getNearestVisibleTarget();
        // verify if gunner can see target to confirm projectile spawns
        if (currentTarget != null) {
            float distance = getDistanceToTarget();
            if (distance <= attackRange) {
                super.update();
                logger.info("Gunner firing at: {} from position {}",
                        currentTarget.getPosition(), owner.getEntity().getPosition());
            } else {
                logger.info("Gunner out of range. Distance {} > attackRange {}");
            }
        } else {
            logger.info("Gunner cannot see target");
        }
    }

    @Override
    public int getPriority() {
        return GUNNER_TASK_PRIORITY;
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
        //Array<Entity> copy = new Array<>(allEntities);
        List<Entity> targets = new ArrayList<>();

        for (Entity entity : allEntities) {
            // Check if the entity is a defense entity
            boolean isDefense = entity.getComponent(DefenderStatsComponent.class) != null;

            if (isDefense) {
                //logger.info("Found defense target: {} at position {}", entity, entity.getPosition());
                targets.add(entity);
            }
        }
        //logger.info("GunnerAttackTask found {} targets", targets.size());
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

}
