package edu.mines.aakash.modules.Pong.players;

import edu.mines.aakash.modules.Pong.Ball;
import edu.mines.aakash.modules.Pong.Paddle;
import edu.mines.aakash.modules.Pong.input.MyHandReceiver;

public class HumanPlayer extends Player {
	protected MyHandReceiver receiver;
	protected int handID;
	protected int trackingHeight, screenHeight;
	protected float marginFraction;
	
	public HumanPlayer(Paddle paddle, Ball ball, int screenH, MyHandReceiver receiver, int handID) {
		super(paddle, ball, screenH);
		this.receiver = receiver;
		this.handID = handID;
	}

	@Override
	public void updatePaddlePosition() {
		int newPosition = (int) receiver.getHandPosition(handID).getY(); 
		myPaddle.setY(ensureWithinBounds(newPosition));
	}
	


}
