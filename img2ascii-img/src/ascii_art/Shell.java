package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.HashSet;
import java.util.Scanner;

/**
 * a class that represents a shell in which a user can control the features of the ascii art
 * and also can render an image eventually.
 */
public class Shell {

    private static final String EXIT_WORD = "exit";
    private static final String FONT = "Courier New";
    private static final String HTML_FILENAME = "out.html";
    private static final String GENERAL_ERROR = "Did not executed due to incorrect command";
    private static final String RES_ERROR = "Did not change due to exceeding boundaries";
    private static final String ADD_ERROR = "Did not add due to incorrect format";
    private static final String REMOVE_ERROR = "Did not add due to incorrect format";
    private static final int FIRST_ASCII = 32;
    private static final int LAST_ASCII = 126;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private final HashSet<Character> chars;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private final BrightnessImgCharMatcher brightnessImgCharMatcher;
    private final ConsoleAsciiOutput consoleAsciiOutput = new ConsoleAsciiOutput();
    private final HtmlAsciiOutput htmlAsciiOutput = new HtmlAsciiOutput(HTML_FILENAME, FONT);
    private boolean printToConsole = false;

    /**
     * constructor - initializes char set to 0-9 numbers.
     * @param img the image to control via shell commands.
     */
    public Shell(Image img){
        chars = new HashSet<>();
        for(char i = '0' ; i <= '9' ; i++){
            chars.add(i);
        }
        minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        brightnessImgCharMatcher = new BrightnessImgCharMatcher(img, FONT);
    }

    /**
     * main function in class - runs the shell. while command is not "exit" function keep
     * receiving input from user describing actions that should be done. function makes sure
     * all actions are done and that input is always legal.
     * when command is "exit" - finishes its run.
     */
    public void run(){
        Scanner scanner = new Scanner(System.in);
        System.out.print(">>>");
        String userInput = scanner.nextLine();
        String[] stringArr;
        while(!userInput.equals(EXIT_WORD)){
            stringArr = userInput.split(" ");
            if(stringArr.length == 0){
                generalInputError();
                System.out.print(">>>");
                userInput = scanner.nextLine();
                continue;
            }
            switch (stringArr[0]) {
                case "chars":
                    printChars();
                    break;
                case "add":
                case "remove":
                    handleAddRemove(stringArr);
                    break;
                case "res":
                    handleRes(stringArr);
                    break;
                case "console":
                case "render":
                    handleConsoleRender(stringArr);
                    break;
                default:
                    generalInputError();
            }
            System.out.print(">>>");
            userInput = scanner.nextLine();
        }
    }

    /**
     * handles general input error - prints general error message.
     */
    private void generalInputError() {
        System.out.println(GENERAL_ERROR);
    }

    /**
     * changes the output state, default to be html output. when command is "console" -
     * changes the output state to be console output.
     * @param stringArr array of strings that were written to shell.
     */
    private void handleConsoleRender(String[] stringArr) {
        if(stringArr.length != 1 || chars.size() == 0){
            generalInputError();
        }
        else if(stringArr[0].equals("console")){
            printToConsole = true;
        }
        else if(stringArr[0].equals("render")){
            Character[] charArr = new Character[chars.size()];
            chars.toArray(charArr);
            char[][] asciiImage = brightnessImgCharMatcher.chooseChars(charsInRow, charArr);
            if(printToConsole){
                consoleAsciiOutput.output(asciiImage);
            }
            else{
                htmlAsciiOutput.output(asciiImage);
            }
        }
    }

    /**
     * increases / decreases charsInRow to make resolution higher / lower.
     * always makes sure that resolution is within legal borders - if not prints an error.
     * @param stringArr array of strings that were written to shell.
     */
    private void handleRes(String[] stringArr) {
        if(stringArr.length != 2){
            generalInputError();
        }
        if(stringArr[1].equals("up")){
            if(charsInRow * 2 <= maxCharsInRow){
                charsInRow *= 2;
                System.out.println("Width set to " + charsInRow);
            }
            else{
                System.out.println(RES_ERROR);
            }
        }
        else if(stringArr[1].equals("down")){
            if(charsInRow / 2 >= minCharsInRow){
                charsInRow /= 2;
                System.out.println("Width set to " + charsInRow);
            }
            else{
                System.out.println(RES_ERROR);
            }
        }
        else {
            generalInputError();
        }
    }

    /**
     * handle the add/remove command, if legal - adds relevant chars to data, if not - prints error.
     * @param stringArr array of the words that were written to shell.
     */
    private void handleAddRemove(String[] stringArr) {
        String command = stringArr[0];
        if(stringArr.length != 2){
            handleAddRemoveError(command);
        }
        else if(stringArr[1].equals("all")){
            for(char i = FIRST_ASCII ; i <= LAST_ASCII ; i++){
                addOrRemove(command, i);
            }
        }
        else if(stringArr[1].equals("space")){
            addOrRemove(command, ' ');
        }
        else if(stringArr[1].length() == 1){
            if(isValidChar(stringArr[1].charAt(0))){
                addOrRemove(command ,stringArr[1].charAt(0));
            }
            else{
                handleAddRemoveError(command);
            }
        }
        else if (stringArr[1].charAt(1) == '-'){
            handleRangeAddRemove(command, stringArr);
        }
        else{
            handleAddRemoveError(command);
        }
    }

    /**
     * adds to the char data all chars that are in the range between the 2 given chars.
     * @param command the add/remove command
     * @param stringArr an array of strings that were written to shell
     */
    private void handleRangeAddRemove(String command, String[] stringArr) {
        if (stringArr[1].length() != 3 || !isValidChar(stringArr[1].charAt(0)) ||
                !isValidChar(stringArr[1].charAt(1))) {
            handleAddRemoveError(command);
        }
        else{
            char firstChar = stringArr[1].charAt(0), secChar = stringArr[1].charAt(2);
            char smallerChar = firstChar < secChar ? firstChar : secChar;
            char biggerChar = firstChar > secChar ? firstChar : secChar;
            for(char i = smallerChar ; i <= biggerChar ; i++){
                addOrRemove(command, i);
            }
        }
    }

    /**
     * checks if a given char is valid.
     * @param c a single char to check validation.
     * @return true if valid, false otherwise.
     */
    private boolean isValidChar(char c) {
        return c >= FIRST_ASCII && c <= LAST_ASCII;
    }

    /**
     * prints an error matching to a non-legal add/remove command.
     * @param command add/remove.
     */
    private void handleAddRemoveError(String command) {
        if(command.equals("add")) {
            System.out.println(ADD_ERROR);
        }
        else{
            System.out.println(REMOVE_ERROR);
        }
    }

    /**
     * adds or removes a given char from char data.
     * @param command add/remove.
     * @param c the char to add/remove.
     */
    private void addOrRemove(String command, char c){
        if(command.equals("add")){
            chars.add(c);
        }
        if(command.equals("remove")){
            chars.remove(c);
        }
    }

    /**
     * prints all chars that are in char data.
     */
    private void printChars() {
        for(char c : chars){
            if(c == ' '){
                System.out.print("space ");
            }
            else{
                System.out.print(c + " ");
            }
        }
        System.out.println();
    }
}
