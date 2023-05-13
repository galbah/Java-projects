package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * represents a second paddle - it's a paddle that is not the main.
 * can collide with objects but disappears after a number of collisions.
 * inherits from paddle.
 */
public class SecondPaddle extends Paddle {

    private final Counter collisonCounter;
    private static final int MAX_COLLISIONS = 3;
    private final GameObjectCollection gameObjects;
    private final Counter secondPaddleState;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     * @param inputListener gets user actions on kbd and mouse.
     * @param windowDimensions game window dimensions.
     * @param minDistFromEdge minimum distance from sides of window.
     * @param gameObjects game objects that were added to the game.
     * @param secondPaddleState contains 1 if the paddle is activated and 0 if not.
     *                          if its activated another one should not be added.
     */
    public SecondPaddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                        UserInputListener inputListener, Vector2 windowDimensions,
                        int minDistFromEdge, GameObjectCollection gameObjects, Counter secondPaddleState) {
        super(topLeftCorner, dimensions, renderable, inputListener, windowDimensions, minDistFromEdge);
        this.collisonCounter = new Counter(0);
        this.gameObjects = gameObjects;
        this.secondPaddleState = secondPaddleState;
    }

    @Override // counts collisions
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.collisonCounter.increment();
    }

    @Override // if collides MAX_COLLISION time - disappears.
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(this.collisonCounter.value() >= MAX_COLLISIONS){
            gameObjects.removeGameObject(this);
            secondPaddleState.decrement();
        }
    }
}
