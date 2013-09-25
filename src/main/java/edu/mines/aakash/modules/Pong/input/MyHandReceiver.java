package edu.mines.aakash.modules.Pong.input;

import edu.mines.aakash.modules.Pong.Pong;
import edu.mines.aakash.modules.Pong.players.HumanPlayer;
import edu.mines.aakash.modules.Pong.players.KinectHumanPlayer;
import edu.mines.acmX.exhibit.stdlib.graphics.Coordinate3D;
import edu.mines.acmX.exhibit.stdlib.graphics.HandPosition;
import edu.mines.acmX.exhibit.stdlib.input_processing.receivers.HandReceiver;

public class MyHandReceiver extends HandReceiver {
	private Pong game;
	
	private int leftHandID;
	private Coordinate3D leftPosition;
	
	private int rightHandID;
	private Coordinate3D rightPosition;
	
	
	
	public MyHandReceiver(Pong instance) {
		leftHandID = -1;
		rightHandID = -1;
		this.game = instance;
	}
	
	public void handCreated(HandPosition handPos) {
		// TODO Scale the position
		if (!game.isLeftPlayerConnected()) {
			leftHandID = handPos.getId();
			leftPosition = handPos.getPosition();
			game.leftPlayerConnected(true);
			game.createLeftPlayer();
			
		} else if (!game.isRightPlayerConnected()) {
			rightHandID = handPos.getId();
			rightPosition = handPos.getPosition();
			game.rightPlayerConnected(true);
			game.createRightPlayer();
		}
		
		// If both are connected, begin the game.
		if (game.isLeftPlayerConnected() && game.isRightPlayerConnected()) {
			game.initGame();
		}
	}
	
	public void handDestroyed(int id) {
		// TODO handle when a player disconnects
		if (id == leftHandID) {
			game.leftPlayerConnected(false);
		} else if (id == rightHandID) {
			game.rightPlayerConnected(false);
		}
		// TODO check if we need to end game
	}
	
	@Override
	public void handUpdated(HandPosition pos) {
		int id = pos.getId();
		if (id == leftHandID) {
			leftPosition = pos.getPosition(); 
		} else if (id == rightHandID) {
			rightPosition = pos.getPosition();
		}
	}
	
	public int getLeftHandID() {
		return leftHandID;
	}
	
	public int getRightHandID() {
		return rightHandID;
	}
	
	public Coordinate3D getHandPosition(int id) {
		if (id == leftHandID) {
			return leftPosition;
		} else if (id == rightHandID) {
			return rightPosition;
		}
		return null;
	}
	
	public Coordinate3D getLeftHandPosition() {
		return leftPosition;
	}
	
	public Coordinate3D getRightHandPosition() {
		return rightPosition;
	}
}
