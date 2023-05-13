package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.trees.Leaf;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.function.UnaryOperator;

public class Avatar extends GameObject {

    private static final float VELOCITY_X = 300;
    private static final float VELOCITY_Y = -400;
    private static final float JUMP_VEL = -300;
    private static final float GRAVITY = 450;
    private static final double IDLE_ANIMATION_INTERVAL = 1;
    private static final double RUN_ANIMATION_INTERVAL = 0.25;
    private static final double FLIGHT_ANIMATION_INTERVAL = 0.15;
    private static final double JUMP_ANIMATION_INTERVAL = 0.25;
    private static final double ATTACK_ANIMATION_INTERVAL = 0.15;
    private static final float AVATAR_SIZE = 60;
    public static final String AVATAR_TAG = "avatar";
    private static final float ACTION_DELAY = 0.5f;
    private static final float HURT_ANIMATION_TIME = 0.5f;
    private static final float ATTACKING_TIME = 1.3f;
    private static final Float HURT_OPAQUENESS = 0.4f;
    private static AnimationRenderable avatarIdleAnimation;
    private static AnimationRenderable avatarRunAnimation;
    private static AnimationRenderable avatarFlightAnimation;
    private static AnimationRenderable avatarJumpAnimation;
    private static AnimationRenderable avatarAttackAnimation;
    private static UserInputListener inputListener;
    private static ImageRenderable peaceImage;
    private static GameObject hurtAnimation;
    private static Transition<Float> hurtTransition;
    private final UnaryOperator<Float> groundHeight;
    private boolean isFlying = false;
    private boolean isJumping = false;
    private static Energy energy;
    private boolean inAction;
    private boolean isAttacking = false;
    private boolean isHurt = false;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     * @param groundHeight  method reference to the method that receives an x and returns the
     *                      height of the ground at that x.
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UnaryOperator<Float> groundHeight) {
        super(topLeftCorner, dimensions, renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.groundHeight = groundHeight;

    }

    /**
     * creates an avatar object, adds it in to game object collection and returns it.
     *
     * @param gameObjects      collection of game objects.
     * @param layer            layer that the avatar should be added to.
     * @param topLeftCorner    top left corner of the location of the avatar.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {
        return null;
    }

    /**
     * creates an avatar object, adds it in to game object collection and returns it.
     *
     * @param gameObjects      collection of game objects.
     * @param layer            layer that the avatar should be added to.
     * @param topLeftCorner    top left corner of the location of the avatar.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param windowDimensions dimensions of the screen.
     * @param groundHeight     method reference to the method that receives an x and returns the
     *                         height of the ground at that x.
     * @return                 The avatar object that was created.
     */
    public static Avatar create_extended(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader,
                                Vector2 windowDimensions, UnaryOperator<Float> groundHeight) {
        Avatar.inputListener = inputListener;
        addAvatarAnimation(imageReader, windowDimensions, gameObjects);
        Avatar avatar = new Avatar(topLeftCorner, new Vector2(AVATAR_SIZE, AVATAR_SIZE),
                avatarIdleAnimation, groundHeight);
        energy = new Energy(Vector2.ZERO, Energy.SIZE);
        gameObjects.addGameObject(energy);
        gameObjects.addGameObject(avatar, layer);
        avatar.setTag(AVATAR_TAG);
        energy.setTag(Energy.ENERGY_TAG);
        return avatar;
    }

    /**
     * sets the animations of the avatar - saves them as class member.
     *
     * @param imageReader - to read images into animation.
     */
    private static void addAvatarAnimation(ImageReader imageReader, Vector2 windowDimensions,
                                           GameObjectCollection gameObjects) {
        setIdleAnimation(imageReader);
        setRunAnimation(imageReader);
        setFlightAnimation(imageReader);
        setJumpAnimation(imageReader);
        setAttackAnimation(imageReader);
        peaceImage = imageReader.readImage("assets/peace.png", true);
        hurtAnimation = new GameObject(Vector2.ZERO, windowDimensions, new RectangleRenderable(Color.RED));
        hurtAnimation.renderer().setOpaqueness(0);
        hurtAnimation.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(hurtAnimation, Layer.FOREGROUND); // a red animation to when
                                                                    // avatar gets hurt
    }

    /**
     * saves the jump animation of the avatar as class member.
     *
     * @param imageReader - to read images into animation.
     */
    private static void setJumpAnimation(ImageReader imageReader) {
        Renderable[] avatarJump1 = {
                imageReader.readImage("assets/jump_0.png", true),
                imageReader.readImage("assets/jump_1.png", true),
                imageReader.readImage("assets/jump_2.png", true),
                imageReader.readImage("assets/jump_3.png", true)};
        avatarJumpAnimation = new AnimationRenderable(avatarJump1, JUMP_ANIMATION_INTERVAL);
    }

    /**
     * saves the attack animation of the avatar as class member.
     *
     * @param imageReader - to read images into animation.
     */
    private static void setAttackAnimation(ImageReader imageReader) {
        Renderable[] avatarJump1 = {
                imageReader.readImage("assets/attack_0.png", true),
                imageReader.readImage("assets/attack_1.png", true),
                imageReader.readImage("assets/attack_2.png", true),
                imageReader.readImage("assets/attack_3.png", true),
                imageReader.readImage("assets/attack_4.png", true),
                imageReader.readImage("assets/attack_5.png", true),
                imageReader.readImage("assets/attack_6.png", true),
                imageReader.readImage("assets/attack_7.png", true)};
        avatarAttackAnimation = new AnimationRenderable(avatarJump1, ATTACK_ANIMATION_INTERVAL);
    }

    /**
     * saves the flight animation of the avatar as class member.
     *
     * @param imageReader - to read images into animation.
     */
    private static void setFlightAnimation(ImageReader imageReader) {
        Renderable[] avatarFlight = {
                imageReader.readImage("assets/swim_0.png", true),
                imageReader.readImage("assets/swim_1.png", true),
                imageReader.readImage("assets/swim_2.png", true),
                imageReader.readImage("assets/swim_3.png", true),
                imageReader.readImage("assets/swim_4.png", true),
                imageReader.readImage("assets/swim_5.png", true)};
        avatarFlightAnimation = new AnimationRenderable(avatarFlight, FLIGHT_ANIMATION_INTERVAL);
    }

    /**
     * saves the run animation of the avatar as class member.
     *
     * @param imageReader - to read images into animation.
     */
    private static void setRunAnimation(ImageReader imageReader) {
        Renderable[] avatarRun = {
                imageReader.readImage("assets/run_0.png", true),
                imageReader.readImage("assets/run_1.png", true),
                imageReader.readImage("assets/run_2.png", true),
                imageReader.readImage("assets/run_3.png", true),
                imageReader.readImage("assets/run_4.png", true),
                imageReader.readImage("assets/run_5.png", true)};
        avatarRunAnimation = new AnimationRenderable(avatarRun, RUN_ANIMATION_INTERVAL);
    }

    /**
     * saves the idle animation of the avatar as class member.
     *
     * @param imageReader - to read images into animation.
     */
    private static void setIdleAnimation(ImageReader imageReader) {
        Renderable[] avatarIdle = {
                imageReader.readImage("assets/idle_0.png", true),
                imageReader.readImage("assets/idle_1.png", true),
                imageReader.readImage("assets/idle_2.png", true),
                imageReader.readImage("assets/idle_3.png", true)};
        avatarIdleAnimation = new AnimationRenderable(avatarIdle, IDLE_ANIMATION_INTERVAL);
    }

    /**
     * in charge of updating avatar movement and actions according to user input.
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
        if(getCenter().y() - AVATAR_SIZE > groundHeight.apply(getCenter().x())) {
            setCenter(new Vector2(getCenter().x(), groundHeight.apply(getCenter().x()) - AVATAR_SIZE));
            setVelocity(Vector2.ZERO);
        }
        if (energy.getEnergy() == 0) {
            transform().setVelocityY(GRAVITY);
        }
        if (transform().getVelocity().equals(Vector2.ZERO) && !inAction) {
            renderer().setRenderable(avatarIdleAnimation);
        }
        if (!isHurt) {
            run();
            jump();
            fly();
            actions();
        }
        if (getVelocity().y() == 0) {
            energy.increase();
        }
    }

    /**
     * handles avatar static actions s.a: attacking(ctrl), doing peace with hand(Z) ect..
     */
    private void actions() {
        if (inputListener.isKeyPressed(KeyEvent.VK_CONTROL)) {
            renderer().setRenderable(avatarAttackAnimation);
            inAction = true;
            isAttacking = true;
            new ScheduledTask(this, ATTACKING_TIME, false, this::setInActionFalse);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_Z)) {
            renderer().setRenderable(peaceImage);
            inAction = true;
            new ScheduledTask(this, ACTION_DELAY, false, this::setInActionFalse);
        }
        if (!inAction)
            avatarAttackAnimation.resetAnimation();
    }

    /**
     * updates avatar state - is not in action. needed because the use of ScheduledTask.
     */
    private void setInActionFalse() {
        inAction = false;
        isAttacking = false;
    }

    /**
     * handles avatar flying action.
     */
    private void fly() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                inputListener.isKeyPressed(KeyEvent.VK_SHIFT) &&
                energy.getEnergy() > 0) {
            energy.decrease();
            transform().setVelocityY(VELOCITY_Y);
            this.renderer().setRenderable(avatarFlightAnimation);
            isFlying = true;
        }
    }

    /**
     * handles avatar jump action.
     */
    private void jump() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && this.getVelocity().y() == 0) {
            transform().setVelocityY(JUMP_VEL);
            this.renderer().setRenderable(avatarJumpAnimation);
            isJumping = true;
        }
    }

    /**
     * handles avatar run action.
     */
    private void run() {
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) || inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            float xVel = 0;
            if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
                xVel = -VELOCITY_X;
                if (!this.renderer().isFlippedHorizontally())
                    this.renderer().setIsFlippedHorizontally(true);
            }
            if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
                xVel = VELOCITY_X;
                if (this.renderer().isFlippedHorizontally())
                    this.renderer().setIsFlippedHorizontally(false);
            }
            if (!isInAir() && !inAction)
                this.renderer().setRenderable(avatarRunAnimation);
            transform().setVelocityX(xVel);
        } else {
            transform().setVelocityX(0);
        }
    }

    /**
     * @return true if avatar is in air, false otherwise.
     */
    private boolean isInAir() {
        return isFlying || isJumping;
    }

    /**
     * handles collisions of avatar with other game objects - updates avatars reactions to these collisions
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(Block.TOP_BLOCK_TAG) || other.getTag().equals(Leaf.TOP_LEAF_TAG)) {
            energy.increase();
            isFlying = false;
            isJumping = false;
            transform().setVelocityY(0);
        }
        if (isAttacking && other instanceof Frogman) {
            new ScheduledTask(this, ACTION_DELAY, false, ((Frogman) other)::die);
        }
        if (!isAttacking && other instanceof Frogman && !isHurt && !((Frogman) other).isDead) {
            if (collision.getNormal().x() > 0) {
                transform().setVelocity(VELOCITY_X, -VELOCITY_X);
            } else {
                transform().setVelocity(-VELOCITY_X, -VELOCITY_X);
            }
            isHurt = true;
            hurtTransition = new Transition<>(hurtAnimation, hurtAnimation.renderer()::setOpaqueness,
                    0f, HURT_OPAQUENESS, Transition.CUBIC_INTERPOLATOR_FLOAT, HURT_ANIMATION_TIME,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    () -> new ScheduledTask(hurtAnimation, ACTION_DELAY, false,
                            () -> hurtAnimation.removeComponent(hurtTransition)));
            new ScheduledTask(hurtAnimation, 2 * ACTION_DELAY, false, () -> isHurt = false);
        }
    }

    /**
     * @return true if avatar is currently attacking, false otherwise.
     */
    public boolean isAttacking() {
        return isAttacking;
    }
}
