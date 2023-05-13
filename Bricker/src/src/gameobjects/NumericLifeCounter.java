package src.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;

/**
 * this class represents a number on the screen that shows the number of lives that are left.
 * the class makes sure that the number updates according to the lives counter.
 */
public class NumericLifeCounter extends GameObject {
    private final Counter livesCounter;
    private final TextRenderable numLivesRender;
    private int curNumOfLives;

    /**
     * Construct a new NumericLifeCounter instance.
     *
     * @param livesCounter  a counter that is updated in another place and represents the
     *                      number of lives that are left in the game.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param gameObjectCollection the game objects that are added to the game.
     */
    public NumericLifeCounter(Counter livesCounter, Vector2 topLeftCorner, Vector2 dimensions,
                              GameObjectCollection gameObjectCollection) {
        super(topLeftCorner, dimensions, null);
        this.livesCounter = livesCounter;
        this.curNumOfLives = livesCounter.value();
        numLivesRender = new TextRenderable(String.format("%d", livesCounter.value()));
        GameObject numLivesObj = new GameObject(topLeftCorner, dimensions, numLivesRender);
        gameObjectCollection.addGameObject(numLivesObj, Layer.BACKGROUND);
        numLivesRender.setColor(Color.GREEN);
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
        if(curNumOfLives > livesCounter.value()){
            curNumOfLives--;
            numLivesRender.setString(String.format("%d", curNumOfLives));
        }
        if(curNumOfLives < livesCounter.value()){
            curNumOfLives++;
            numLivesRender.setString(String.format("%d", curNumOfLives));
        }
        if(curNumOfLives > 2){
            numLivesRender.setColor(Color.GREEN);
        }
        if(curNumOfLives == 2){
            numLivesRender.setColor(Color.YELLOW);
        }
        if(curNumOfLives == 1){
            numLivesRender.setColor(Color.RED);
        }
    }
}
