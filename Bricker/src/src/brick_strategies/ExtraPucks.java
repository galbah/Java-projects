package src.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.Puck;

import java.util.Random;

/**
 * this class represents the collision strategy in which 3 extra balls are added,
 * the balls move in a random way from the broken brick.
 */
public class ExtraPucks extends CollisionStrategy {

    private final float PUCK_SIZE;
    private static final float PUCK_SPEED = 300;
    private static final int NUM_OF_PUCKS = 3;
    private final Vector2 windowDimensions;


    public ExtraPucks(GameObjectCollection gameObjects, ImageReader imageReader,
                             SoundReader soundReader, Vector2 windowDimensions, Vector2 brickDim) {
        super(gameObjects);
        this.soundReader = soundReader;
        this.imageReader = imageReader;
        this.windowDimensions = windowDimensions;
        this.PUCK_SIZE = brickDim.x() / NUM_OF_PUCKS;
    }

    @Override
    public void onCollision(GameObject collidedObj, GameObject colliderObj, Counter bricksCounter) {
        super.onCollision(collidedObj, colliderObj, bricksCounter);
        Renderable puckImage = imageReader.readImage("assets/mockBall.png", true);
        Sound puckCollisionSound = soundReader.readSound("assets/Bubble5_4.wav");
        Puck puck = new Puck(collidedObj.getCenter(), new Vector2(PUCK_SIZE, PUCK_SIZE),
                puckImage, puckCollisionSound, gameObjects, windowDimensions);
        gameObjects.addGameObject(puck);
        movePuck(puck);
    }

    /**
     * randomly decides in witch direction the puck will move.
     * @param puck the ball to decide movement.
     */
    private void movePuck(GameObject puck){
        float ballVelY = PUCK_SPEED;
        float ballVelX = PUCK_SPEED;
        Random rand = new Random();
        if(rand.nextBoolean()){
            ballVelX *= -1;
        }
        if(rand.nextBoolean()){
            ballVelY *= -1;
        }
        puck.setVelocity(new Vector2(ballVelX, ballVelY));
    }
}
