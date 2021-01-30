package com.ansdoship.golly.game;

public class Land {

	private final int width;
	private final int height;

	private Cell[][] cellMap;
	private int dayCount;
	private int aliveCellCount;

	public static final double ALIVE_PROBABILITY_DEFAULT = 0.5;
	public static final double ALIVE_PROBABILITY_ALL_DEAD = 0;
	public static final double ALIVE_PROBABILITY_ALL_ALIVE = 1;

	public Land (int size) {
		this(size, size);
	}

	public Land (int width, int height) {
		this(width, height, ALIVE_PROBABILITY_DEFAULT);
	}

	public Land (int size, double aliveProbability) {
		this(size, size, aliveProbability);
	}

	public Land (int width, int height, double aliveProbability) {
		if (width < 0) {
			throw new IllegalArgumentException("Width must be > 0");
		}
		if (height < 0) {
			throw new IllegalArgumentException("Height must be > 0");
		}
		this.width = width;
		this.height = height;
		cellMap = new Cell[width][height];
		dayCount = 0;
		init(aliveProbability);
	}

	public void init () {
		init(ALIVE_PROBABILITY_DEFAULT);
	}

	public synchronized void init (double aliveProbability) {
		aliveCellCount = 0;
		for (int x = 0; x < width; x ++) {
			for (int y = 0; y < height; y ++) {
				Cell cell = new Cell(deadOrAlive(aliveProbability), 0xFF000000);
				aliveCellCount += cell.getState();
				cellMap[x][y] = cell;
			}
		}
	}

	public synchronized void invalidate () {
		dayCount ++;
		Land stepLand = new Land(width, height, ALIVE_PROBABILITY_ALL_DEAD);
		for (int x = 0; x < width; x ++) {
			for (int y = 0; y < height; y ++) {
				int n = getPosCellCount(x, y);
				if (isCellAlive(x, y)) {
					if (n < 2 || n > 3) {
						stepLand.setCellDie(x, y);
					}
					else {
						stepLand.setCellAlive(x, y);
					}
				}
				else {
					if (stepLand.isCellDead(x, y)) {
						if (n == 3) {
							stepLand.setCellAlive(x, y);
						}
					}
				}
			}
		}
		this.cellMap = stepLand.cellMap;
		this.aliveCellCount = stepLand.aliveCellCount;
	}

	public void clear () {
		init(ALIVE_PROBABILITY_ALL_DEAD);
	}

	public int getPosCellCount(int posX, int posY) {
		int count = 0;
		for (int x = posX - 1; x <= posX + 1; x ++) {
			for (int y = posY - 1; y <= posY + 1; y ++) {
				if (x >= 0 && x < width && y >= 0 && y < height) {
					if (x == posX && y == posY) {
						count += 0;
					}
					else {
						count += getCellState(x, y);
					}
				}
			}
		}
		return count;
	}

	private byte getCellState(int posX, int posY) {
		return cellMap[posX][posY].getState();
	}

	public boolean isCellAlive(int posX, int posY) {
		return cellMap[posX][posY].getState() == Cell.STATE_ALIVE;
	}

	public boolean isCellDead(int posX, int posY) {
		return cellMap[posX][posY].getState() == Cell.STATE_DEAD;
	}

	public void setCellAlive(int posX, int posY) {
		cellMap[posX][posY].alive();
		aliveCellCount ++;
	}

	public void setCellDie(int posX, int posY) {
		cellMap[posX][posY].die();
		aliveCellCount --;
	}

	public int getCellColor(int posX, int posY) {
		return cellMap[posX][posY].getColor();
	}

	public void setCellColor(int posX, int posY, int color) {
		cellMap[posX][posY].setColor(color);
	}

	private byte deadOrAlive (double aliveProbability) {
		if (aliveProbability < 0 || aliveProbability > 1) {
			throw new IllegalArgumentException("Invalid probability, the range is 0 <= probability <= 1");
		}
		if (Math.random() < aliveProbability) {
			return Cell.STATE_ALIVE;
		}
		else {
			return Cell.STATE_DEAD;
		}
	}

	@Deprecated
	public int countAliveCell() {
		int aliveCellCount = 0;
		for (int x = 0; x < width; x ++) {
			for (int y = 0; y < height; y ++) {
				aliveCellCount += getCellState(x, y);
			}
		}
		return aliveCellCount;
	}

	public int getCellCount() {
		return getWidth() * getHeight();
	}

	public int getAliveCellCount() {
		return aliveCellCount;
	}

	public int getDeadCellCount() {
		return getCellCount() - getAliveCellCount();
	}

	public int getDayCount() {
		return dayCount;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void reset() {
		init();
		dayCount = 0;
	}

	public void reset(double aliveProbability) {
		init(aliveProbability);
		dayCount = 0;
	}

}
