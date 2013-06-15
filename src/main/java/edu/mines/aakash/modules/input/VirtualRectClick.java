package edu.mines.aakash.modules.input;

public class VirtualRectClick extends VirtualClick {
	private int x;
	private int y;
	private int width;
	private int height;
	
	private boolean inRegion = false;
	
	public VirtualRectClick(int duration,int x, int y, int width, int height) {
		super(duration);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void update(int mouseX, int mouseY, int millis) {
		if (mouseX > x && mouseX < (x + width) &&
			mouseY > y && mouseY < (y + height)) {
			if (!inRegion) {
				inRegion = true;
				this.startMillis = millis;
			}
		} else {
			inRegion = false;
		}
	
	}

	@Override
	public boolean durationCompleted(int millis) {
		if (inRegion &&
				(millis - startMillis) >= duration) {
			
			this.startMillis = millis;
			return true;
		}
		return false;
	}

	public void updateCoordinates(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
