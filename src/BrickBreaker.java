import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Breakout game class
 *
 * @author reqLane & Andrii Sulimenko
 * @Files BrickBreaker.java      
 */

//To run this program you need to run BrickBreaker.main() (on the left of the 18th line).
//Also you need to stretch the game window a bit to the bottom and right so that you can see game conveniently.
public class BrickBreaker extends GraphicsProgram {
    /** Width and height of application window in pixels */
    public static final int WIDTH = 397;
    public static final int HEIGHT = 600;

    /** Dimensions of the paddle */
    private static int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 20;

    /** Offset of the paddle up from the bottom */
    private static final int PADDLE_Y_OFFSET = 30;

    /** Number of bricks per row */
    private static final int NBRICKS_PER_ROW = 10;

    /** Number of rows of bricks */
    private static final int NBRICK_ROWS = 10;

    /** Separation between bricks */
    private static final int BRICK_SEP = 4;

    /** Width of a brick */
    private static final double BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;

    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;

    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;

    /** Number of turns */
    private int NTURNS = 3;

    /** parameter which declares the process of the game */
    private boolean gameStarted = false;

    /** Delay between two frames of the game */
    private double DELAY = 1000;

    /** Ball horizontal and vertical speed and its direction angle */
    private double ballHorSpeed = 0;
    private double ballVertSpeed = 0;
    private double angle;

    /** Number of bricks already broken at the moment */
    private int bricksBroken;

    /** The ball, paddle and the rectangle behind buttons */
    private GOval ball;
    private GRect paddle, board;

    /** Four points around the ball */
    GPoint pointNW = new GPoint();
    GPoint pointNE = new GPoint();
    GPoint pointSW = new GPoint();
    GPoint pointSE = new GPoint();

    /** The buttons, hearts and labels on the screen */
    private GImage button1, button2, button3, turn1, turn2, turn3;
    GLabel introduction, introduction1, introduction2, score;

    /** All the music and sounds */
    public SoundClip win = new SoundClip("resources/win.wav");
    public SoundClip lose = new SoundClip("resources/fail.wav");
    public SoundClip losePoint = new SoundClip("resources/losePoint.wav");
    public SoundClip soundtrack = new SoundClip("resources/backMusic.wav");
    public SoundClip hit = new SoundClip("resources/hit.wav");
    public SoundClip bounce = new SoundClip("resources/bounce.wav");

    /**
     * void which starts all processes
     */
    public void run() {
        this.setSize(WIDTH, HEIGHT);
        do {
            setup();
            drawButtons();
            addMouseListeners();
            game();
            pause(3000);
            removeAll();
            bricksBroken = 0;
            ballHorSpeed = 0;
            ballVertSpeed = 0;
            NTURNS = 3;
            gameStarted = false;
        } while (true);
    }

    /**
     * void which is in charge of checking whether the ball collides with anything (analyzes every single frame). 
     * Is in charge of ball's direction
     */
    private void checkForCollision() {
        if (ball.getX() <= 0) {
            ball.move(2, 0);
            ballHorSpeed *= -1;

            bounce.stop();
            bounce.rewind();
            bounce.play();
        }else if(ball.getX() >= WIDTH - BALL_RADIUS * 2){
            ball.move(-2, 0);
            ballHorSpeed *= -1;

            bounce.stop();
            bounce.rewind();
            bounce.play();
        }
        if (ball.getY() <= 0) {
            ball.move(0, 2);
            ballVertSpeed *= -1;

            bounce.stop();
            bounce.rewind();
            bounce.play();
        }
        if(isCollObj(button2) || isCollObj(turn1) || isCollObj(turn2) || isCollObj(turn3) || isCollObj(board) || isCollObj(score)){
            return;
        }
        if (getElementAt(pointSW) == paddle && Math.abs(pointSW.getX() - paddle.getX() - PADDLE_WIDTH/2) <= PADDLE_WIDTH/2 - 2) {
            setRandomAngle();
            moveBallWithPoints();

            bounce.stop();
            bounce.rewind();
            bounce.play();

            return;
        }else if(getElementAt(pointSW) == paddle){
            ballHorSpeed = Math.sin(Math.toDegrees(10));
            ballVertSpeed = -Math.cos(Math.toDegrees(10));
            ball.setLocation(ball.getX(), paddle.getY() - 1 - BALL_RADIUS*2);

            bounce.stop();
            bounce.rewind();
            bounce.play();

            return;
        }
        if(getElementAt(pointSE) == paddle && pointSE.getX() - paddle.getX() >= 2 && pointSE.getX() - paddle.getX() <= PADDLE_WIDTH - 2){
            setRandomAngle();
            moveBallWithPoints();

            bounce.stop();
            bounce.rewind();
            bounce.play();

            return;
        }else if(getElementAt(pointSE) == paddle){
            ballHorSpeed = -Math.sin(Math.toDegrees(10));
            ballVertSpeed = -Math.cos(Math.toDegrees(10));
            ball.setLocation(ball.getX(), paddle.getY() - 1 - BALL_RADIUS*2);

            bounce.stop();
            bounce.rewind();
            bounce.play();

            return;
        }
        if(ball.getY() >= HEIGHT){
            losePoint.stop();
            losePoint.rewind();
            losePoint.play();

            ball.setLocation(WIDTH/2 - BALL_RADIUS, HEIGHT/2 - BALL_RADIUS);
            ballHorSpeed = 0;
            ballVertSpeed = 1;
            pause(1000);

            NTURNS--;
        }
        if (getElementAt(pointNW) != null && getElementAt(pointNE) != null
                && getElementAt(pointNW) != getElementAt(pointNE)) {
            if (!isCollObj(paddle)) {
                remove(getElementAt(pointNW));
                remove(getElementAt(pointNE));
                bricksBroken++;
                bricksBroken++;
                hit.setVolume(1);
                hit.stop();
                hit.rewind();
                hit.play();

                ballVertSpeed *= -1;
            }
        } else if (getElementAt(pointNE) != null
                && getElementAt(pointSE) != null
                && getElementAt(pointNE) != getElementAt(pointSE)) {
            if (!isCollObj(paddle)) {
                remove(getElementAt(pointNE));
                remove(getElementAt(pointSE));
                bricksBroken++;
                bricksBroken++;
                hit.setVolume(1);
                hit.stop();
                hit.rewind();
                hit.play();
                ballHorSpeed *= -1;
            }
        } else if (getElementAt(pointNW) != null
                && getElementAt(pointSW) != null
                && getElementAt(pointNW) != getElementAt(pointSW)) {
            remove(getElementAt(pointNW));
            remove(getElementAt(pointSW));
            if (!isCollObj(paddle)) {
                bricksBroken++;
                bricksBroken++;
                hit.setVolume(1);
                hit.stop();
                hit.rewind();
                hit.play();

                ballHorSpeed *= -1;
            }
        } else if (getElementAt(pointSW) != null
                && getElementAt(pointSE) != null
                && getElementAt(pointSW) != getElementAt(pointSE)) {
            if (!isCollObj(paddle)) {
                remove(getElementAt(pointSW));
                remove(getElementAt(pointSE));
                bricksBroken++;
                bricksBroken++;
                hit.setVolume(1);
                hit.stop();
                hit.rewind();
                hit.play();

                ballVertSpeed *= -1;
            }
        } else if (getElementAt(pointNW) != null) {
            if(getElementAt(pointNW) == paddle){
                ballHorSpeed = Math.sin(Math.toDegrees(10));
                ballVertSpeed = -Math.cos(Math.toDegrees(10));
                ball.setLocation(ball.getX(), paddle.getY() - 1 - BALL_RADIUS*2);

                bounce.stop();
                bounce.rewind();
                bounce.play();

                return;
            }else if (pointNW.getX() - getElementAt(pointNW).getX() >= getElementAt(
                    pointNW).getWidth() - 1) {
                ballHorSpeed *= -1;
            } else {
                ballVertSpeed *= -1;
            }

            bricksBroken++;
            hit.setVolume(1);
            hit.stop();
            hit.rewind();
            hit.play();
            remove(getElementAt(pointNW));
        } else if (getElementAt(pointNE) != null) {
            if(getElementAt(pointNE) == paddle){
                ballHorSpeed = -Math.sin(Math.toDegrees(10));
                ballVertSpeed = -Math.cos(Math.toDegrees(10));
                ball.setLocation(ball.getX(), paddle.getY() - 1 - BALL_RADIUS*2);

                bounce.stop();
                bounce.rewind();
                bounce.play();

                return;
            }else if (pointNE.getX() - getElementAt(pointNE).getX() <= 1) {
                ballHorSpeed *= -1;
            } else {
                ballVertSpeed *= -1;
            }

            bricksBroken++;
            hit.setVolume(1);
            hit.stop();
            hit.rewind();
            hit.play();
            remove(getElementAt(pointNE));
        } else if (getElementAt(pointSW) != null) {
            if (pointSW.getX() - getElementAt(pointSW).getX() >= getElementAt(
                    pointSW).getWidth() - 1) {
                ballHorSpeed *= -1;
            } else {
                ballVertSpeed *= -1;
            }
            bricksBroken++;
            hit.setVolume(1);
            hit.stop();
            hit.rewind();
            hit.play();
            remove(getElementAt(pointSW));
        } else if (getElementAt(pointSE) != null) {
            if (pointSE.getX() - getElementAt(pointSE).getX() <= 1) {
                ballHorSpeed *= -1;
            } else {
                ballVertSpeed *= -1;
            }

            bricksBroken++;
            hit.setVolume(1);
            hit.stop();
            hit.rewind();
            hit.play();
            remove(getElementAt(pointSE));
        }
    }

    /**
     * boolean that declares whether an object collides with the ball
     * @param obj object which is being tested
     * @return true if the object collides and false if it does not
     */
    private boolean isCollObj(GObject obj) {
        if (getElementAt(pointNW) == obj || getElementAt(pointNE) == obj
                || getElementAt(pointSW) == obj || getElementAt(pointSE) == obj) {
            return true;
        } else
            return false;
    }

    /**
     * void that creates all visual objects of the game (except the buttons)
     */
    private void setup() {

        this.setBackground(Color.decode("#6798e6"));

        score = new GLabel("Score: 0");
        score.setFont("Arial-24");

        board = new GRect(0, HEIGHT / 2 - WIDTH / 10, WIDTH, WIDTH / 5);
        board.setColor(Color.WHITE);
        board.setFilled(true);
        add(board);

        turn1 = new GImage("turn.gif");
        turn2 = new GImage("turn.gif");
        turn3 = new GImage("turn.gif");

        turn1.setSize(30, 30);
        turn2.setSize(30, 30);
        turn3.setSize(30, 30);

        drawBall();
        drawAllBricks();
        drawPaddle();
        addMouseListeners();


        win.setVolume(1);
        lose.setVolume(1);
        losePoint.setVolume(1);
        bounce.setVolume(1);
    }

    /**
     * void that creates the paddle at the bottom of the game window
     */
    private void drawPaddle() {
        paddle = new GRect(WIDTH / 2 - PADDLE_WIDTH / 2, HEIGHT - PADDLE_Y_OFFSET - 55, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setColor(Color.DARK_GRAY);
        paddle.setFilled(true);
        add(paddle);
    }

    /**
     * void that draws the wall of bricks
     */
    private void drawAllBricks() {
        for (int k = 0; k < NBRICK_ROWS; k++) {
            for (int i = 0; i < NBRICKS_PER_ROW; i++) {
                GRect block = new GRect(i * (BRICK_WIDTH + BRICK_SEP), BRICK_Y_OFFSET + k * (BRICK_HEIGHT + BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
                block.setFilled(true);
                if (k <= 1)
                    block.setFillColor(Color.red);
                else if (k <= 3)
                    block.setFillColor(Color.orange);
                else if (k <= 5)
                    block.setFillColor(Color.yellow);
                else if (k <= 7)
                    block.setFillColor(Color.green);
                else
                    block.setFillColor(Color.cyan);
                add(block);
            }
        }
    }

    /**
     * void that draws the buttons which are responsible for defining the level of the game
     */
    private void drawButtons() {

        introduction = new GLabel("INTRODUCTION");
        introduction1 = new GLabel("Choose the level of the game you want to play");
        introduction2 = new GLabel("Note: from level 1 to 3 the complexity increases");

        introduction.setFont("Arial-36");
        introduction1.setFont("Arial-16");
        introduction2.setFont("Arial-16");

        add(introduction, WIDTH / 2 - introduction.getWidth() / 2, 40);
        add(introduction1, WIDTH / 2 - introduction1.getWidth() / 2, HEIGHT / 2 + 100);
        add(introduction2, WIDTH / 2 - introduction2.getWidth() / 2, HEIGHT / 2 + 120);


        double size = (WIDTH / 5);
        button1 = new GImage("button1.jpg");
        button2 = new GImage("button2.jpg");
        button3 = new GImage("button3.jpg");

        button1.setSize(size, size);
        button2.setSize(size, size);
        button3.setSize(size, size);

        add(button1, WIDTH / 2 - 2 * size + 20, HEIGHT / 2 - size / 2);
        add(button2, WIDTH / 2 - size / 2, HEIGHT / 2 - size / 2);
        add(button3, WIDTH / 2 + size / 2 + 20, HEIGHT / 2 - size / 2);
    }

    /**
     * asynchronous void that tracks moves that user makes and moves the paddle accordingly
     * @param e mouse event related to the click of the mouse
     */
    public void mouseMoved(MouseEvent e) {
        if (gameStarted) {
            double x = e.getX() - PADDLE_WIDTH / 2;
            if (x >= 0 && x <= WIDTH - PADDLE_WIDTH)
                paddle.move(x - paddle.getX(), 0);
        }
    }

    /**
     * asynchronous void that tracks clicks that user makes and checks if it is a try to press the button
     * @param e mouse event related to the click of the mouse
     */
    public void mouseClicked(MouseEvent e) {
        double ClickX = e.getX();
        double ClickY = e.getY();
        GObject collObj = getElementAt(ClickX, ClickY);

        if (collObj == button1) {
            remove(button1);
            remove(button2);
            remove(button3);
            remove(introduction);
            remove(introduction1);
            remove(introduction2);
            remove(board);

            gameStarted = true;
            ballVertSpeed = 1;
            DELAY = 4;
            PADDLE_WIDTH = WIDTH / 3;
            paddle.setSize(WIDTH / 3, PADDLE_HEIGHT);

            add(turn1, 5, 5);
            add(turn2, turn1.getWidth() + 5, 5);
            add(turn3, turn2.getX() + turn1.getWidth(), 5);

            add(score, WIDTH - score.getWidth() - 5, score.getAscent() + 5);

            playSoundtrack();

        } else if (collObj == button2) {
            remove(button1);
            remove(button2);
            remove(button3);
            remove(introduction);
            remove(introduction1);
            remove(introduction2);
            remove(board);

            gameStarted = true;
            ballVertSpeed = 1;
            DELAY = 2;
            PADDLE_WIDTH = WIDTH / 4;
            paddle.setSize(WIDTH / 4, PADDLE_HEIGHT);

            add(turn1, 5, 5);
            add(turn2, turn1.getWidth() + 5, 5);
            add(turn3, turn2.getX() + turn1.getWidth(), 5);

            add(score, WIDTH - score.getWidth() - 5, score.getAscent() + 5);

            playSoundtrack();

        } else if (collObj == button3) {
            remove(button1);
            remove(button2);
            remove(button3);
            remove(introduction);
            remove(introduction1);
            remove(introduction2);
            remove(board);

            gameStarted = true;
            ballVertSpeed = 1;
            DELAY = 1;
            PADDLE_WIDTH = WIDTH / 5;
            paddle.setSize(WIDTH / 5, PADDLE_HEIGHT);

            add(turn1, 5, 5);
            add(turn2, turn1.getWidth() + 5, 5);
            add(turn3, turn2.getX() + turn1.getWidth(), 5);

            add(score, WIDTH - score.getWidth() - 5, score.getAscent() + 5);

            playSoundtrack();
        }
    }

    /**
     * void that is the main loop for the progress of the game
     */
    private void game() {
        addMouseListeners();
        while (gameContinues()) {

            moveBallWithPoints();
            checkForCollision();

            score.setLabel("Score: " + bricksBroken);
            score.setLocation(WIDTH - score.getWidth() - 5, score.getAscent() + 5);

            if (bricksBroken == NBRICK_ROWS * NBRICKS_PER_ROW || NTURNS == 0) {
                if (NTURNS == 0) {
                    remove(turn1);
                }
                endScreen();
                break;
            }
            pause(DELAY);
        }
    }

    /**
     * void that is in charge of moving ball with all the points
     */
    private void moveBallWithPoints() {
        ball.move(ballHorSpeed, ballVertSpeed);
        pointNW.setLocation(ball.getX(), ball.getY());
        pointNE.setLocation(ball.getX() + BALL_RADIUS * 2, ball.getY());
        pointSW.setLocation(ball.getX(), ball.getY() + BALL_RADIUS * 2);
        pointSE.setLocation(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
    }

    /**
     * void that draws and adds ball on the canvas
     */
    private void drawBall() {
        ball = new GOval(WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
        ball.setFilled(true);
        add(ball);
    }

    /**
     * void that defines a random angle for the ball after collision with the paddle
     */
    private void setRandomAngle() {
        angle = rgen.nextDouble(30, 60);
        int horDirection;
        if (ballHorSpeed > 0)
            horDirection = 1;
        else if (ballHorSpeed < 0)
            horDirection = -1;
        else {
            horDirection = rgen.nextInt(1, 2);
            if (horDirection == 2)
                horDirection = -1;
        }
        ballHorSpeed = horDirection * Math.cos(Math.toRadians(angle));
        ballVertSpeed = -Math.sin(Math.toRadians(angle));
    }

    /**
     * private boolean that defines the value of the game in progress
     * @return false if game is over and true if it is in progress
     */
    private boolean gameContinues() {
        if (NTURNS == 3) {
            return true;
        } else if (NTURNS == 2) {
            remove(turn3);
            return true;
        } else if (NTURNS == 1) {
            remove(turn2);
            return true;
        } else {
            return false;
        }
    }

    /**
     * void that displays the end screen when the game is over
     */
    private void endScreen() {
        pause(500);
        this.setBackground(Color.black);
        soundtrack.stop();

        if (NTURNS > 0 && bricksBroken == NBRICK_ROWS * NBRICKS_PER_ROW) {
            removeAll();
            GImage win_screen = new GImage("win.jpg");
            win_screen.setSize(500, 500);
            add(win_screen, WIDTH / 2 - 250, HEIGHT / 2 - 250);

            win.play();
            win_screen.sendForward();
        } else {
            removeAll();
            GImage lose_screen = new GImage("lose.jpg");
            lose_screen.setSize(400, 400);
            add(lose_screen, WIDTH / 2 - 200, HEIGHT / 2 - 200);

            lose.play();
            lose_screen.sendForward();
        }
    }

    /**
     * void that starts the soundtrack of the game
     */
    private void playSoundtrack() {
        soundtrack.setVolume(0.3);
        soundtrack.loop();
        soundtrack.play();
    }

    /**
     * randomly declared value
     */
    RandomGenerator rgen = RandomGenerator.getInstance();
}
