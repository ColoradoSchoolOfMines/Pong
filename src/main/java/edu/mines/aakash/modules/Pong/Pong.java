package edu.mines.aakash.modules.Pong;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.Timer;

import edu.mines.acmX.exhibit.input_services.hardware.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import processing.core.PApplet;
import edu.mines.aakash.modules.Pong.input.MyHandReceiver;
import edu.mines.aakash.modules.Pong.logic.*;
import edu.mines.aakash.modules.Pong.players.HumanPlayer;
import edu.mines.aakash.modules.Pong.players.KinectHumanPlayer;
import edu.mines.aakash.modules.Pong.players.Player;
import edu.mines.acmX.exhibit.input_services.events.EventManager;
import edu.mines.acmX.exhibit.input_services.events.EventType;
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
	
	public static final int BALL_VARIABILITY = 40;

	public static final int END_DELAY = 5000;
	public static final int IDLE_DELAY = 30000;
	public static final int RESET_DELAY = 500;

	private State gameState;

	private static Logger logger = LogManager.getLogger(Pong.class);
	
	Random rand;

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

	private int totalVelocity;

	// 0 - left, 1 - right
	private int lastPoint;
	private int leftPoints;
	private int rightPoints;

	private HandTrackerInterface handDriver;
	private MyHandReceiver receiver;
	private Timer timer;
	private Timer idle;

	public void setup() {
		rand = new Random();
		
		size(width, height);
		frameRate(30);
		
		startRound();

		// Create ball and paddle
		ball = new Ball(width / 2, height / 2);
		leftPaddle = new Paddle(0, (height - Paddle.PADDLE_HEIGHT) / 2);
		rightPaddle = new Paddle(width - Paddle.PADDLE_WIDTH,
				(height - Paddle.PADDLE_HEIGHT) / 2);

		// Register hand tracking
		HardwareManager hm;
		try {
			receiver = new MyHandReceiver(this);
			if (!DEBUG_HANDS) {
				handDriver = (HandTrackerInterface) getInitialDriver("handtracking");

				handDriver.registerHandCreated(receiver);
				handDriver.registerHandUpdated(receiver);
				handDriver.registerHandDestroyed(receiver);
			} else {
				EventManager em = EventManager.getInstance();

				em.registerReceiver(EventType.HAND_CREATED, receiver);
				em.registerReceiver(EventType.HAND_DESTROYED, receiver);
				em.registerReceiver(EventType.HAND_UPDATED, receiver);
			}
		} catch (BadFunctionalityRequestException e) {
			e.printStackTrace();
		} catch (UnknownDriverRequest e) {
			e.printStackTrace();
		} catch (InvalidConfigurationFileException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
            e.printStackTrace();
        } catch (BadDeviceFunctionalityRequestException e) {
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

			// nobody connected so lets start a timer to exit
			if(!leftPlayerConnected && !rightPlayerConnected && timer == null){
				idle = new Timer();
				idle.schedule(new ExitTask(this), IDLE_DELAY);
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

			// nobody connected so lets start a timer to exit
			if(!leftPlayerConnected && !rightPlayerConnected && timer == null)   {
				idle = new Timer();
				idle.schedule(new ExitTask(this), IDLE_DELAY);
			}

			break;
		case STATE_PLAYING:

			if(idle != null)
				idle.cancel();
			idle = null;

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
		line(width / 2, 0, width / 2, height);

		// draw paddle
		drawPaddle(leftPaddle);
		drawPaddle(rightPaddle);


	}

	public void drawStatePlaying() {
		// draw ball
		drawBall(ball);
		// Draw score
		final float PADDING_FROM_CENTER = 20;
		final float PADDING_FROM_TOP = 64;
		stroke(255);
		fill(255);
		int center = width / 2;
		textSize(64);
		textAlign(RIGHT);
		// Not sure why the extra character is needed but it defintely lines up perfectly.
		text(PApplet.nfs(leftPoints, 2) + " ", center - PADDING_FROM_CENTER, PADDING_FROM_TOP);
		textAlign(LEFT);
		text(PApplet.nfs(rightPoints, 2), center + PADDING_FROM_CENTER, PADDING_FROM_TOP);
	}

	public void drawStateWaiting() {
		// draw ball
		drawBall(ball);
		// TODO Draw welcome text
		textSize(18);
		if (!leftPlayerConnected) {
			textSize(24);
			stroke(255);
			fill(255);
			textAlign(CENTER,CENTER);
			text("Waiting for left player to join", width / 4,
					height / 2);
		}

		if (!rightPlayerConnected && leftPlayerConnected) {
			textSize(24);
			stroke(255);
			fill(255);
			textAlign(CENTER,CENTER);
			text("Waiting for right player to join", 3 * width / 4,
					height / 2);
		}
	}

	public void drawStateOver() {
		int centerx = width / 2;
		int centery = height / 2;

		textSize(64);
		textAlign(CENTER,CENTER);
		text(GAME_OVER, centerx, centery);

	}

	public void initGame() {
		if (timer != null) {
			timer.cancel();
		}

		resetBall();
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

		if(idle != null)
			idle.cancel();
		idle = null;

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
	
	private double randWithinRange(double low, double high) {
		return low + (Math.random() * (high - low));
	}
	
	private double randWithinRangeWithOffset(double low, double high, double offset) {
		double rand = randWithinRange(low, high);
		if(rand < 0) {
			return rand - offset;
		} else {
			return rand + offset;
		}
	}
	
	private double pythagOtherLeg(double c, double a) {
		return Math.pow(Math.pow(c, 2) - Math.pow(a,2),0.5);
	}

	public void resetBall() {
		// We want the ball to go across the screen in 2.5 seconds.
		totalVelocity = (int) (width / 2.5 / frameRate);
		logger.debug("Total v: " + totalVelocity);
		double velocityY = randWithinRangeWithOffset(-totalVelocity/4.0, totalVelocity/4.0, totalVelocity/8.0);
		double velocityX = pythagOtherLeg(totalVelocity, velocityY);
		logger.debug("velocity: (" + velocityX + "," + velocityY +")");
		ball.setX(width / 2);
		ball.setY(height / 2);
		int direction = lastPoint == 0 ? -1 : 1;
		ball.setInitialVelocity(direction * velocityX, velocityY);
		ball.setVisible();
	}

	public void checkBallPosition() {
		int ballX = ball.getX();
		int ballY = ball.getY();

		// Check ball position against top wall
		if (ballY < Ball.BALL_RADIUS) {
			ball.setY(Ball.BALL_RADIUS);
			ball.reverseVelocityY();
		} else if (ballY > (height - Ball.BALL_RADIUS)) {
			ball.setY(height - Ball.BALL_RADIUS);
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
		} else if (ballX > width) {
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
				ball.setX(width / 2);
				ball.setInitialVelocity(0, 0);
				ball.setInvisible();
				new Timer().schedule(new ResetTask(this), RESET_DELAY);
			}
		}
	}

	public void checkBallCollisionOnLeftPaddle() {
		if( ball.isIntersectedWith(leftPaddle)) {
			ball.reverseVelocityX();
			ball.setX((int)leftPaddle.getX() + Paddle.PADDLE_WIDTH + ball.BALL_RADIUS);
		}
	}

	public void checkBallCollisionOnRightPaddle() {
		if( ball.isIntersectedWith(rightPaddle)) {
			ball.reverseVelocityX();
			ball.setX((int)rightPaddle.getX() - ball.BALL_RADIUS);
		}
	}

	public void drawPaddle(Paddle paddle) {
		stroke(255);
		fill(255);
		rect((float)paddle.getX(), (float)paddle.getY(), Paddle.PADDLE_WIDTH,
				Paddle.PADDLE_HEIGHT);
	}

	public void drawBall(Ball ball) {
		if(!ball.isVisible())
			return;

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
