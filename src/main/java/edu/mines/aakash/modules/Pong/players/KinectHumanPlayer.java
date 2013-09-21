package edu.mines.aakash.modules.Pong.players;
import edu.mines.aakash.modules.Pong.Ball;
import edu.mines.aakash.modules.Pong.Paddle;
import edu.mines.aakash.modules.Pong.input.MyHandReceiver;
import edu.mines.acmX.exhibit.stdlib.input_processing.tracking.HandTrackingUtilities;

public class KinectHumanPlayer extends HumanPlayer {

	private int trackingHeight, screenHeight;
	private float marginFraction;
	
	public KinectHumanPlayer(Paddle paddle, Ball ball, MyHandReceiver receiver, int handID, int trackingHeight, int screenHeight, float marginFraction) {
		super(paddle, ball, receiver, handID);
		this.trackingHeight = trackingHeight;
		this.screenHeight = screenHeight;
		this.marginFraction = marginFraction;
	}

	@Override
	public void updatePaddlePosition() {
		int newPosition = (int) receiver.getHandPosition(handID).getY(); 
		myPaddle.setY(HandTrackingUtilities.getScaledHandY(newPosition, trackingHeight, screenHeight, marginFraction));
	}

}
