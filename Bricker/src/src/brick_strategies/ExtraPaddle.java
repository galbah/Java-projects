package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.SecondPaddle;

/**
 * this class represents the collision strategy in which a second paddle is added to middle of the screen
 * the second paddle move the same as the user main one and inherit from it.
 */
public class ExtraPaddle extends CollisionStrategy {

    private final Vector2 windowDimensions;
    private static final Vector2 EXTRA_PADDLE_DIM = new Vector2(200,20);
    private static final int PADDLE_MIN_DIST_FROM_EDGE = 30;
    private final UserInputListener inputListener;
    private final Counter state;


    public ExtraPaddle(GameObjectCollection gameObjects, ImageReader imageReader, Vector2 windowDimensions,
                       UserInputListener inputListener) {
        super(gameObjects);
        this.imageReader = imageReader;
        this.windowDimensions = windowDimensions;
        this.inputListener = inputListener;
        this.state = new Counter(0);
    }

    @Override
    public void onCollision(GameObject collidedObj, GameObject colliderObj, Counter bricksCounter) {
        super.onCollision(collidedObj, colliderObj, bricksCounter);
        if(state.value() == 0) {
            state.increment();
            Renderable extraPaddleImage = imageReader.readImage("assets/botGood.png", true);
            Vector2 paddleTopLeft = new Vector2(windowDimensions.x() / 2 - EXTRA_PADDLE_DIM.x() / 2,
                    windowDimensions.y() / 2 - EXTRA_PADDLE_DIM.y() / 2);
            SecondPaddle extraPaddle = new SecondPaddle(paddleTopLeft, EXTRA_PADDLE_DIM, extraPaddleImage, inputListener,
                    windowDimensions, PADDLE_MIN_DIST_FROM_EDGE, gameObjects, state);
            gameObjects.addGameObject(extraPaddle);
        }
    }
}
