package edu.mines.aakash.modules.Pong;

import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import processing.core.PApplet;
import edu.mines.aakash.modules.Pong.input.MyHandReceiver;
import edu.mines.aakash.modules.Pong.logic.Restart;
import edu.mines.aakash.modules.Pong.players.HumanPlayer;
import edu.mines.aakash.modules.Pong.players.KinectHumanPlayer;
import edu.mines.aakash.modules.Pong.players.Player;
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
@SuppressWarnings("serial")
public class Pong extends ProcessingModule {

	public static final String GAME_OVER = "Game Over";

	public enum State {
		STATE_GAME_START_WAITING, STATE_PLAYING, STATE_PAUSED, STATE_OVER
	}

	public static final int POINTS_OVER = 5;
	public static final boolean DEBUG_HANDS = false;
	
	public static final int END_DELAY = 5000;

	private State gameState;

	private static Logger logger = LogManager.getLogger(Pong.class);

	// Game models
	private Ball ball;
	private Paddle leftPaddle;
	private Paddle rightPaddle;

	private Player leftPlayer;
	private Player rightPlayer;

	// We only use these to determine if a player is connected,
	// not for game logic
	private boolean leftPlayerConnected = false;
	private boolean rightPlayerConnected = false;

	private int initialVelocityX;

	// 0 - left, 1 - right
	private int lastPoint;
	private int leftPoints;
	private int rightPoints;

	private HandTrackerInterface handDriver;
	private MyHandReceiver receiver;
	private Timer timer;

	public void setup() {
		size(screenWidth, screenHeight);
		frameRate(30);
		
		startRound();

		// Create ball and paddle
		ball = new Ball(screenWidth / 2, screenHeight / 2);
		leftPaddle = new Paddle(0, (screenHeight - Paddle.PADDLE_HEIGHT) / 2);
		rightPaddle = new Paddle(screenWidth - Paddle.PADDLE_WIDTH,
				(screenHeight - Paddle.PADDLE_HEIGHT) / 2);

		// Register hand tracking
		HardwareManager hm;
		try {
			hm = HardwareManager.getInstance();
			receiver = new MyHandReceiver(this);
			if (!DEBUG_HANDS) {
				handDriver = (HandTrackerInterface) hm
						.getInitialDriver("handtracking");

				handDriver.registerHandCreated(receiver);
				handDriver.registerHandUpdated(receiver);
				handDriver.registerHandDestroyed(receiver);
			} else {
				EventManager em = EventManager.getInstance();

				em.registerReceiver(EventType.HAND_CREATED, receiver);
				em.registerReceiver(EventType.HAND_DESTROYED, receiver);
				em.registerReceiver(EventType.HAND_UPDATED, receiver);
			}
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
		if (!DEBUG_HANDS) {
			handDriver.updateDriver();
		}

		switch (gameState) {
		case STATE_GAME_START_WAITING:
			// let the players see their movements
			if (leftPlayerConnected) {
				leftPlayer.updatePaddlePosition();
			}
			if (rightPlayerConnected) {
				rightPlayer.updatePaddlePosition();
			}
			break;
		case STATE_PAUSED:
			// let the players see their movements
			if (leftPlayerConnected) {
				leftPlayer.updatePaddlePosition();
			} 
			if (rightPlayerConnected) {
				
				rightPlayer.updatePaddlePosition();
			}
			break;
		case STATE_PLAYING:

			ball.update();

			checkBallPosition();

			// Update player information
			leftPlayer.updatePaddlePosition();
			rightPlayer.updatePaddlePosition();
			break;
		case STATE_OVER:
			// let the players see their movements
			if (leftPlayerConnected) {
				leftPlayer.updatePaddlePosition();
			}
			if (rightPlayerConnected) {
				
				rightPlayer.updatePaddlePosition();
			} 
			break;
		default:
			logger.error("Not a valid game state");
			break;
		}

	}

	public void draw() {
		drawCommon();

		switch (gameState) {
		case STATE_GAME_START_WAITING:
			drawStateWaiting();
			break;
		case STATE_PAUSED:
			drawStateWaiting();
			break;
		case STATE_PLAYING:
			drawStatePlaying();
			break;
		case STATE_OVER:
			drawStateOver();
			break;
		default:
			logger.error("Not a valid game state");
			break;
		}
	}

	public void drawCommon() {
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
	}

	public void drawStatePlaying() {
		// Draw score
		final float PADDING_FROM_CENTER = 20;
		final float PADDING_FROM_TOP = 64;
		stroke(255);
		fill(255);
		int center = screenWidth / 2;
		textSize(64);
		textAlign(RIGHT);
		// Not sure why the extra character is needed but it defintely lines up perfectly.
		text(PApplet.nfs(leftPoints, 2) + " ", center - PADDING_FROM_CENTER, PADDING_FROM_TOP);
		textAlign(LEFT);
		text(PApplet.nfs(rightPoints, 2), center + PADDING_FROM_CENTER, PADDING_FROM_TOP);
	}

	public void drawStateWaiting() {
		// TODO Draw welcome text
		if (!leftPlayerConnected) {
			textSize(24);
			stroke(255);
			fill(255);
			textAlign(CENTER,CENTER);
			text("Waiting for left player to join", screenWidth / 4,
					screenHeight / 2);
		}

		if (!rightPlayerConnected) {
			textSize(24);
			stroke(255);
			fill(255);
			textAlign(CENTER,CENTER);
			text("Waiting for right player to join", 3 * screenWidth / 4,
					screenHeight / 2);
		}
	}

	public void drawStateOver() {
		int centerx = screenWidth / 2;
		int centery = screenHeight / 2;
		textAlign(CENTER,CENTER);
		text(GAME_OVER, centerx, centery);
	}

	public void initGame() {
		if (timer != null) {
			timer.cancel();
		}

		// We want the ball to go across the screen in 2.5 seconds.
		initialVelocityX = (int) (screenWidth / 2.5 / frameRate);
		
		ball.setInitialVelocity(initialVelocityX, 4);

		lastPoint = 1;
		leftPoints = rightPoints = 0;

		gameState = State.STATE_PLAYING;
	}
	


	public void bothPlayersConnectedEvent() {
		switch (gameState) {
		case STATE_PAUSED:
			gameState = State.STATE_PLAYING;
			break;
		case STATE_GAME_START_WAITING:
			initGame();
			break;
		default:
			logger.error("Unexpected game state when both players are connected");
		}
	}

	public void startRound() {
		gameState = State.STATE_GAME_START_WAITING;
		leftPlayerConnected = false;
		rightPlayerConnected = false;
	}

	public HumanPlayer createPlayer(int handID) {
		Paddle p;
		if (handID == receiver.getRightHandID()) {
			p = rightPaddle;
		} else if (handID == receiver.getLeftHandID()) {
			p = leftPaddle;
		} else {
			logger.error("Recieved invalid hand id");
			return null;
		}

		if (!DEBUG_HANDS) {
			return new KinectHumanPlayer(p, ball, height, receiver, handID,
					handDriver.getHandTrackingHeight(), height, 1 / (float) 6);
		} else {
			return new HumanPlayer(p, ball, height, receiver, handID);
		}
	}

	public void createLeftPlayer() {
		leftPlayer = createPlayer(receiver.getLeftHandID());
	}

	public void createRightPlayer() {
		rightPlayer = createPlayer(receiver.getRightHandID());
	}

	public void endGame() {
		gameState = State.STATE_OVER;
		drawStateOver();
		launchTimer(this);
	}
	
	private void launchTimer(Pong object) {
		timer = new Timer();
		timer.schedule(new Restart(object), END_DELAY);
	}

	public void resetBall() {
		ball.setX(screenWidth / 2);
		ball.setY(screenHeight / 2);
		int direction = lastPoint == 0 ? -1 : 1;
		ball.setInitialVelocity(direction * initialVelocityX, 4);
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
		if (ballX < 0) {
			// Player 2 scored a point
			System.out.println("Player 2 scores a point");
			rightPoints++;
			lastPoint = 1;
			pointScored = true;
		} else if (ballX > screenWidth) {
			// Player 1 scored a point
			System.out.println("Player 1 scores a point");
			lastPoint = 0;
			leftPoints++;
			pointScored = true;
		}

		if (pointScored) {
			if (leftPoints == POINTS_OVER || rightPoints == POINTS_OVER) {
				endGame();
			} else {
				resetBall();
			}
		}
	}

	public void checkBallCollisionOnLeftPaddle() {
		int ballX = ball.getX();
		int ballY = ball.getY();

		if (ballY > leftPaddle.getY()
				&& ballY < (leftPaddle.getY() + Paddle.PADDLE_HEIGHT)) {
			if (Math.abs((ballX - leftPaddle.getX() - Paddle.PADDLE_WIDTH)) < Ball.BALL_RADIUS) {
				ball.setX(leftPaddle.getX() + Paddle.PADDLE_WIDTH
						+ Ball.BALL_RADIUS);
				// turn around and move to be outside the paddle
				ball.reverseVelocityX();
				ball.setX(leftPaddle.getX() + Paddle.PADDLE_WIDTH + ball.BALL_RADIUS);
			}
		}
	}

	public void checkBallCollisionOnRightPaddle() {
		int ballX = ball.getX();
		int ballY = ball.getY();
		if (ballY > rightPaddle.getY()
				&& ballY < (rightPaddle.getY() + Paddle.PADDLE_HEIGHT)) {
			if (Math.abs((ballX - rightPaddle.getX())) < Ball.BALL_RADIUS) {
				ball.setX(rightPaddle.getX() - Ball.BALL_RADIUS);
				// turn around and move to be outside the paddle
				ball.reverseVelocityX();
				ball.setX(rightPaddle.getX() - ball.BALL_RADIUS);
			}
		}
	}

	public void drawPaddle(Paddle paddle) {
		stroke(255);
		fill(255);
		rect(paddle.getX(), paddle.getY(), Paddle.PADDLE_WIDTH,
				Paddle.PADDLE_HEIGHT);
	}

	public void drawBall(Ball ball) {
		int red = color(255, 0, 0);
		stroke(red);
		fill(red);

		ellipse(ball.getX(), ball.getY(), Ball.BALL_RADIUS * 2, Ball.BALL_RADIUS * 2);
	}

	public void mouseReleased() {
		if (DEBUG_HANDS) {
			if (!leftPlayerConnected) {
				EventType type = EventType.HAND_CREATED;
				EventManager.getInstance()
						.fireEvent(
								type,
								new HandPosition(1, new Coordinate3D(mouseX,
										mouseY, 0)));

			}
			if (!rightPlayerConnected) {
				EventType type = EventType.HAND_CREATED;
				EventManager.getInstance()
						.fireEvent(
								type,
								new HandPosition(2, new Coordinate3D(mouseX,
										mouseY, 0)));

			}
		}
	}

	public void mouseMoved() {
		if (DEBUG_HANDS && gameState == State.STATE_PLAYING) {
			EventManager.getInstance().fireEvent(EventType.HAND_UPDATED,
					new HandPosition(1, new Coordinate3D(mouseX, mouseY, 0)));
			EventManager.getInstance().fireEvent(EventType.HAND_UPDATED,
					new HandPosition(2, new Coordinate3D(mouseX, mouseY, 0)));
		}
	}

	public boolean isLeftPlayerConnected() {
		return leftPlayerConnected;
	}

	public boolean isRightPlayerConnected() {
		return rightPlayerConnected;
	}
	
	public boolean areBothPlayersConnected() {
		return isLeftPlayerConnected() && isRightPlayerConnected();
	}

	public void leftPlayerConnected(boolean val) {
		leftPlayerConnected = val;
		if(val == false) {
			playerDisconnectEvent();
		}
	}

	public void rightPlayerConnected(boolean val) {
		rightPlayerConnected = val;
		if(val == false) {
			playerDisconnectEvent();
		}
	}
	
	public void playerDisconnectEvent() {
		switch(gameState) {
		case STATE_PLAYING:
			gameState = State.STATE_PAUSED;
			break;
		default:
			// do nothing in other states
		}
	}
}
