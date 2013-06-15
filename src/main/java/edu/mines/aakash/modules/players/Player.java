package edu.mines.aakash.modules.players;

import edu.mines.aakash.modules.Ball;
import edu.mines.aakash.modules.Paddle;

public abstract class Player {
	
	protected Paddle myPaddle;
	protected Ball theBall;
	
	public Player(Paddle paddle, Ball ball) {
		this.myPaddle = paddle;
		this.theBall = ball;
	}
	
	public abstract void updatePaddlePosition();
}
