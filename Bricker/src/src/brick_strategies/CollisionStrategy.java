package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.util.Counter;

/**
 * this class represents a collision strategy, in that nothing happen when a brick is collided
 * other strategy inherit from this one.
 */
public class CollisionStrategy {

    protected final GameObjectCollection gameObjects;
    protected SoundReader soundReader = null;
    protected ImageReader imageReader = null;

    public CollisionStrategy(GameObjectCollection gameObjects){
        this.gameObjects = gameObjects;
    }


    /**
     * determines what will happen when an object collides with another.
     * @param collidedObj the object that was collided.
     * @param colliderObj the object that is the collider.
     * @param bricksCounter a counter for the bricks that have been collided.
     */
    public void onCollision(GameObject collidedObj, GameObject colliderObj, Counter bricksCounter){
        gameObjects.removeGameObject(collidedObj);
        bricksCounter.increment(); // updates that a brick was collided
    }
}

