package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * this class represents the halo around the sun.
 */
public class SunHalo extends GameObject {

    private static final String SUNHALO_TAG = "sun_halo";
    private static final float DIM_FACTOR = 2;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public SunHalo(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     * creates a sunHalo objects and adds it to game object collection.
     *
     * @param gameObjects the collection that sunHalo should be added to.
     * @param layer       the layer that the sun halo should be created in.
     * @param sun         the sun object to set sunHalo to follow it.
     * @param color       the color of the sunHalo.
     * @return the object of the sunHalo that was created.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    GameObject sun, Color color) {
        GameObject sunHalo = new GameObject(Vector2.ZERO, sun.getDimensions().mult(DIM_FACTOR),
                new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.setTag(SUNHALO_TAG);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        return sunHalo;
    }
}
