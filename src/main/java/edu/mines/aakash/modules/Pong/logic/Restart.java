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
		object.initGame();
	}

}
