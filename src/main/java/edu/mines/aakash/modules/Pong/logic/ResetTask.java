package edu.mines.aakash.modules.Pong.logic;

import edu.mines.aakash.modules.Pong.Pong;

import java.util.TimerTask;

public class ResetTask extends TimerTask {
	private Pong object;

	public ResetTask(Pong object) {
		this.object = object;
	}

	@Override
	public void run() {
		object.resetBall();
	}
}
