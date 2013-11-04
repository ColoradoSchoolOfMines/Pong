package edu.mines.aakash.modules.Pong;

import java.awt.Rectangle;

public class Paddle extends Rectangle {
	public static final int PADDLE_WIDTH = 20;
	public static final int PADDLE_HEIGHT = 140;
	
	public Paddle(int x, int y) {
		super(x,y,PADDLE_WIDTH,PADDLE_HEIGHT);
	}
	
	public void setY(int val) {
		setLocation((int)getX(), val);
	}
	
	public void setX(int val) {
		setLocation(val, (int)getY());
	}
	
	public void setY(double val) {
		setLocation((int)getX(), (int) val);
	}
	
	public void setX(double val) {
		setLocation((int) val, (int)getY());
	}

}
