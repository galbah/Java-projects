package src.brick_strategies;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.gameobjects.Ball;

import java.util.Random;

public class StrategyFactory {
    protected final CollisionStrategy[] allStrategys;
    private final GameObjectCollection gameObjects;

    /**
     * this class controls building Strategy for game,
     * it builds a new array with all CollisionStrategy.
     */
    public StrategyFactory(GameObjectCollection gameObjectCollection,
                           ImageReader imageReader, SoundReader soundReader,
                           Vector2 windowDimensions, UserInputListener inputListener,
                           GameManager gameManager, Ball ball, Vector2 brickDim, int heartSize,
                           Counter livesCounter) {
        this.gameObjects = gameObjectCollection;
        this.allStrategys = new CollisionStrategy[]{
                new ExtraPucks(gameObjectCollection, imageReader, soundReader, windowDimensions, brickDim),
                new ExtraPaddle(gameObjectCollection, imageReader, windowDimensions, inputListener),
                new MoveCamera(gameObjectCollection, gameManager, ball, windowDimensions),
                new ExtraLife(gameObjectCollection, heartSize, imageReader, livesCounter),
                createNewDoubleStrategy(), // should be one before last in array.
                new CollisionStrategy(gameObjectCollection)}; // should be last in array.
    }

    private CollisionStrategy createNewDoubleStrategy() {
        return new DoubleAttribute(gameObjects);
    }

    /**
     * @param fromDoubleAttribute is the call made from the DoubleAttribute class.
     * @param secondDouble is the call made from a DoubleAttribute for second time.
     * @return a random strategy from the strategy array.
     * - if called from DoubleAttribute it randomly choose between special strategy.
     * - if called second time from DoubleAttribute it randomly choose between special strategy
     *   without the DoubleAttribute one.
     */
    public CollisionStrategy getRandomStrategy(boolean fromDoubleAttribute, boolean secondDouble){
        Random rand = new Random();
        if(!fromDoubleAttribute && !secondDouble) {
            CollisionStrategy chosenStrategy = allStrategys[rand.nextInt(allStrategys.length)];
            if(chosenStrategy instanceof DoubleAttribute){
                ((DoubleAttribute) chosenStrategy).fillDoubleAttribute();
            }
            return chosenStrategy;
        }
        else if(fromDoubleAttribute && !secondDouble) {
            CollisionStrategy chosenStrategy = allStrategys[rand.nextInt(allStrategys.length - 1)];
            if(chosenStrategy instanceof DoubleAttribute){
                ((DoubleAttribute) chosenStrategy).fillDoubleAttribute();
            }
            return chosenStrategy;
        }
        else {
            return allStrategys[rand.nextInt(allStrategys.length - 2)];
        }
    }

    /**
     * this class represents the collision strategy in which the brick can hold 2 strategy
     * isn't public because it doesn't stand for itself, only holds other strategy.
     */
    class DoubleAttribute extends CollisionStrategy{

        CollisionStrategy[] attributes = new CollisionStrategy[3];

        public DoubleAttribute(GameObjectCollection gameObjects) {
            super(gameObjects);
        }

        /** fills the attributes array with randomly chosen strategys
         */
        public void fillDoubleAttribute(){
            CollisionStrategy attribute1 = getRandomStrategy(true, false);
            CollisionStrategy attribute2 = getRandomStrategy(true, false);
            if(attribute1 instanceof DoubleAttribute || attribute2 instanceof DoubleAttribute){
                for(int i = 0 ; i < 3 ; i++){
                    attributes[i] = getRandomStrategy(true, true);
                }
            }
            else {
                attributes[0] = attribute1;
                attributes[1] = attribute2;
            }
        }

        /** activates every collision strategy that the object holds in array.
         * @param collidedObj the object that was collided.
         * @param colliderObj the object that is the collider.
         * @param bricksCounter a counter for the bricks that have been collided.
         */
        @Override
        public void onCollision(GameObject collidedObj, GameObject colliderObj, Counter bricksCounter) {
            super.onCollision(collidedObj, colliderObj, bricksCounter);
            for (CollisionStrategy collisionStrategy : this.attributes) {
                if(collisionStrategy == null){
                    continue;
                }
                bricksCounter.decrement(); // to cover the over-increment
                collisionStrategy.onCollision(collidedObj, colliderObj, bricksCounter);
            }
        }
    }
}

