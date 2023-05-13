package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;
import pepse.world.trees.Trunk;

import javax.sound.sampled.*;
import java.io.File;
import java.awt.*;
import java.io.IOException;

/**
 * the game manager - in charge of running the game, and enforce its rules.
 */
public class PepseGameManager extends GameManager {

    private static final Color SunHaloColor = new Color(255, 255, 0, 20);
    private static final float SUN_CYCLE = 30;
    private static final int SUN_HALO_DEFAULT_LAYER = Layer.BACKGROUND + 10;
    private static final float AVATAR_LOCATION_FACTOR = 0.5f;
    private static final int TIME_BETWEEN_ENEMIES = 10;
    private static final int REMOVE_WORLD_FACTOR = 5;
    private static final int TERRAIN_SEED = 5;
    private static final String MUSIC_FILE_DIR = "assets/music/216-music-33.wav";
    private static final float FROG_Y_LOCATION = 20;
    private static final String FROGMAN_IMAGE_DIR = "assets/enemie/frogman_idle.png";
    private static final int TARGET_FRAME_RATE = 80;
    private ImageReader imageReader;
    private Avatar avatar;
    private ImageRenderable frogImage;
    private Terrain terrain;
    private final int buffer = 850;
    private float time = 0;
    private int prevCreatedTime = 0;
    private int startWorld;
    private int endWorld;
    private Tree trees;
    private Vector2 windowDim;
    private UserInputListener inputListener;

    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /**
     * initialize game - creates all game objects and place them in game.
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from
     *                         disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param windowController Contains an array of helpful, self-explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.imageReader = imageReader;
        playMusic();
        this.inputListener = inputListener;
        windowController.setTargetFramerate(TARGET_FRAME_RATE);
        windowDim = windowController.getWindowDimensions();
        createGameObjects();
        startWorld = -Block.SIZE * 2 - buffer;
        endWorld = (int) windowController.getWindowDimensions().x() + Block.SIZE + buffer;
        terrain.createInRange(startWorld, endWorld);
        trees.createInRange(startWorld, endWorld);
        gameObjects().layers().shouldLayersCollide(Layer.BACKGROUND + 3,
                Layer.DEFAULT, true);
        this.setCamera(new Camera(avatar, Vector2.ZERO, windowDim, windowDim));
        frogImage = imageReader.readImage(FROGMAN_IMAGE_DIR, true);
    }

    /**
     * creates all game objects for the game.
     */
    private void createGameObjects() {
        Sky.create(gameObjects(), windowDim, Layer.BACKGROUND);
        terrain = new Terrain(gameObjects(), Layer.DEFAULT, windowDim, TERRAIN_SEED);
        Night.create(gameObjects(), Layer.FOREGROUND, windowDim, SUN_CYCLE);
        GameObject sun = Sun.create(gameObjects(), Layer.BACKGROUND + 1, windowDim, SUN_CYCLE);
        SunHalo.create(gameObjects(), SUN_HALO_DEFAULT_LAYER, sun, SunHaloColor);
        trees = new Tree(gameObjects(), terrain);
        Vector2 avatarInitialLocation = windowDim.mult(AVATAR_LOCATION_FACTOR);
        avatar = Avatar.create_extended(gameObjects(), Layer.DEFAULT, avatarInitialLocation,
                inputListener, imageReader, windowDim, terrain::groundHeightAt);
    }

    /**
     * in charge of updating the world background - delete the unseen objects and
     * add new to create an infinite world.
     *
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        time += deltaTime;
        extendWorld();
        removeWorld();
        if ((int) time % TIME_BETWEEN_ENEMIES == 0 && prevCreatedTime != (int) time) {
            createSingleFrog();
            prevCreatedTime = (int) time;
        }
    }

    /**
     * extends the world - creates new game objects to make world infinite.
     */
    private void extendWorld() {
        int middleWorld;
        if (startWorld > avatar.getCenter().x() - buffer) {
            middleWorld = startWorld;
            startWorld -= buffer;
            endWorld -= buffer;
            terrain.createInRange(startWorld, middleWorld);
            trees.createInRange(startWorld, middleWorld);
        } else if (endWorld < avatar.getCenter().x() + buffer) {
            middleWorld = endWorld;
            endWorld += buffer;
            startWorld += buffer;
            terrain.createInRange(middleWorld, endWorld);
            trees.createInRange(middleWorld, endWorld);
        }
    }

    /**
     * removes the unseen objects to make game memory efficient.
     */
    private void removeWorld() {
        String tag;
        int layer;
        for (GameObject ob : gameObjects()) {
            if (ob.getCenter().x() < startWorld - REMOVE_WORLD_FACTOR * Block.SIZE ||
                    ob.getCenter().x() > endWorld + REMOVE_WORLD_FACTOR * Block.SIZE) {
                tag = ob.getTag();
                if (!ob.getTag().equals(Energy.ENERGY_TAG)) {
                    layer = Layer.DEFAULT;
                    switch (tag) {
                        case Block.BLOCK_TAG:
                            layer += 1;
                            break;
                        case Leaf.TOP_LEAF_TAG:
                            layer = Leaf.DEAFULT_LAYER;
                            break;
                        case Leaf.LEAF_TAG:
                            layer = Leaf.DEAFULT_LAYER;
                            break;
                        case Trunk.TRUNK_TAG:
                            layer = Trunk.DEAFULT_LAYER;
                    }
                    gameObjects().removeGameObject(ob, layer);
                }
            }
        }
    }

    /**
     * creates a single frog and adds it to the game.
     */
    private void createSingleFrog() {
        Vector2 frogLocation = new Vector2(avatar.getCenter().x(), FROG_Y_LOCATION);
        Frogman frogman = new Frogman(frogLocation, Frogman.FROGMAN_SIZE, frogImage, gameObjects(),
                imageReader, terrain::groundHeightAt);
        gameObjects().addGameObject(frogman);
    }

    /**
     * plays the background music.
     */
    private void playMusic() {
        File file = new File(MUSIC_FILE_DIR);
        AudioInputStream stream;
        try {
            stream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
        Clip clip;
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        try {
            clip.open(stream);
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
        clip.loop(-1);
    }
}
