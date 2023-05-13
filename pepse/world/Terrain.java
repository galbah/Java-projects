package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static java.lang.Math.abs;

/**
 * this class responsible to generate the terrain
 */
public class Terrain {

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 15;
    private static final float GROUND_HEIGHT_RATIO = 2 / 3f;
    private static final float NOISE_GEN_FACTOR = 35;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final float groundHeightAtX0;
    private final Vector2 windowDimensions;
    private final NoiseGenerator noisegen;

    /**
     * @param gameObjects      the game objects
     * @param groundLayer      the ground layer
     * @param windowDimensions the game dimensions
     * @param seed             a seed to the terrain random
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = (float) GROUND_HEIGHT_RATIO * windowDimensions.y();
        this.windowDimensions = windowDimensions;
        noisegen = new NoiseGenerator(seed);
    }

    /**
     * @param x the x in which the terrain will be build
     * @return y in which the terrain will be build
     */
    public float groundHeightAt(float x) {
        return groundHeightAtX0 + (float) abs(noisegen.noise(x / NOISE_GEN_FACTOR)) * groundHeightAtX0;

    }

    /**
     * this function build terrain in specific range
     *
     * @param minX x to start
     * @param maxX x to end
     */
    public void createInRange(int minX, int maxX) {
        float ground_level;
        minX = (int) (Math.floor(minX / Block.SIZE) * Block.SIZE);
        maxX = (int) (Math.ceil((float) maxX / Block.SIZE) * Block.SIZE);
        float maxY = (float) (Math.ceil(windowDimensions.y() / Block.SIZE) * Block.SIZE);
        for (int i = minX; i <= maxX; i += Block.SIZE) {
            ground_level = (float) (Math.floor(groundHeightAt(i) / Block.SIZE) * Block.SIZE);
            for (float j = ground_level; j <= maxY + Block.SIZE * TERRAIN_DEPTH; j += Block.SIZE) {
                GameObject block = new Block(new Vector2(i, j),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                if (j == ground_level || j == ground_level + Block.SIZE || j == ground_level
                        + 2*Block.SIZE) {
                    gameObjects.addGameObject(block, groundLayer);
                    block.setTag(Block.TOP_BLOCK_TAG);

                } else {
                    gameObjects.addGameObject(block, Layer.DEFAULT + 1);
                    block.setTag(Block.BLOCK_TAG);

                }
            }
        }

    }
}
