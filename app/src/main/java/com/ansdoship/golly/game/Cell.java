package com.ansdoship.golly.game;

class Cell {

	public static final byte STATE_DEAD = 0;
	public static final byte STATE_ALIVE = 1;
	
	private byte state;
	private int color;
	
	public Cell (byte state, int color) {
		setState(state);
		setColor(color);
	}

	public void alive() {
		this.state = STATE_ALIVE;
	}

	public void die() {
		this.state = STATE_DEAD;
	}

	public void setState(byte state) {
		switch (state) {
			case STATE_DEAD:
				die();
				break;
			case STATE_ALIVE:
				alive();
				break;
			default:
				throw new IllegalArgumentException("Invalid state: " + state);
		}
	}

	public byte getState() {
		return state;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

}
