package src.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Paddle extends GameObject {
    private static final int MOVE_SPEED = 500;
    private final UserInputListener inputListener;
    private final Vector2 windowDimensions;
    private final int minDistFromEdge;

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
     */
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, Vector2 windowDimensions,
                  int minDistFromEdge) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.minDistFromEdge = minDistFromEdge;
    }

    /**
     * determines movement direction of paddle according to the arrows of KBD.
     * the speed of the movement is determined according to MOVE_SPEED.
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
        Vector2 movementDir = Vector2.ZERO;
        float halfObjLen = getDimensions().x()/2;
        if(getCenter().x()-halfObjLen >= minDistFromEdge){ //check in border
            if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
                movementDir = movementDir.add(Vector2.LEFT);
            }
        }
        if(getCenter().x()+halfObjLen <= windowDimensions.x() - minDistFromEdge){
            if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
                movementDir = movementDir.add(Vector2.RIGHT);
            }
        }
        setVelocity(movementDir.mult(MOVE_SPEED));
    }

}

