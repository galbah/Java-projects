package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * this class represents a heart in the game. it can be an object in the game that can be picked and used
 * and can be only a sign that represents a life in the game.
 */
public class Heart extends GameObject {

    private Counter livesCounter = null;
    private GameObjectCollection gameObjects = null;

    /**
     * Construct a new Heart instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    // constructor for game object hearts
    public Heart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                 Counter livesCounter, GameObjectCollection gameObjects) {
        super(topLeftCorner, dimensions, renderable);
        this.livesCounter = livesCounter;
        this.gameObjects = gameObjects;
    }

    // constructor for static hearts
    public Heart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    @Override // falling heart should collide with main paddle
    public boolean shouldCollideWith(GameObject other) {
        return ((other instanceof Paddle) && !(other instanceof SecondPaddle));
    }

    @Override // add life if heart is collided with paddle
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(livesCounter.value() < 4){
            livesCounter.increment();
        }
        gameObjects.removeGameObject(this);
    }
}
