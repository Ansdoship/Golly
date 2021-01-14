package com.tianscar.golly.ui;

import android.annotation.SuppressLint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.tianscar.golly.game.Cell;
import com.tianscar.golly.game.Land;
import com.tianscar.golly.util.FPSCounter;
import com.tianscar.golly.util.ScreenUtils;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private final SurfaceHolder mHolder;
    private volatile boolean isDraw;
    private volatile boolean threadActive;

	private final Land mLand;
	private int cellSize;
	private int cellColor;
	private int backgroundColor;

	private final FPSCounter fpsCounter;

	private OnInvalidateListener onInvalidateListener;

	public interface OnInvalidateListener {
		void onInvalidate();
	}

	public void setOnInvalidateListener(OnInvalidateListener onInvalidateListener) {
		this.onInvalidateListener = onInvalidateListener;
	}

	public OnInvalidateListener getOnInvalidateListener() {
		return onInvalidateListener;
	}

	public GameView(Context context) {
		this(context, null);
	}

	public GameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setFormat(PixelFormat.TRANSLUCENT);
		setZOrderOnTop(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);
		isDraw = false;
		threadActive = false;
		setCellSize(5);
		setCellColor(Color.BLACK);
		setBackgroundColor(Color.WHITE);
		fpsCounter = new FPSCounter();
		mLand = new Land(ScreenUtils.getScreenWidth() / cellSize,
				ScreenUtils.getScreenRealHeight() / cellSize / 2);
		mHolder.setFixedSize(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenRealHeight() / 2);
	}

	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
		drawLand();
		threadActive = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				fpsCounter.setNowFPS(System.nanoTime());
				while (threadActive) {
					if (isDraw) {
						mLand.invalidate();
						drawLand();
						if (onInvalidateListener != null) {
							onInvalidateListener.onInvalidate();
						}
						fpsCounter.countFPS();
					}
				}

			}
		}).start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
		stopGame();
		threadActive = false;
    }

    private void drawLand () {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
			Paint paint = new Paint();
			paint.setAntiAlias(false);
			paint.setStrokeWidth(cellSize * 0.5f);
			paint.setColor(cellColor);
			canvas.drawColor(backgroundColor);
			for(int x = 0; x < mLand.getWidth(); x ++){
				for(int y = 0; y < mLand.getHeight(); y ++){
					Cell cell = mLand.getCell(x, y);
					if (cell.getState() == Cell.STATE_ALIVE) {
						canvas.drawPoint((x + 0.5f) * cellSize,(y + 0.5f) * cellSize, paint);
					}
				}
			}
			mHolder.unlockCanvasAndPost(canvas);
		}
    }

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		int strokeSize = 3;
		int posX = (int)(event.getX() / cellSize);
		int posY = (int)(event.getY() / cellSize);
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				for (int x = posX - strokeSize; x <= posX + strokeSize; x ++) {
					for (int y = posY - strokeSize; y <= posY + strokeSize; y ++) {
						if (x >= 0 && x < mLand.getWidth() && y >= 0 && y < mLand.getHeight()) {
							mLand.getCell(x, y).alive();
						}
					}
				}
				break;
		}
		return true;
	}
	
	public void stopGame() {
		isDraw = false;
	}

	public void startGame() {
		isDraw = true;
	}

	public boolean isGameRendering() {
		return isDraw;
	}

	public void setGameRendering(boolean isRendering) {
		isDraw = isRendering;
	}

	public void setCellColor(int cellColor) {
		this.cellColor = cellColor;
	}

	public int getCellColor() {
		return cellColor;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

	public int getCellSize() {
		return cellSize;
	}

	@Override
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public Land getLand() {
		return mLand;
	}

	public int getFPS() {
		return (int) fpsCounter.getFPS();
	}

}
