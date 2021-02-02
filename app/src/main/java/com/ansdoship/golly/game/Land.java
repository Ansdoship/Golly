package com.ansdoship.golly.game;

import androidx.annotation.Nullable;

import com.ansdoship.golly.common.Settings;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Land {

	private final ReentrantLock modifyLandLock;

	private Random random;

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
		modifyLandLock = new ReentrantLock(true);
		this.width = width;
		this.height = height;
		cellMap = new Cell[width][height];
		dayCount = 0;
		init(aliveProbability);
	}

	public void init () {
		init(ALIVE_PROBABILITY_DEFAULT);
	}

	public void init (double aliveProbability) {
		modifyLandLock.lock();
		try {
			random = new Random();
			aliveCellCount = 0;
			for (int x = 0; x < width; x ++) {
				for (int y = 0; y < height; y ++) {
					Cell cell = new Cell(deadOrAlive(aliveProbability), Settings.getInstance().getPaletteColor());
					aliveCellCount += cell.getState();
					cellMap[x][y] = cell;
				}
			}
		}
		finally {
			modifyLandLock.unlock();
		}
	}

	public void iteration () {
		modifyLandLock.lock();
		try {
			dayCount ++;
			// Color iteration
			Cell[][] stepColorMap = new Cell[width][height];
			for (int x = 0; x < width; x ++) {
				for (int y = 0; y < height; y ++) {
					stepColorMap[x][y] = new Cell(getCellState(x, y), getStepCellColor(getCellColor(x, y), x, y));
				}
			}
			cellMap = stepColorMap;
			// Life iteration
			Cell[][] stepCellMap = new Cell[width][height];
			int stepAliveCellCount = 0;
			for (int x = 0; x < width; x ++) {
				for (int y = 0; y < height; y ++) {
					stepCellMap[x][y] = new Cell(Cell.STATE_DEAD, getCellColor(x, y));
				}
			}
			for (int x = 0; x < width; x ++) {
				for (int y = 0; y < height; y ++) {
					int n = getPosSameColorAliveCellCount(x, y);
					if (isCellAlive(x, y)) {
						if (n < 2 || n > 3) {
							stepCellMap[x][y].die();
							stepAliveCellCount --;
						}
						else {
							stepCellMap[x][y].alive();
							stepAliveCellCount ++;
						}
					}
					else {
						if (stepCellMap[x][y].getState() == Cell.STATE_DEAD) {
							if (n == 3) {
								stepCellMap[x][y].alive();
								stepAliveCellCount ++;
							}
						}
					}
				}
			}
			cellMap = stepCellMap;
			aliveCellCount = stepAliveCellCount;
		}
		finally {
			modifyLandLock.unlock();
		}
	}

	public void clear () {
		init(ALIVE_PROBABILITY_ALL_DEAD);
	}

	private @Nullable int[] getPosAliveCellColors(int posX, int posY) {
		int[] colors = new int[8];
		byte count = 0;
		for (int x = posX - 1; x <= posX + 1; x ++) {
			for (int y = posY - 1; y <= posY + 1; y ++) {
				if (x >= 0 && x < width && y >= 0 && y < height) {
					if (!(x == posX && y == posY)) {
						if (getCellState(x, y) == Cell.STATE_ALIVE) {
							colors[count] = getCellColor(x, y);
							count ++;
						}
					}
				}
			}
		}
		if (count < 1) {
			return null;
		}
		int[] result = new int[count];
		System.arraycopy(colors, 0, result, 0, count);
		return result;
	}

	private int getStepCellColor(int defaultCellColor, int posX, int posY) {
		int[] colors = getPosAliveCellColors(posX, posY);
		if (colors == null) {
			return defaultCellColor;
		}
		else {
			final Map<Integer, Integer> map = new HashMap<>();
			for (int key : colors) {
				if (map.containsKey(key)) {
					Integer count = map.get(key);
					if (count == null) {
						map.put(key, 1);
					}
					else {
						map.put(key, count + 1);
					}
				}
				else {
					map.put(key, 1);
				}
			}
			int count = 0;
			for (int key : colors) {
				Integer integer = map.get(key);
				if (integer != null) {
					if (count < integer) {
						count = integer;
					}
				}
			}
			for (int key : colors) {
				Integer integer = map.get(key);
				if (integer != null) {
					if (count > integer) {
						map.remove(key);
					}
				}
			}
			return (int) map.keySet().toArray()[random.nextInt(map.size())];
		}
	}

	private byte getPosSameColorAliveCellCount(int posX, int posY) {
		byte count = 0;
		int color = getCellColor(posX, posY);
		for (int x = posX - 1; x <= posX + 1; x ++) {
			for (int y = posY - 1; y <= posY + 1; y ++) {
				if (x >= 0 && x < width && y >= 0 && y < height) {
					if (!(x == posX && y == posY)) {
						if (getCellColor(x, y) == color && getCellState(x, y) == Cell.STATE_ALIVE) {
							count ++;
						}
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
		if (isCellAlive(posX, posY)) {
			return;
		}
		cellMap[posX][posY].alive();
		aliveCellCount ++;
	}

	public void setCellDie(int posX, int posY) {
		if (isCellDead(posX, posY)) {
			return;
		}
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
