package edu.mines.aakash.modules.Pong.players;

import edu.mines.aakash.modules.Pong.Ball;
import edu.mines.aakash.modules.Pong.Paddle;

public abstract class Player {
	
	protected Paddle myPaddle;
	protected Ball theBall;
	
	public Player(Paddle paddle, Ball ball) {
		this.myPaddle = paddle;
		this.theBall = ball;
	}
	
	public abstract void updatePaddlePosition();
}
