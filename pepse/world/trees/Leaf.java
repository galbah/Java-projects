package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.util.Random;

/**
 * this class represents a leaf in the tree.
 */
public class Leaf extends GameObject {

    public static final String LEAF_TAG = "leaf";
    public static final String TOP_LEAF_TAG = "top_leaf";
    public static final int SIZE = 30;
    public static final int DEAFULT_LAYER = Layer.BACKGROUND + 3;
    private static final int MAX_LIFE_TIME = 80;
    private static final int MAX_DEATH_TIME = 5;
    private static final float FADEOUT_TIME = 10;
    private static final float IN_TREE_TRANSITION_TIME = 2;
    private static final Float FINAL_ANGLE_VAL = 15f;
    private static final float FALL_TRANSITION_TIME = 5;
    private static final float FALL_VELOCITY = 30;
    Random random = new Random();
    private final Vector2 originalVec;
    private Transition horizontalTransition;

    /**
     * Construct a new Leaf instance, sets physics to match the leaf features
     * and runs the leaf cycle if leaf is not in the top of the tree.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     * @param isTop         True if leaf is fart of the tow row in tree, False otherwise.
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Boolean isTop) {
        super(topLeftCorner, dimensions, renderable);
        originalVec = this.getCenter();
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        if (!isTop)
            leafCycle();
    }

    /**
     * sets the transitions of a leaf - makes the leaf look like its moving in the wind.
     */
    public void setTransitions() {
        new Transition<>(this, this.renderer()::setRenderableAngle, 0f, FINAL_ANGLE_VAL,
                Transition.LINEAR_INTERPOLATOR_FLOAT, IN_TREE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        new Transition<>(this, this::setDimensions, this.getDimensions(),
                new Vector2(this.getDimensions().x() - 1, this.getDimensions().y()),
                Transition.LINEAR_INTERPOLATOR_VECTOR, IN_TREE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * the start of leaf cycle - randomly chooses time for the leaf to stay on the tree and then calls
     * the next method in the cycle.
     */
    private void leafCycle() {
        this.renderer().setOpaqueness(1);
        this.setCenter(originalVec);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.transform().setVelocity(Vector2.ZERO);
        float lifeTime = (float) random.nextInt(MAX_LIFE_TIME);
        new ScheduledTask(this, lifeTime, false, this::leafFall);
    }

    /**
     * second method in leaf cycle - makes the leaf fall while has small horizontal movements and fade out,
     * when leaf finishes its fall the method calls the next method in cycle.
     */
    private void leafFall() {
        physics().preventIntersectionsFromDirection(null);
        horizontalTransition = new Transition<>(this, this.transform()::setVelocityX, -10f, 10f,
                Transition.LINEAR_INTERPOLATOR_FLOAT, FALL_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        this.transform().setVelocityY(FALL_VELOCITY);
        this.renderer().fadeOut(FADEOUT_TIME, this::leafDeath);
    }

    /**
     * the last stage in leaf cycle - randomly chooses time for the leaf to stay dead on ground,
     * after that method calls the first method in cycle to run it again.
     */
    private void leafDeath() {
        int deathTime = this.random.nextInt(MAX_DEATH_TIME);
        new ScheduledTask(this, deathTime, false, this::leafCycle);
        this.removeComponent(horizontalTransition);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(Block.TOP_BLOCK_TAG)) {
            this.removeComponent(horizontalTransition);
            this.transform().setVelocity(Vector2.ZERO);
        }
    }

    /**
     * makes sure that when the leaf hits the ground or another object it stops the horizontal transition.
     *
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
        if (this.getVelocity().y() == 0) {
            this.transform().setVelocityX(0);
        }
    }
}
