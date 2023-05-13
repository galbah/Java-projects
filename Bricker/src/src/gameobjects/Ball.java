package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * this class represents a ball in a game. it inherits from GameObject
 */
public class Ball extends GameObject {

    private final Sound collisionSound;
    private final Counter collisionCounter;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner  Position of the object, in window coordinates (pixels).
     *                       Note that (0,0) is the top-left corner of the window.
     * @param dimensions     Width and height in window coordinates.
     * @param renderable     The renderable representing the object. Can be null, in which case
     *                       the GameObject will not be rendered.
     * @param collisionSound The sound that is made when a ball is collided with another object.
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;
        collisionCounter = new Counter();
    }

    /**
     * makes the ball go the to flipped direction of the collision after
     * hitting an object
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        Vector2 newVel = getVelocity().flipped(collision.getNormal());
        setVelocity(newVel);
        collisionSound.play();
        if(collisionCounter.value() > 0 && !(other instanceof Heart)) {
            collisionCounter.increment();
        }
    }

    public int getCollisionCount(){ // returns the collision counter value
        return collisionCounter.value();
    }

    // increases the counter - activates it (because it counts only when above 0).
    public void activateCollisionCount(){
        collisionCounter.increment();
    }

    // resets the counter to 0 - dis-activates it (because it counts only when above 0).
    public void resetCollisionCount(){
        collisionCounter.reset();
    }
}

