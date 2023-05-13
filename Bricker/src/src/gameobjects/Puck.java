package src.gameobjects;

import danogl.collisions.GameObjectCollection;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * represents a Puck ball - it's a ball that is not the main.
 * can collide with objects but doesn't affect lives in game.
 * inherits from Ball.
 */
public class Puck extends Ball {

    private final Vector2 puckDimensions;
    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner  Position of the object, in window coordinates (pixels).
     *                       Note that (0,0) is the top-left corner of the window.
     * @param puckDimensions     Width and height in window coordinates.
     * @param renderable     The renderable representing the object. Can be null, in which case
     *                       the GameObject will not be rendered.
     * @param collisionSound the sound that is made when Puck collides with another object.
     * @param gameObjects all game objects that were added to the game.
     * @param windowDimensions game's window dimensions
     */
    public Puck(Vector2 topLeftCorner, Vector2 puckDimensions, Renderable renderable, Sound collisionSound,
                GameObjectCollection gameObjects, Vector2 windowDimensions) {
        super(topLeftCorner, puckDimensions, renderable, collisionSound);
        this.puckDimensions = puckDimensions;
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
    }
}
