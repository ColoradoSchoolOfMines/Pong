package edu.mines.aakash.modules;

import edu.mines.acmX.exhibit.module_management.modules.ProcessingModule;

/**
 * Hello world!
 *
 */
public class Pong extends ProcessingModule {
	
	private Ball ball;
	private Paddle leftPaddle;
	private Paddle rightPaddle;
	
	private Player leftPlayer;
	private Player rightPlayer;
	
	private boolean leftPlayerConnected;
	private boolean rightPlayerConnected;
	
	private int initialVelocityX;
	
	public void setup() {
		size(screenWidth, screenHeight);
		frameRate(30);
		initialVelocityX = (int) (screenWidth / 5 / frameRate); 
		
		ball = new Ball(screenWidth / 2, screenHeight / 2, initialVelocityX, 0);
		leftPaddle = new Paddle(0, (screenHeight - Paddle.PADDLE_HEIGHT) / 2);
		rightPaddle = new Paddle(screenWidth - Paddle.PADDLE_WIDTH,
								(screenHeight - Paddle.PADDLE_HEIGHT) / 2);
		leftPlayerConnected = false;
		rightPlayerConnected = false;
		
	}
	
	public void update() {
		System.out.println(millis());
		// Check ball position
		if (ball.getY() < Ball.BALL_RADIUS) {
			ball.setY(Ball.BALL_RADIUS);
			ball.reverseVelocityY();
		} else if (ball.getY() > (screenHeight - Ball.BALL_RADIUS)) {
			ball.setY(screenHeight - Ball.BALL_RADIUS);
			ball.reverseVelocityY();
		}
		
		if (ball.getX() < Paddle.PADDLE_WIDTH) {
			// Player 2 scored a point
		} else if (ball.getX() > (screenWidth - Paddle.PADDLE_WIDTH)) {
			// Player 1 scored a point
		}
		
		// Update ball location
		ball.update();
		
		// Update player information
		if (leftPlayerConnected) {
			leftPlayer.updatePaddlePosition();
		}
		if (rightPlayerConnected) {
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
}
