package com.tianscar.golly.game;

public class SnakeNode
{
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = -1;
	public static final int DIRECTION_LEFT = 2;
	public static final int DIRECTION_RIGHT = -2;
	
	public int direction;
	public int posX;
	public int posY;
	
	SnakeNode(int x,int y,int d){
		this.posX = x;
		this.posY = y;
		this.direction = d;
	}
	
	public boolean isSamePositionAsCell(int i,int j){
		if(posX==i&&posY==j){
			return true;
		}
		else return false;
	}
	
}
