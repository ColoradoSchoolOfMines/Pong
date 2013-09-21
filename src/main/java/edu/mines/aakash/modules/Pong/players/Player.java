package edu.mines.aakash.modules.Pong.players;

import edu.mines.aakash.modules.Pong.Ball;
import edu.mines.aakash.modules.Pong.Paddle;

public abstract class Player {
	
	protected Paddle myPaddle;
	protected Ball theBall;
	protected int screenH;
	
	public Player(Paddle paddle, Ball ball, int screenH) {
		this.myPaddle = paddle;
		this.theBall = ball;
		this.screenH = screenH;
	}
	
	public abstract void updatePaddlePosition();
	
	// Returns a position that ensures the paddle is within the screen bounds
	protected int ensureWithinBounds(int position) {
		if( position < 0 ) {
			return 0;
		} else if ( position + Paddle.PADDLE_HEIGHT > screenH ) {
			return screenH - Paddle.PADDLE_HEIGHT;
		} else {
			return position;
		}
	}
}
