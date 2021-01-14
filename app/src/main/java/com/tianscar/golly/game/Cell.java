package com.tianscar.golly.game;

public class Cell {

	public static final byte STATE_DEAD = 0;
	public static final byte STATE_ALIVE = 1;
	
	private byte state;
	
	public Cell (byte state){
		if (state != STATE_DEAD && state != STATE_ALIVE) {
			throw new IllegalArgumentException("Invalid state: " + state);
		}
		this.state = state;
	}

	public void alive() {
		this.state = STATE_ALIVE;
	}

	public void die() {
		this.state = STATE_DEAD;
	}

	public byte getState() {
		return state;
	}

}
