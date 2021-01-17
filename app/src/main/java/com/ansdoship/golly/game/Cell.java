package com.ansdoship.golly.game;

public class Cell {

	public static final byte STATE_DEAD = 0;
	public static final byte STATE_ALIVE = 1;
	
	private byte state;
	
	public Cell (byte state) {
		setState(state);
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

}
