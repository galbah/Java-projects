package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final Color[][] pixelArray;
    private int width;
    private int height;

    /**
     * constructor - receives an image and transform it to a 2d array of type Color, makes sure
     * width and height are powers of 2 - rounds dimension upwards and sets new pixels as white.
     * @param filename file name of an image.
     * @throws IOException if image file name is not legal.
     */
    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        //im.getRGB(x, y)); getter for access to a specific RGB rates

        this.width = getClosestPowerOfTwo(origWidth);
        this.height = getClosestPowerOfTwo(origHeight);

        pixelArray = new Color[this.height][this.width];
        int diffWidth = (this.width - origWidth) / 2;
        int diffHeight = (this.height - origHeight) / 2;

        for(int i = 0 ; i < this.height ; i++){ // filling white pixels
            for (int j = 0; j < this.width; j++) {
                if(i < diffHeight || i >= this.height-diffHeight || j < diffWidth ||
                        j >= this.width-diffWidth){
                    pixelArray[i][j] = DEFAULT_COLOR;
                }
                else{
                    pixelArray[i][j] = new Color(im.getRGB(j-diffWidth,i-diffHeight));
                }
            }
        }
    }

    /**
     * @param num a positive integer
     * @return the closest higher power of 2 to the given number
     */
    private int getClosestPowerOfTwo(int num){
        double curPower = 0;
        for (int i = 0; i < num ; i++) {
            curPower=Math.pow(2,i);
            if(curPower >= num){
                return (int) curPower;
            }
        }
        return -1; // never gets here because 2^(num-1) >= num for every positive number
    }

    /**
     * divides the original picture to subImages of size divideWidth x divideHeight.
     * @param divideWidth amount of pixels in width of divided pictures
     * @param divideHeight amount of pixels in height of divided pictures
     * @return an ArrayList that contains all subImages.
     */
    public ArrayList<Color[][]> divideImage(int divideWidth, int divideHeight){
        ArrayList<Color[][]> subImages = new ArrayList<>();
        for (int i = 0; i < this.height / divideHeight; i++) {
            for (int j = 0; j < this.width / divideWidth; j++) {
                subImages.add(getSubMatrix(i*divideHeight, j*divideWidth, divideHeight, divideWidth));
            }
        }
        return subImages;
    }

    /**
     * @param rowCoor the row coordinate of the top left pixel of the wanted subImage.
     * @param colCoor the col coordinate of the top left pixel of the wanted subImage.
     * @param divideHeight amount of pixels in height of divided pictures
     * @param divideWidth amount of pixels in width of divided pictures
     * @return the subImage - an 2d array of type Color.
     */
    private Color[][] getSubMatrix(int rowCoor, int colCoor, int divideHeight, int divideWidth) {
        Color[][] subMatrix = new Color[divideHeight][divideWidth];
        for (int i = 0; i < divideHeight; i++) {
            for (int j = 0; j < divideWidth; j++) {
                subMatrix[i][j] = pixelArray[rowCoor + i][colCoor + j];
            }
        }
        return subMatrix;
    }

    @Override
    public int getWidth() { // width of picture.
        return this.width;
    }

    @Override
    public int getHeight() { // height of picture.
        return this.height;
    }

    @Override
    public Color getPixel(int x, int y) { // returns a specific pixel color.
        return pixelArray[x][y];
    }

}
