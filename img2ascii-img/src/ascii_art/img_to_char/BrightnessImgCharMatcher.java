package ascii_art.img_to_char;

import image.Image;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

/**
 * this class is in charge of matching between a picture and chars according to its brightness value.
 * it calculates each one brightness value (number between 0-1) and finds the closest match.
 */
public class BrightnessImgCharMatcher {

    private static final int PIXELS_RES = 16;
    private static final double GREEN_RATIO = 0.7152;
    private static final double RED_RATIO = 0.2126;
    private static final double BLUE_RATIO = 0.0722;
    private static final double MAX_GREYSCALE = 255;
    private static final String EMPTY_CHAR_SET_ERROR = "Char set is empty therefore no ascii" +
                                                       " image can be created";
    private static final HashMap<Character, Double> charsToBrightness = new HashMap<>();
    private static final HashMap<Double, Character> normalBrightnessToChars = new HashMap<>();
    private final String font;
    private final Image image;
    private int prevNumCharsInRow;
    private ArrayList<Color[][]> prevSubImages;
    private Double[] normalBrightnessArr;

    // constructor
    public BrightnessImgCharMatcher(Image image, String font){
        this.font = font;
        this.image = image;
    }

    /**
     * takes an array of chars and calculates the brightness of each one of them.
     * updates value of HashMap that contains chars as keys and brightness as value.
     * @param chars array of chars that are used to create image.
     */
    private void calcCharsBrightness(Character[] chars){
        double maxBrightness = 0, minBrightness = 1;
        double cBrightness;
        for(char c : chars){
            if(charsToBrightness.containsKey(c)){
                cBrightness = charsToBrightness.get(c);
            }
            else {
                cBrightness = calcSingleCharBrightness(c);
                charsToBrightness.put(c, cBrightness);
            }
            if(cBrightness > maxBrightness){
                maxBrightness = cBrightness;
            }
            if(cBrightness < minBrightness){
                minBrightness = cBrightness;
            }
        }
        double normalBrightness;
        normalBrightnessArr = new Double[chars.length];
        for(int i = 0 ; i < chars.length ; i++){
            normalBrightness = (charsToBrightness.get(chars[i]) - minBrightness) /
                                   (maxBrightness - minBrightness);
            normalBrightnessArr[i] = normalBrightness;
            normalBrightnessToChars.put(normalBrightness, chars[i]);
        }
        Arrays.sort(normalBrightnessArr); // sort to be able to binary search in it.
    }

    /**
     * calculates a brightness value for a single char.
     * @param c a char to calculate it brightness value.
     * @return a double between 0 and 1 that represents the brightness value.
     */
    private double calcSingleCharBrightness(char c){
        boolean[][] boolArr = CharRenderer.getImg(c, PIXELS_RES, this.font);
        int trueCounter = 0, totalCounter = 0;
        for (boolean[] booleans : boolArr) {
            for (boolean bool : booleans) {
                totalCounter++;
                if (bool) {
                    trueCounter++;
                }
            }
        }
        return (double) trueCounter / totalCounter;
    }

    /**
     * calculates the gives image's brightness value by the ratio between the number bright
     * pixels to total number of pixels.
     * @param subImage image to calculate its brightness value.
     * @return the brightness value - a number between 0 and 1.
     */
    private double calcSubImageBrightness(Color[][] subImage){
        double sumGreyValue = 0;
        int pixelCounter = 0;
        for(Color[] row : subImage){
            for(Color color : row){
                pixelCounter++;
                sumGreyValue += color.getBlue() * BLUE_RATIO + color.getGreen() * GREEN_RATIO +
                                color.getRed() * RED_RATIO;
            }
        }
        return (sumGreyValue / pixelCounter) / MAX_GREYSCALE;
    }

    /**
     * finds  char from the given char array with the closest brightness value to the given
     * picture brightness value.
     * @param subImage the image to find a matching char to
     * @return the char that matches the images brightness value.
     */
    private char getClosestBrightnessValueChar(Color[][] subImage){
        double subImageBrightness = calcSubImageBrightness(subImage);
        return normalBrightnessToChars.get(binarySearchBrightnessValue(subImageBrightness));
    }

    /**
     * preforms a binary search in array of all normal chars values
     * @return the closest normal value in normalBrightnessArr to the given value.
     */
    private double binarySearchBrightnessValue(double imgBrightness){
        if(imgBrightness < normalBrightnessArr[0]) {
            return normalBrightnessArr[0];
        }
        if(imgBrightness > normalBrightnessArr[normalBrightnessArr.length-1]) {
            return normalBrightnessArr[normalBrightnessArr.length-1];
        }
        int lo = 0;
        int hi = normalBrightnessArr.length - 1;
        while (lo <= hi) {
            int mid = (hi + lo) / 2;
            if (imgBrightness < normalBrightnessArr[mid]) {
                hi = mid - 1;
            } else if (imgBrightness > normalBrightnessArr[mid]) {
                lo = mid + 1;
            } else {
                return normalBrightnessArr[mid];
            }
        }
        return (normalBrightnessArr[lo] - imgBrightness) < (imgBrightness - normalBrightnessArr[hi]) ?
                normalBrightnessArr[lo] : normalBrightnessArr[hi];
    }

    /**
     * the main function - generates an ascii art that matches the given image using
     * the chars in given array.
     * @param numCharsInRow the number of chars that can be in a row - the higher it will be the
     *                     higher the ascii image resolution will be.
     * @param charSet the set of chars that can be used in ascii image.
     * @return the ascii image that was generates (in 2d array).
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        if(charSet.length == 0){
            System.out.println(EMPTY_CHAR_SET_ERROR);
            return null;
        }
        int subImageSize = image.getWidth() / numCharsInRow;
        normalBrightnessToChars.clear(); // reset normal brightness values.
        calcCharsBrightness(charSet);
        char[][] asciiImage = new char[image.getHeight() / subImageSize][image.getWidth() / subImageSize];
        ArrayList<Color[][]> subImages;
        if(numCharsInRow == prevNumCharsInRow){ // if even - the subImages should be the same.
            subImages = prevSubImages;
        }
        else{
            subImages = image.divideImage(subImageSize, subImageSize);
        }
        for(int i = 0 ; i < subImages.size() ; i++){
            asciiImage[i/numCharsInRow][i%numCharsInRow] =
                    getClosestBrightnessValueChar(subImages.get(i));
        }
        prevNumCharsInRow = numCharsInRow;
        prevSubImages = subImages;
        return asciiImage;
    }
}
