package edu.mines.aakash.modules.Pong.players;

import edu.mines.aakash.modules.Pong.Ball;
import edu.mines.aakash.modules.Pong.Paddle;
import edu.mines.aakash.modules.Pong.input.MyHandReceiver;

public class HumanPlayer extends Player {
	private MyHandReceiver receiver;
	private int handID;
	
	public HumanPlayer(Paddle paddle, Ball ball, MyHandReceiver receiver, int handID) {
		super(paddle, ball);
		this.receiver = receiver;
		this.handID = handID;
	}

	@Override
	public void updatePaddlePosition() {
		int newPosition = (int) receiver.getHandPosition(handID).getY(); 
		myPaddle.setY(newPosition);
	}

}
