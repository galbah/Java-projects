package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * this class represents a sun object in the game. the sun is moving in an ellipsis movement
 * in an infinite cycle around the screen.
 */
public class Sun extends GameObject {

    private static final float X_ELLIPSE = 1.1F;
    private static final float Y_ELLIPSE = 0.9F;
    private static final float SUN_FACTOR = 1.7F;
    private static final String SUN_TAG = "sun";
    private static final Vector2 SUN_SIZE = new Vector2(200, 200);
    private static final float SUN_START_FACTOR = 1.5f;
    private static final float SUN_END_FACTOR = 3.5f;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Sun(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }

    /**
     * creates a sun object, adds it to the game object collection.
     *
     * @param gameObjects      the collection to add sun to.
     * @param layer            the layer that the sun should be created in.
     * @param windowDimensions the dimensions of the game window.
     * @param cycleLength      the time of the cycle that the sun goes through.
     * @return the sun object that was created.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer,
                                    Vector2 windowDimensions, float cycleLength) {
        final float radius = windowDimensions.y() / 2;
        final float centerX = windowDimensions.x() / 2;
        GameObject sun = new GameObject(Vector2.ZERO, SUN_SIZE,
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN_TAG);
        new Transition<>(sun,
                angle -> sun.setCenter(new Vector2(((float) Math.cos(angle) * radius + centerX) * X_ELLIPSE,
                        ((float) Math.sin(angle) * radius + radius) * Y_ELLIPSE))
                , (float) Math.PI * SUN_START_FACTOR, (float)  Math.PI * SUN_END_FACTOR,
                Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);
        return sun;
    }
}
