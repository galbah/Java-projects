package src.brick_strategies;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Camera;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.Ball;
import src.gameobjects.Puck;

/**
 * this class represents the collision strategy in which the camera starts following
 * the main ball until it collides 4 times.
 */
public class MoveCamera extends CollisionStrategy {

    private final GameManager gameManager;
    private final Ball ball;
    private final Vector2 windowDimensions;

    public MoveCamera(GameObjectCollection gameObjects, GameManager gameManager, Ball ball,
                      Vector2 windowDimensions) {
        super(gameObjects);
        this.gameManager = gameManager;
        this.ball = ball;
        this.windowDimensions = windowDimensions;
    }

    @Override
    public void onCollision(GameObject collidedObj, GameObject colliderObj, Counter bricksCounter) {
        super.onCollision(collidedObj, colliderObj, bricksCounter);
        if (gameManager.getCamera() == null && !(colliderObj instanceof Puck)) {
            gameManager.setCamera(
                    new Camera(
                            ball, //object to follow
                            Vector2.ZERO, //follow the center of the object
                            windowDimensions.mult(1.2f), //widen the frame a bit
                            windowDimensions //share the window dimensions
                    )
            );
            ball.activateCollisionCount();
        }
    }
}
