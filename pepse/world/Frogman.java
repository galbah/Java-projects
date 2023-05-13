package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;
import java.util.function.UnaryOperator;

/**
 * this class represents a frogman which is the evil character in the game.
 */
public class Frogman extends GameObject {

    private static final float VELOCITY_X = 150;
    private static final float VELOCITY_Y = -300;
    private static final double DIE_ANIMATION_INTERVAL = 0.5;
    private static final int GRAVITY = 400;
    public static final String FROGMAN_TAG = "frogman";
    private static final float FROGMAN_ACTIONS_DELIM = 2f;
    private static final int NUMBER_OF_ACTIONS = 3;
    private static final float MOVE_IN_AIR_WAIT = 0.3f;
    private static AnimationRenderable frogmanDieAnimation;
    private final Random random;
    private final GameObjectCollection gameObjects;
    private final UnaryOperator<Float> groundHeight;
    public boolean isDead;

    public static final Vector2 FROGMAN_SIZE = new Vector2(80, 80);


    /**
     * makes sure that frogman doesn't cross the terrain height.
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
        if (getCenter().y() > groundHeight.apply(getCenter().x())) {
            setCenter(new Vector2(getCenter().x(), groundHeight.apply(getCenter().x())
                    - FROGMAN_SIZE.y()));
            setVelocity(Vector2.ZERO);
        }

    }

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     * @param imageReader   Contains a single method: readImage, which reads an image from disk.
     *                      See its documentation for help.
     * @param groundHeight  method reference to the method that receives an x and returns the
     *                      height of the ground at that x.
     */
    public Frogman(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                   GameObjectCollection gameObjects, ImageReader imageReader,
                   UnaryOperator<Float> groundHeight) {
        super(topLeftCorner, dimensions, renderable);
        this.groundHeight = groundHeight;
        random = new Random();
        this.gameObjects = gameObjects;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.setTag(FROGMAN_TAG);
        setFrogmanAnimation(imageReader);
        frogmanCycle();
    }

    /**
     * saves the frogman animations to his actions as class member.
     *
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                    See its documentation for help.
     */
    private static void setFrogmanAnimation(ImageReader imageReader) {
        Renderable[] frogmanDie = {
                imageReader.readImage("pepse/assets/enemie/frogman_die0.png", true),
                imageReader.readImage("pepse/assets/enemie/frogman_die1.png", true),
                imageReader.readImage("pepse/assets/enemie/frogman_die2.png", true),
                imageReader.readImage("pepse/assets/enemie/frogman_die2.png", true)};
        frogmanDieAnimation = new AnimationRenderable(frogmanDie, DIE_ANIMATION_INTERVAL);
    }

    /**
     * the cycle of the frog actions - random movements
     */
    private void frogmanCycle() {
        if (isDead) { // dead is delayed to make it more realistic
            return;
        }
        int nextTask = random.nextInt(NUMBER_OF_ACTIONS);
        if (nextTask == 0)
            walkLeft();
        else if (nextTask == 1) {
            walkRight();
        } else {
            stand();
        }
    }

    /**
     * makes the frog walk to the right direction and jump.
     */
    private void walkRight() {
        if (isDead) {
            return;
        }
        transform().setVelocityX(VELOCITY_X);
        if (this.renderer().isFlippedHorizontally())
            this.renderer().setIsFlippedHorizontally(false);
        new ScheduledTask(this, FROGMAN_ACTIONS_DELIM, false, this::jump);
        new ScheduledTask(this, 2 * FROGMAN_ACTIONS_DELIM, false, this::frogmanCycle);
    }

    /**
     * makes the frog walk to the left direction and jump.
     */
    private void walkLeft() {
        if (isDead) {
            return;
        }
        transform().setVelocityX(-VELOCITY_X);
        if (!this.renderer().isFlippedHorizontally())
            this.renderer().setIsFlippedHorizontally(true);
        new ScheduledTask(this, FROGMAN_ACTIONS_DELIM, false, this::jump);
        new ScheduledTask(this, 2 * FROGMAN_ACTIONS_DELIM, false, this::frogmanCycle);
    }

    /**
     * makes the frog stand in his place for a short time.
     */
    private void stand() {
        transform().setVelocityX(0);
        new ScheduledTask(this, FROGMAN_ACTIONS_DELIM, false, this::frogmanCycle);
    }

    /**
     * makes the frogman jump to his movement direction.
     */
    private void jump() {
        if (isDead) {
            return;
        }
        if (this.renderer().isFlippedHorizontally()) {
            transform().setVelocityY(VELOCITY_Y);
            new ScheduledTask(this, MOVE_IN_AIR_WAIT, false,
                    () -> transform().setVelocityX(-VELOCITY_X)); // to overcome obstacles
        } else {
            transform().setVelocityY(VELOCITY_Y);
            new ScheduledTask(this, MOVE_IN_AIR_WAIT, false,
                    () -> transform().setVelocityX(VELOCITY_X)); // to overcome obstacles
        }
    }

    /**
     * makes the frogman dead and removes it from game.
     */
    public void die() {
        transform().setVelocity(Vector2.ZERO);
        renderer().setRenderable(frogmanDieAnimation);
        new ScheduledTask(this, FROGMAN_ACTIONS_DELIM, false,
                () -> gameObjects.removeGameObject(this));
    }

    /**
     * handles frogman collisions with other objects.
     * if collides with an attacking avatar - dies.
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other instanceof Avatar && ((Avatar) other).isAttacking()) {
            isDead = true;
        }
    }
}