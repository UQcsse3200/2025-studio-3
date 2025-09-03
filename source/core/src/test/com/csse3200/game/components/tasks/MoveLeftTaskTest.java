package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.ai.tasks.TaskRunner;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the enemy robots/entities moving to the left
 * like in PVZ's
 */
class MoveLeftTaskTest {
    private Entity entity; // entity to test
    private WanderTask wanderTask; // AI behaviour to test


    @BeforeEach
    void setUp() {
        ServiceLocator.clear();

        // mock up gametime (simulate gameclock) for headless tests
        ServiceLocator.registerTimeSource(new GameTime() {
            @Override
            public float getDeltaTime() {
                return 1f / 60f; // 60fps
            }
        });

        // register a physics engine so entities can move/collide
        ServiceLocator.registerPhysicsService(new PhysicsService());

        // create new entity & add physics/movement components to it
        entity = new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new PhysicsMovementComponent());

        wanderTask = new WanderTask(new Vector2(2f, 2f), 1f);
        wanderTask.create(new TaskRunner() {
            @Override
            public Entity getEntity() {
                return entity;
            }
        });
    }

    // test to see iff the entity moves left

    @Test
    void testMoveLeft() {
        //set entity starting position
        Vector2 initialPosition = new Vector2(5f, 5f);
        entity.setPosition(initialPosition);
        // set AI to begin its behaviour
        wanderTask.start();
        wanderTask.update();

        // verify iff entity has move left from its starting position
        Vector2 target = entity.getComponent(PhysicsMovementComponent.class).getTarget();
        // confirm entity is not null
        assertNotNull(target, "Target should not be null after start()");
        assertTrue(target.x < initialPosition.x, "Entity should move left");
    }


    // test to check priority of the task
    @Test
    void testPriority() {
        assertEquals(1, wanderTask.getPriority(), "WanderTask priority is 1");

    }

    // test to see iff entity stops at a object (tree)

    @Test
    public void testCollision() {
        // create a object/tree for the testing environment
        // TODO: will need to replace tree with an defence object once implemented

        Entity tree = new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.OBSTACLE));

        tree.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.StaticBody);
        tree.setPosition(2f, 1.5f);

        // create an entity robot near the object to collide with
        Entity robot = new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC));
        robot.setPosition(1f, 1.5f);

        // move the robot right towards object (tree)
        robot.getComponent(PhysicsComponent.class).getBody().setLinearVelocity(1f, 0f);

        // make robot collide with object (tree)
        for (int i = 0; i < 60; i ++) {
            ServiceLocator.getPhysicsService().getPhysics().update();
        }

        float finalX = robot.getPosition().x;
        assertTrue(finalX < 2f, "Robot should stop at object");
    }

    // test for checking iff robot stops at boundary of map

    @Test
    void testEnd() {
        // set up required services
        ServiceLocator.clear();
        ServiceLocator.registerTimeSource(new GameTime());
        ServiceLocator.registerPhysicsService(new PhysicsService());

        // create entity for the test (robot)
        Entity entity = new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new PhysicsMovementComponent());
        //create new task for this testing entity
        WanderTask wanderTask = new WanderTask(new Vector2(2f, 2f), 1f);
        wanderTask.create(new TaskRunner() {

            @Override
            public Entity getEntity() {
                return entity;
            }
        });

        // manually create world boundaries (avoid loading terrain textures)
        float worldWidth = 10f;
        float worldHeight = 5f;

        // set entity to the edge of map
        entity.setPosition(new Vector2(worldWidth - 0.5f, worldHeight / 2f));
        wanderTask.update();
        assertTrue(entity.getPosition().x <= worldWidth, "Entity should not move beyond boundary");
    }


    @AfterEach
    void tearDown() {
        // clear all services after every test
        ServiceLocator.clear();
    }


}













