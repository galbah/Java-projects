package src;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import src.brick_strategies.StrategyFactory;
import src.gameobjects.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * the main class of the game, in charge to initialize all objects and add them to game
 * and in charge of the game rules.
 */
public class BrickerGameManager extends GameManager{

    public static final float BORDER_WIDTH = 15;
    public static final float BRICKS_PER_ROW = 7;
    public static final float BRICKS_PER_COL = 8;
    public static final float BRICK_SPACE_FROM_UPPER_BOARDER = 30;
    public static final float BALL_SPEED = 250;
    public static final String END_GAME_LOSE_DIALOG = "you lose! play again?";
    public static final String END_GAME_WIN_DIALOG = "you win! play again?";
    private static final int INITIAL_LIVES = 3;
    private static final int HEART_SIZE = 20;
    private static final float BRICK_HEIGHT = 15;
    private static final float BALL_SIZE = 20;
    private static final Vector2 PADDLE_DIM = new Vector2(200,20);
    private static final int PADDLE_MIN_DIST_FROM_EDGE = 25;
    private static final int WINDOW_DIM_X = 700;
    private static final int WINDOW_DIM_Y = 500;
    private static final int CAMERA_BALL_COLLISIONS = 5; // number of collisions until reset camera
                                                         // from power-up (+1 because starts from 1)
    private static final int MAX_LIVES = 4;
    private Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private Counter livesCounter;
    private Counter bricksCounter;
    private UserInputListener inputListener;
    private SoundReader soundReader;

    public  BrickerGameManager(String windowTitle, Vector2 windowDimensions){ //constructor
        super(windowTitle, windowDimensions);
    }

    /**
     * overrides GameManager's method, adds an initialization to all objects of the game.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        this.windowController = windowController;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.inputListener = inputListener;
        this.soundReader = soundReader;
        this.livesCounter = new Counter(INITIAL_LIVES);
        windowDimensions = windowController.getWindowDimensions();
        createBackground(imageReader, windowDimensions); // creating background
        initializeLives(imageReader); // initializing lives
        this.ball = createBall(imageReader, soundReader); // creating ball
        createPaddle(imageReader, inputListener, windowDimensions); // creating user paddle
        createBricks(imageReader, windowDimensions); // creating bricks
        createBorders(windowDimensions); // creating borders
    }

    /**
     * resets the graphic and numeric life counter to the bottom left of the screen.
     */
    private void initializeLives(ImageReader imageReader){
        windowController.setTargetFramerate(100);
        Renderable heartImage = imageReader.readImage("assets/heart.png", true);
        Vector2 heartsTopLeft = new Vector2(0, windowDimensions.y() - HEART_SIZE);
        Vector2 heartsDimensions = new Vector2(HEART_SIZE, HEART_SIZE);

        GameObject graphicLifeCounter =  new GraphicLifeCounter(heartsTopLeft, heartsDimensions,
                livesCounter, heartImage, gameObjects(), MAX_LIVES);
        gameObjects().addGameObject(graphicLifeCounter, Layer.BACKGROUND);

        Vector2 numLifeTopLeft = new Vector2(MAX_LIVES * HEART_SIZE,
                windowDimensions.y() - HEART_SIZE);
        Vector2 numLifeDimensions = new Vector2(HEART_SIZE, HEART_SIZE);
        GameObject numericLifeCounter = new NumericLifeCounter(livesCounter,
                numLifeTopLeft, numLifeDimensions, gameObjects());
        gameObjects().addGameObject(numericLifeCounter, Layer.BACKGROUND);
    }

    /**
     * sets the background of the game, fits it into given window dimensions.
     */
    private void createBackground(ImageReader imageReader, Vector2 windowDimensions){
        Renderable backgroundImage = imageReader.readImage("assets/DARK_BG2_small.jpeg", false);
        GameObject background = new GameObject(Vector2.ZERO, windowDimensions, backgroundImage);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    /**
     * creates the ball of the game, resets it to start in the middle and go to a random direction.
     * @return the ball object that was created.
     */
    private Ball createBall(ImageReader imageReader, SoundReader soundReader){
        Renderable ballImage = imageReader.readImage("assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/Bubble5_4.wav");
        Ball ball = new Ball(Vector2.ZERO, new Vector2(BALL_SIZE,BALL_SIZE),
                ballImage, collisionSound);
        resetBall(ball);
        this.gameObjects().addGameObject(ball);
        return ball;
    }

    /**
     * creates the paddle and sets it to start at the middle bottom of the screen.
     */
    private void createPaddle(ImageReader imageReader, UserInputListener inputListener,
                              Vector2 windowDimensions){
        Renderable paddleImage = imageReader.readImage("assets/paddle.png", false);
        GameObject paddle = new Paddle(Vector2.ZERO, PADDLE_DIM, paddleImage,
                inputListener, windowDimensions, PADDLE_MIN_DIST_FROM_EDGE);
        paddle.setCenter(new Vector2(windowDimensions.x()/2, windowDimensions.y()-30));
        gameObjects().addGameObject(paddle);
    }

    /**
     * creates the bricks of the game, fits them to the screen dimensions, amount is determined by macros.
     * for each brick randomly chooses a collision strategy using StrategyFactory.
     */
    private void createBricks(ImageReader imageReader, Vector2 windowDimensions){
        this.bricksCounter = new Counter();
        Renderable brickImage = imageReader.readImage("assets/brick.png", false);
        Vector2 brickDimensions = new Vector2(windowDimensions.x()/(BRICKS_PER_ROW + 1),BRICK_HEIGHT);
        Vector2 brickPos = new Vector2((windowDimensions.x() - brickDimensions.x() * BRICKS_PER_ROW) / 2,
                BRICK_SPACE_FROM_UPPER_BOARDER);
        StrategyFactory strategyFactory = new StrategyFactory(gameObjects(),
                imageReader, soundReader, windowDimensions, inputListener, this,
                ball, brickDimensions, HEART_SIZE, livesCounter);
        for(int i = 0 ; i < BRICKS_PER_COL ; i++){
            for (int j = 0; j < BRICKS_PER_ROW; j++) {
                if(j != 0)
                    brickPos = brickPos.add(new Vector2(brickDimensions.x(), 0));
                gameObjects().addGameObject(new Brick(brickPos, brickDimensions, brickImage,
                strategyFactory.getRandomStrategy(false, false ), bricksCounter)); // selects random strategy
            }
            brickPos = brickPos.add(new Vector2(-(BRICKS_PER_ROW-1) * brickDimensions.x(),
                    brickDimensions.y()));
        }
    }

    /**
     * creates 3 borders on the edge of the screen (top, right and left),
     * width is determined according to BORDER_WIDTH.
     * @param windowDimensions the dimensions of the opened window
     */
    private void createBorders(Vector2 windowDimensions){
        GameObject upperBorder = new GameObject(Vector2.ZERO,
                new Vector2(windowDimensions.x(), BORDER_WIDTH), null);
        GameObject leftBorder = new GameObject(Vector2.ZERO,
                new Vector2(BORDER_WIDTH, windowDimensions.y()-BORDER_WIDTH),  null);
        GameObject rightBorder = new GameObject(new Vector2(windowDimensions.x()-BORDER_WIDTH, 0),
                new Vector2(BORDER_WIDTH, windowDimensions.y()),  null);
        gameObjects().addGameObject(upperBorder);
        gameObjects().addGameObject(leftBorder);
        gameObjects().addGameObject(rightBorder);
    }

    /** resets the ball to the center of the screen and randomly chooses a direction to start.
     */
    private void resetBall(GameObject ball){
        float ballVelY = BALL_SPEED;
        float ballVelX = BALL_SPEED;
        Random rand = new Random();
        if(rand.nextBoolean()){
            ballVelX *= -1;
        }
        if(rand.nextBoolean()){
            ballVelY *= -1;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));
        ball.setCenter(windowDimensions.mult(0.5F));
    }

    /**
     * checks if round is finished and resets the ball or finishes the game.
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
        for(GameObject gameObject : gameObjects()){
            if (!(gameObject instanceof Ball) && gameObject.getCenter().y() > windowDimensions.y()){
                gameObjects().removeGameObject(gameObject);
            }
        }
        if(ball.getCollisionCount() >= CAMERA_BALL_COLLISIONS){
            this.setCamera(null);
            ball.resetCollisionCount();
        }
        if(ball.getCenter().y() > windowDimensions.y()){
            livesCounter.decrement();
            resetBall(this.ball);
        }
        isGameFinished();
    }

    /**
     * checks if: bricks are finished or 'w' was pressed - WIN.
     *            lives are finished - LOSE.
     */
    public void isGameFinished(){
        if(livesCounter.value() == 0){
            if(windowController.openYesNoDialog(END_GAME_LOSE_DIALOG)){
                windowController.resetGame();
            }
            else{
                windowController.closeWindow();
            }
        }
        if(bricksCounter.value() == BRICKS_PER_ROW * BRICKS_PER_COL ||
                inputListener.isKeyPressed(KeyEvent.VK_W)){
            if(windowController.openYesNoDialog(END_GAME_WIN_DIALOG)){
                windowController.resetGame();
            }
            else{
                windowController.closeWindow();
            }
        }
    }

    public static void main(String[] args) {
        new BrickerGameManager("Bricker", new Vector2(WINDOW_DIM_X, WINDOW_DIM_Y)).run();
    }
}