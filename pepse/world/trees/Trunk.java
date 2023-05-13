package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * this class represents the trunk of a tree.
 */
public class Trunk extends GameObject {
    public static final int SIZE = 30;
    public static final String TRUNK_TAG = "trunk";
    public static final int DEAFULT_LAYER = Layer.BACKGROUND + 2;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Trunk(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
//        physics().preventIntersectionsFromDirection(Vector2.ZERO);
//        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}
