package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.Heart;

/**
 * this class represents the collision strategy in which a heart falls from the brick,
 * if the paddle collects the heart a life is added.
 */
public class ExtraLife extends CollisionStrategy {

    private static final float HEART_SPEED = 100;
    private final int heartSize;
    private final Counter livesCounter;

    public ExtraLife(GameObjectCollection gameObjects, int heartSize, ImageReader imageReader,
                     Counter livesCounter) {
        super(gameObjects);
        this.heartSize = heartSize;
        this.livesCounter = livesCounter;
        this.imageReader = imageReader;
    }

    @Override
    public void onCollision(GameObject collidedObj, GameObject colliderObj, Counter bricksCounter) {
        super.onCollision(collidedObj, colliderObj, bricksCounter);
        Renderable heartImage = imageReader.readImage("assets/heart.png", true);
        GameObject heart = new Heart(collidedObj.getCenter(), new Vector2(heartSize, heartSize),
                heartImage, livesCounter, gameObjects);
        heart.setVelocity(new Vector2(0, HEART_SPEED));
        gameObjects.addGameObject(heart);
    }
}
