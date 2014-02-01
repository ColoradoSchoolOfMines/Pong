package edu.mines.aakash.modules.Pong.logic;

import edu.mines.aakash.modules.Pong.Pong;

import java.util.TimerTask;

public class ExitTask extends TimerTask {
	private Pong object;

	public ExitTask(Pong object) {
		this.object = object;
	}

	@Override
	public void run() {
		if( !object.isLeftPlayerConnected() && !object.isRightPlayerConnected())
			object.exit();
	}
}
