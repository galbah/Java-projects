package src.gameobjects;

import src.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * this class represents a ball in a game. it inherits from GameObject
 */
public class Brick extends GameObject {

    private final CollisionStrategy collisionStrategy;
    private final Counter bricksCounter;
    private boolean wasCollided = false;

    /**
     * Construct a new GameObject - Brick instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     * @param collisionStrategy    The strategy that is activated when brick is collided with
     *                             another object.
     * @param bricksCounter    A counter that represents the number of bricks that were broken.
     *                         when a brick is collided it increases the counter.
     */
    public Brick(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                 CollisionStrategy collisionStrategy, Counter bricksCounter) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionStrategy = collisionStrategy;
        this.bricksCounter = bricksCounter;
    }

    /**
     * activates the collision strategy when an object hits a brick.
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(!wasCollided) {
            wasCollided = true;
            collisionStrategy.onCollision(this, other, bricksCounter);
        }
    }
}
