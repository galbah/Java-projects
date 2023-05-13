package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;

/**
 * this class responsible to the avatar energy
 */
public class Energy extends GameObject {

    public static final String ENERGY_TAG = "energy";
    public static final Vector2 SIZE = new Vector2(50, 50);
    private static final double MAX_ENERGY = 100;
    private static final double CHANGE_FACTOR = 0.5;
    private double counter = 100;
    TextRenderable textRenderable = new TextRenderable("100");

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public Energy(Vector2 topLeftCorner, Vector2 dimensions) {
        super(topLeftCorner, dimensions, null);
        this.renderer().setRenderable(textRenderable);
        textRenderable.setColor(Color.green);
        textRenderable.setString("energy: " + counter);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * increase the avatar energy
     */
    public void increase() {
        if (counter < MAX_ENERGY) {
            counter += CHANGE_FACTOR;
        }
    }

    public double getEnergy() {
        return counter;
    }

    /**
     * decrease the avatar energy
     */
    public void decrease() {
        if (counter > 0) {
            counter -= CHANGE_FACTOR;
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        textRenderable.setString("energy: " + counter);
    }
}
