package edu.mines.aakash.modules.Pong;

public class Ball {
	public static final int BALL_RADIUS = 25;
	
	private int x;
	private int y;
	private int velX;
	private double velY;
	
	public Ball(int x, int y) {
		this.x = x;
		this.y = y;
		
	}
	
	public void update() {
		this.x += velX;
		this.y += velY;
	}
	
	public void setInitialVelocity(int velocityX, double d) {
		this.velX = velocityX;
		this.velY = d;
	}
	
	public void setX(int val) {
		this.x = val;
	}
	
	public void setY(int val) {
		this.y = val;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void reverseVelocityX() {
		this.velX *= -1;
	}
	
	public void reverseVelocityY() {
		this.velY *= -1;
	}
}
