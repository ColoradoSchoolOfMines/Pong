package edu.mines.aakash.modules.Pong;

public class Paddle {
	public static final int PADDLE_WIDTH = 20;
	public static final int PADDLE_HEIGHT = 70;
	
	private int x;
	private int y;
	
	public Paddle(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setY(int val) {
		this.y = val;
		
	}
}
