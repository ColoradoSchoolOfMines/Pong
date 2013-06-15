package edu.mines.aakash.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.mines.acmX.exhibit.input_services.hardware.BadFunctionalityRequestException;
import edu.mines.acmX.exhibit.input_services.hardware.HardwareManager;
import edu.mines.acmX.exhibit.input_services.hardware.HardwareManagerManifestException;
import edu.mines.acmX.exhibit.input_services.hardware.UnknownDriverRequest;
import edu.mines.acmX.exhibit.input_services.hardware.devicedata.HandTrackerInterface;
import edu.mines.acmX.exhibit.input_services.hardware.drivers.InvalidConfigurationFileException;
import edu.mines.acmX.exhibit.module_management.modules.ProcessingModule;
import edu.mines.acmX.exhibit.stdlib.graphics.Coordinate3D;
import edu.mines.acmX.exhibit.stdlib.graphics.HandPosition;
import edu.mines.acmX.exhibit.stdlib.input_processing.receivers.HandReceiver;

/**
 * Hello world!
 *
 */
public class Pong extends ProcessingModule {
	
	public static final int STATE_WAITING = 0;
	public static final int STATE_PLAYING = 1;
	public static final int POINTS_OVER = 5;
	
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
		ball = new Ball(screenWidth / 2, screenHeight / 2, 0, 0);
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
			 receiver = new MyHandReceiver();
			 
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
			// Check ball position
			if (ball.getY() < Ball.BALL_RADIUS) {
				ball.setY(Ball.BALL_RADIUS);
				ball.reverseVelocityY();
			} else if (ball.getY() > (screenHeight - Ball.BALL_RADIUS)) {
				ball.setY(screenHeight - Ball.BALL_RADIUS);
				ball.reverseVelocityY();
			}
			
			boolean pointScored = false;
			if (ball.getX() < Paddle.PADDLE_WIDTH) {
				// Player 2 scored a point
				rightPoints++;
				pointScored = true;
			} else if (ball.getX() > (screenWidth - Paddle.PADDLE_WIDTH)) {
				// Player 1 scored a point
				leftPoints++;
				pointScored = true;
			}
			
			if (pointScored) {
				if (leftPoints > POINTS_OVER || rightPoints > POINTS_OVER) {
					// TODO Start new game, wait for new players to join
				} else {
					// TODO Reset ball
				}
			}
			
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
	
	class MyHandReceiver extends HandReceiver {
		
		private Map<Integer, Coordinate3D> handPositions;
		
		public MyHandReceiver() {
			handPositions = new HashMap<Integer, Coordinate3D>();
		}
		
		public void handCreated(HandPosition handPos) {
			handPositions.put(handPos.getId(), handPos.getPosition());
		}
		
		public void handUpdated(HandPosition handPos) {
			handPositions.put(handPos.getId(), handPos.getPosition());
		}
		
		public void handDestroyed(int id) {
			handPositions.remove(id);
		}
		
		public Set<Integer> getHandIDs() {
			return handPositions.keySet();
		}
		
		public Coordinate3D getHandPosition(int id) {
			return handPositions.get(id);
		}
	}
}
