package edu.mines.aakash.modules.Pong.players;

import edu.mines.aakash.modules.Pong.Ball;
import edu.mines.aakash.modules.Pong.Paddle;
import edu.mines.aakash.modules.Pong.input.MyHandReceiver;
import edu.mines.acmX.exhibit.stdlib.input_processing.tracking.HandTrackingUtilities;

public class HumanPlayer extends Player {
	protected MyHandReceiver receiver;
	protected int handID;
	protected int trackingHeight, screenHeight;
	protected float marginFraction;
	
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
