package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * This class represents the graphic lives that are shown on the screen.
 * the class makes sure that the number of lives update accordingly to the game.
 */
public class GraphicLifeCounter extends GameObject {
    private final Counter livesCounter;
    private final GameObjectCollection gameObjectCollection;
    private  Vector2 topLeftCorner;
    private final Vector2 heartDim;
    private final Renderable heartImage;
    private int numOfLives;
    private final GameObject[] lives; // array of hearts

    /**
     * Construct a new GraphicLifeCounter instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     * @param gameObjectCollection    the collections of game objects.
     * @param maxLives      the maximum lives that a player can get in the game.
     */
    public GraphicLifeCounter(Vector2 topLeftCorner, Vector2 dimensions, Counter livesCounter,
                              Renderable renderable, GameObjectCollection gameObjectCollection,
                              int maxLives) {
        super(topLeftCorner, dimensions, renderable);
        this.heartImage = renderable;
        this.topLeftCorner = topLeftCorner;
        this.heartDim = dimensions;
        this.lives = new GameObject[maxLives];
        this.livesCounter = livesCounter;
        this.gameObjectCollection = gameObjectCollection;
        this.numOfLives = livesCounter.value(); // to compare with counter
        for (int i = 0; i < numOfLives; i++) {
            this.lives[i] = new Heart(this.topLeftCorner, heartDim, heartImage);
            gameObjectCollection.addGameObject(this.lives[i], Layer.BACKGROUND);
            this.topLeftCorner = this.topLeftCorner.add(new Vector2(dimensions.x(), 0));
        }
    }

    /**
     * checks if the user lost a round and update the amount of lives that are left.
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(livesCounter.value() < numOfLives) {
            numOfLives--;
            gameObjectCollection.removeGameObject(lives[numOfLives], Layer.BACKGROUND);
            topLeftCorner = topLeftCorner.add(new Vector2(-heartDim.x(), 0));
        }
        if(livesCounter.value() > numOfLives){
            lives[numOfLives] = new Heart(topLeftCorner, heartDim, heartImage);
            topLeftCorner = topLeftCorner.add(new Vector2(heartDim.x(), 0));
            gameObjectCollection.addGameObject(lives[numOfLives], Layer.BACKGROUND);
            numOfLives++;
        }
    }
}
