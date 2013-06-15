package edu.mines.aakash.modules;

import processing.core.PApplet;
import edu.mines.aakash.modules.input.MyHandReceiver;
import edu.mines.aakash.modules.players.HumanPlayer;
import edu.mines.aakash.modules.players.Player;
import edu.mines.acmX.exhibit.input_services.events.EventManager;
import edu.mines.acmX.exhibit.input_services.events.EventType;
import edu.mines.acmX.exhibit.input_services.hardware.BadFunctionalityRequestException;
import edu.mines.acmX.exhibit.input_services.hardware.HardwareManager;
import edu.mines.acmX.exhibit.input_services.hardware.HardwareManagerManifestException;
import edu.mines.acmX.exhibit.input_services.hardware.UnknownDriverRequest;
import edu.mines.acmX.exhibit.input_services.hardware.devicedata.HandTrackerInterface;
import edu.mines.acmX.exhibit.input_services.hardware.drivers.InvalidConfigurationFileException;
import edu.mines.acmX.exhibit.module_management.modules.ProcessingModule;
import edu.mines.acmX.exhibit.stdlib.graphics.Coordinate3D;
import edu.mines.acmX.exhibit.stdlib.graphics.HandPosition;

/**
 * Hello world!
 *
 */
public class Pong extends ProcessingModule {
	
	public static final int STATE_WAITING = 0;
	public static final int STATE_PLAYING = 1;
	public static final int POINTS_OVER = 5;
	public static final boolean DEBUG_HANDS = true;
	
	private int gameState;
	
	// Game models
	private Ball ball;
	private Paddle leftPaddle;
	private Paddle rightPaddle;
	
	private Player leftPlayer;
	private Player rightPlayer;
	
	// We only use these to determine if a player is connected,
	// not for game logic
	private boolean leftPlayerConnected;
	private boolean rightPlayerConnected;
	
	private int initialVelocityX;
	
	private int leftPoints;
	private int rightPoints;
	
	private HandTrackerInterface handDriver;
	private MyHandReceiver receiver;
	
	public void setup() {
		size(screenWidth, screenHeight);
		frameRate(30);
		
		// Initialize game state
		gameState = STATE_WAITING;
		
		// We want the ball to go across the screen in 5 seconds.
		initialVelocityX = (int) (screenWidth / 5 / frameRate); 
		
		// Create ball and paddle
		ball = new Ball(screenWidth / 2, screenHeight / 2);
		leftPaddle = new Paddle(0, (screenHeight - Paddle.PADDLE_HEIGHT) / 2);
		rightPaddle = new Paddle(screenWidth - Paddle.PADDLE_WIDTH,
								(screenHeight - Paddle.PADDLE_HEIGHT) / 2);
		
		leftPlayerConnected = false;
		rightPlayerConnected = false;
		
		// Register hand tracking
		HardwareManager hm;
		try {
			 hm = HardwareManager.getInstance();
			 handDriver = (HandTrackerInterface) hm.getInitialDriver("handtracking");
			 receiver = new MyHandReceiver(this);
			 
			 handDriver.registerHandCreated(receiver);
			 handDriver.registerHandUpdated(receiver);
			 handDriver.registerHandDestroyed(receiver);
		} catch (HardwareManagerManifestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadFunctionalityRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownDriverRequest e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void update() {
		
		if (gameState == STATE_PLAYING) {
			
			checkBallPosition();
			
			// Update ball location
			ball.update();
			
			// Update player information
			leftPlayer.updatePaddlePosition();
			rightPlayer.updatePaddlePosition();
		}
	}
	
	public void draw() {
		update();
		background(0);
		
		// draw middle line
		stroke(255);
		line(screenWidth / 2, 0, screenWidth / 2, screenHeight);
		
		// draw paddle
		drawPaddle(leftPaddle);
		drawPaddle(rightPaddle);
		
		// draw ball
		drawBall(ball);
		
		if (gameState == STATE_WAITING) {
			// TODO Draw welcome text
			if (!leftPlayerConnected) {
				stroke(255);
				fill(255);
				text("Waiting for left player to join",
						screenWidth / 4,
						screenHeight / 2);
			}
			
			if (!rightPlayerConnected) {
				stroke(255);
				fill(255);
				text("Waiting for right player to join",
						3 * screenWidth / 4,
						screenHeight / 2);
			}
		} else if (gameState == STATE_PLAYING) {
			// Draw score
			stroke(255);
			fill(255);
			text(PApplet.nfs(leftPoints, 2) + "", 20, 20);
			text(PApplet.nfs(rightPoints, 2), screenWidth - 20, 20);
			
		}
	}
	
	public void initGame() {
		ball.setInitialVelocity(initialVelocityX, 4);
		leftPlayer = new HumanPlayer(leftPaddle, ball, receiver, receiver.getLeftHandID());
		rightPlayer = new HumanPlayer(rightPaddle, ball, receiver, receiver.getRightHandID());
		
		leftPoints = rightPoints = 0;
		
		gameState = STATE_PLAYING;
	}
	
	public void endGame() {
		exit();
	}
	
	public void resetBall() {
		ball.setX(screenWidth / 2);
		ball.setY(screenHeight / 2);
		ball.setInitialVelocity(initialVelocityX, 4);
	}
	
	public void checkBallPosition() {
		int ballX = ball.getX();
		int ballY = ball.getY();
		
		// Check ball position against top wall
		if (ballY < Ball.BALL_RADIUS) {
			ball.setY(Ball.BALL_RADIUS);
			ball.reverseVelocityY();
		} else if (ballY > (screenHeight - Ball.BALL_RADIUS)) {
			ball.setY(screenHeight - Ball.BALL_RADIUS);
			ball.reverseVelocityY();
		}

		// Check for ball collision against left paddle
		checkBallCollisionOnLeftPaddle();
		checkBallCollisionOnRightPaddle();
		
		// Check if the ball is past the paddle
		boolean pointScored = false;
		if (ballX < leftPaddle.getX()) {
			// Player 2 scored a point
			System.out.println("Player 2 scores a point");
			rightPoints++;
			pointScored = true;
		} else if (ballX > (screenWidth - Paddle.PADDLE_WIDTH)) {
			// Player 1 scored a point
			System.out.println("Player 1 scores a point");
			leftPoints++;
			pointScored = true;
		}

		if (pointScored) {
			if (leftPoints > POINTS_OVER || rightPoints > POINTS_OVER) {
				initGame();
			} else {
				resetBall();
			}
		}
	}
	
	public void checkBallCollisionOnLeftPaddle() {
		int ballX = ball.getX();
		int ballY = ball.getY();
		
		if (ballY > leftPaddle.getY() && ballY < (leftPaddle.getY() + Paddle.PADDLE_HEIGHT)) {
			if (Math.abs((ballX - leftPaddle.getX() - Paddle.PADDLE_WIDTH)) < Ball.BALL_RADIUS) {
				ball.setX(leftPaddle.getX() + Paddle.PADDLE_WIDTH + Ball.BALL_RADIUS);
				ball.reverseVelocityX();
			}
		}
	}
	
	public void checkBallCollisionOnRightPaddle() {
		int ballX = ball.getX();
		int ballY = ball.getY();
		if (ballY > rightPaddle.getY() && ballY < (rightPaddle.getY() + Paddle.PADDLE_HEIGHT)) {
			if (Math.abs((ballX - rightPaddle.getX())) < Ball.BALL_RADIUS) {
				ball.setX(rightPaddle.getX() - Ball.BALL_RADIUS);
				ball.reverseVelocityX();
			}
		}
	}
	
	public void drawPaddle(Paddle paddle) {
		stroke(255);
		fill(255);
		rect(paddle.getX(), paddle.getY(),
			 Paddle.PADDLE_WIDTH, Paddle.PADDLE_HEIGHT);
	}
	
	public void drawBall(Ball ball) {
		int red = color(255, 0, 0);
		stroke(red);
		fill(red);
		
		ellipse(ball.getX(), ball.getY(), Ball.BALL_RADIUS, Ball.BALL_RADIUS);
	}
	
	public void mouseReleased() {
		if (DEBUG_HANDS) {
			if (mouseButton == LEFT) {
				EventType type = EventType.HAND_UPDATED;
				if (!leftPlayerConnected) {
					type = EventType.HAND_CREATED;
				}
				EventManager.getInstance().fireEvent(type,
						new HandPosition(1, new Coordinate3D(mouseX, mouseY, 0)));
			} else if (mouseButton == RIGHT) {
				EventType type = EventType.HAND_UPDATED;
				if (!rightPlayerConnected) {
					type = EventType.HAND_CREATED;
				}
				EventManager.getInstance().fireEvent(type,
						new HandPosition(2, new Coordinate3D(mouseX, mouseY, 0)));
			}
		}
	}
	
	public void mouseMoved() {
		if (DEBUG_HANDS && gameState == STATE_PLAYING) {
			if (mouseButton == LEFT) {
				EventManager.getInstance().fireEvent(EventType.HAND_UPDATED,
						new HandPosition(1, new Coordinate3D(mouseX, mouseY, 0)));
			} else if (mouseButton == RIGHT) {
				EventManager.getInstance().fireEvent(EventType.HAND_UPDATED,
						new HandPosition(2, new Coordinate3D(mouseX, mouseY, 0)));
			}
		}
	}
	
	public boolean isLeftPlayerConnected() {
		return leftPlayerConnected;
	}
	
	public boolean isRightPlayerConnected() {
		return rightPlayerConnected;
	}
	
	public void leftPlayerConnected(boolean val) {
		leftPlayerConnected = val;
	}
	
	public void rightPlayerConnected(boolean val) {
		rightPlayerConnected = val;
	}
}
