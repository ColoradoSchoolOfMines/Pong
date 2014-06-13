package edu.mines.aakash.modules.Pong.logic;

import java.util.TimerTask;

import edu.mines.aakash.modules.Pong.Pong;

public class Restart extends TimerTask {
	private Pong object;
	
	public Restart(Pong object) {
		this.object = object;
	}
	
	@Override
	public void run() {
		System.out.println(new Throwable().getStackTrace()[0]);
		if( object.isLeftPlayerConnected() && object.isRightPlayerConnected()) {
			object.initGame();
		} else {
			object.getDriver().clearAllHands();
			object.destroy();
			System.out.println("destroy");
		}
	}

}
