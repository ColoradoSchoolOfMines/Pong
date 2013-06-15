package edu.mines.aakash.modules;

public abstract class Player {
	
	private Paddle myPaddle;
	private Ball theBall;
	
	public Player(Paddle paddle, Ball ball) {
		this.myPaddle = paddle;
		this.theBall = ball;
	}
	
	public abstract void updatePaddlePosition();
}
