package edu.mines.aakash.modules.Pong;

import java.awt.geom.Rectangle2D;

public class Ball {
	public static final int BALL_RADIUS = 25;
	
	private int x;
	private int y;
	private double velX;
	private double velY;
	
	public Ball(int x, int y) {
		this.x = x;
		this.y = y;
		
	}
	
	public Rectangle2D getRectangleRepresentation() {
		return new Rectangle2D.Double(x - BALL_RADIUS, y - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
	}
	
	public boolean isIntersectedWith(Rectangle2D other) {
		Rectangle2D ballRect = getRectangleRepresentation();
		Rectangle2D intersected = ballRect.createIntersection(other);
		if(intersected.isEmpty()){
			return false;
		} else {
			return true;
		}
	}
	
	public void update() {
		this.x += velX;
		this.y += velY;
	}
	
	public void setInitialVelocity(double velocityX, double d) {
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
