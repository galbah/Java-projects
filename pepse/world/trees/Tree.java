package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

/**
 * this class that represents a tree in the game.
 * a tree is built with a trunk and a group of leaves around it top.
 */
public class Tree {

    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final int TRUNK_MAX_DIST_FROM_TOP = 100;
    private static final int MIN_TRUNK_SIZE = 8;
    private static final int MIN_LEAVES_RADIUS = 3;
    private static final int CREATE_TREE_RANDOM_BOUND = 15;
    private static final int SET_LEAF_TRANSITION_RANDOM_BOUND = 10;
    private final Random random = new Random();
    private final GameObjectCollection gameObjects;
    private final Terrain terrain;
    private final HashMap<Integer, Integer> treeMap = new HashMap<>();
    private int totalminX;
    private int totalmaxX;

    public Tree(GameObjectCollection gameObjects, Terrain terrain) {
        this.gameObjects = gameObjects;
        this.terrain = terrain;
    }

    /**
     * creates trees in random places between the 2 given x indexes (at least a block between 2 trees).
     *
     * @param minX the minimum x that trees will be created in
     * @param maxX the maximum x that trees will be created in
     */
    public void createInRange(int minX, int maxX) {
        minX = (int) (Math.floor(minX / Block.SIZE) * Block.SIZE);
        maxX = (int) (Math.ceil((float) maxX / Block.SIZE) * Block.SIZE);
        for (int i = minX; i <= maxX; i += Block.SIZE) {
            if (i >= totalminX && i <= totalmaxX) {
                if (treeMap.containsKey(i)) {
                    plantTree(i);
                    i += Block.SIZE;
                }
            } else if (random.nextInt(CREATE_TREE_RANDOM_BOUND) == 0) {
                plantTree(i);
                i += Block.SIZE;
            }
        }
        if (minX < totalminX) {
            totalminX = minX;
        }
        if (maxX > totalmaxX) {
            totalmaxX = maxX;
        }
    }

    /**
     * creates a single tree - a trunk in a random size and leaves in a random radius
     *
     * @param x the index that the trunk of the tree will be created
     */
    private void plantTree(int x) {
        int groundLayer = (int) (Math.floor(terrain.groundHeightAt(x) / Block.SIZE) * Block.SIZE);
        int trunkSize = createTrunk(x, groundLayer);
        int topTree = (groundLayer / Block.SIZE - trunkSize) * Block.SIZE;
        int radius = Math.max(random.nextInt(trunkSize / 2), MIN_LEAVES_RADIUS);
        createLeaves(x, radius, topTree);
    }

    /**
     * creates the trunk of a tree.
     *
     * @param x           the x index that the trunk will be built in the world.
     * @param groundLayer the height of the ground in x.
     * @return the size of the trunk that is randomly chosen.
     */
    private int createTrunk(int x, int groundLayer) {
        int trunkSize;
        if (treeMap.containsKey(x)) {
            trunkSize = treeMap.get(x);
        } else {
            trunkSize = random.nextInt(((int) terrain.groundHeightAt(x) - TRUNK_MAX_DIST_FROM_TOP)
                    / Trunk.SIZE - MIN_TRUNK_SIZE) + MIN_TRUNK_SIZE;
            treeMap.put(x, trunkSize);
        }
        int topTree = (groundLayer / Trunk.SIZE - trunkSize) * Trunk.SIZE;
        for (int i = groundLayer - Trunk.SIZE; i >= topTree;
             i -= Trunk.SIZE) {
            Trunk trunk = new Trunk(new Vector2(x, i), new Vector2(Trunk.SIZE, Trunk.SIZE),
                    new RectangleRenderable(TRUNK_COLOR));
            gameObjects.addGameObject(trunk, Trunk.DEAFULT_LAYER);
            trunk.setTag(Trunk.TRUNK_TAG);
        }
        return trunkSize;
    }

    /**
     * creates the leaves of the tree and adds it to object.
     *
     * @param x       the x index of that the trunk of the tree was created.
     * @param radius  the radius of the leaves - randomly chosen.
     * @param topTree the height of the top of the trunk.
     */
    private void createLeaves(int x, int radius, int topTree) {
        int leaf_x = x - (radius - 1) * Leaf.SIZE;
        int leaf_y = topTree - (radius - 1) * Leaf.SIZE;
        Leaf leaf;
        for (int i = 0; i < 2 * radius - 1; i++) {
            for (int j = 0; j < 2 * radius - 1; j++) {
                if (j == 0) {
                    leaf = new Leaf(new Vector2(leaf_x + i * Leaf.SIZE, leaf_y),
                            new Vector2(Leaf.SIZE, Leaf.SIZE), new RectangleRenderable(LEAF_COLOR), true);
                    leaf.setTag(Leaf.TOP_LEAF_TAG);
                } else {
                    leaf = new Leaf(new Vector2(leaf_x + i * Leaf.SIZE, leaf_y + j * Leaf.SIZE),
                            new Vector2(Leaf.SIZE, Leaf.SIZE), new RectangleRenderable(LEAF_COLOR), false);
                    leaf.setTag(Leaf.LEAF_TAG);
                }
                gameObjects.addGameObject(leaf, Leaf.DEAFULT_LAYER);
                new ScheduledTask(leaf, (float) random.nextInt(SET_LEAF_TRANSITION_RANDOM_BOUND) / 2,
                        false, leaf::setTransitions);
            }
        }
    }
}