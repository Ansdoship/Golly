package com.tianscar.golly.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;

import com.tianscar.golly.game.Cell;
import com.tianscar.golly.game.Land;
import com.tianscar.golly.util.FPSCounter;
import com.tianscar.golly.util.ScreenUtils;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private final SurfaceHolder mHolder;
    private volatile boolean isDraw;
    private volatile boolean threadActive;

	private Land mLand;
	private int cellSize;
	private int cellColor;
	private int backgroundColor;
	private final Paint cellPaint;
	private final static Paint eraser;
	static {
		eraser = new Paint();
		eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}
	private final static Paint bitmapPaint;
	static {
		bitmapPaint = new Paint();
	}
	private Bitmap cacheBitmap;
	private Canvas cacheCanvas;

	private float drawScale;

	private final FPSCounter fpsCounter;

	private OnInvalidateListener onInvalidateListener;

	public interface OnInvalidateListener {
		void onInvalidate();
	}

	private OnDrawScaleChangeListener onDrawScaleChangeListener;

	public interface OnDrawScaleChangeListener {
		void onDrawScaleChange(float newScale);
	}

	public void setOnInvalidateListener(OnInvalidateListener onInvalidateListener) {
		this.onInvalidateListener = onInvalidateListener;
	}

	public OnInvalidateListener getOnInvalidateListener() {
		return onInvalidateListener;
	}

	public void setOnDrawScaleChangeListener(OnDrawScaleChangeListener onDrawScaleChangeListener) {
		this.onDrawScaleChangeListener = onDrawScaleChangeListener;
	}

	public OnDrawScaleChangeListener getOnDrawScaleChangeListener() {
		return onDrawScaleChangeListener;
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
		cellPaint = new Paint();
		cellPaint.setAntiAlias(false);
		setLandSize(ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenRealHeight() / 4);
		setCellSize(5);
		setCellColor(Color.BLACK);
		setBackgroundColor(Color.WHITE);
		fpsCounter = new FPSCounter();
		setDrawScale(2.0f);
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
        Canvas mCanvas = mHolder.lockCanvas();
		Matrix matrix = new Matrix();
		matrix.setScale(drawScale, drawScale);
        if (mCanvas != null) {
        	mCanvas.drawPaint(eraser);
        	cacheCanvas.drawPaint(eraser);
			cacheCanvas.drawColor(backgroundColor);
			for(int x = 0; x < mLand.getWidth(); x ++) {
				for(int y = 0; y < mLand.getHeight(); y ++) {
					Cell cell = mLand.getCell(x, y);
					if (cell.getState() == Cell.STATE_ALIVE) {
						cacheCanvas.drawPoint((x + 0.5f) * cellSize,(y + 0.5f) * cellSize, cellPaint);
					}
				}
			}
			mCanvas.drawBitmap(cacheBitmap, matrix, bitmapPaint);
			mHolder.unlockCanvasAndPost(mCanvas);
		}
    }

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		int strokeSize = 3;
		int posX = (int)(event.getX() / cellSize / drawScale);
		int posY = (int)(event.getY() / cellSize / drawScale);
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
		cellPaint.setColor(cellColor);
	}

	public int getCellColor() {
		return cellColor;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
		cellPaint.setStrokeWidth(cellSize);
		cacheBitmap = Bitmap.createBitmap(mLand.getWidth(), mLand.getHeight(), Bitmap.Config.ARGB_8888);
		cacheCanvas = new Canvas(cacheBitmap);
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

	public void clear() {
		mLand.clear();
		if (!isDraw) {
			drawLand();
		}
	}

	public void reset() {
		mLand.reset();
		if (!isDraw) {
			drawLand();
		}
	}

	public void setDrawScale(float drawScale) {
		this.drawScale = MathUtils.clamp(drawScale, 1.0f, 10.0f);
		if (onDrawScaleChangeListener != null) {
			onDrawScaleChangeListener.onDrawScaleChange(drawScale);
		}
	}

	public float getDrawScale() {
		return drawScale;
	}

	public void setLandSize(int width, int height) {
		mLand = new Land(width, height);
	}

	public void addDrawScale(float value) {
		drawScale += value;
		drawScale = MathUtils.clamp(drawScale, 1.0f, 10.0f);
		if (onDrawScaleChangeListener != null) {
			onDrawScaleChangeListener.onDrawScaleChange(drawScale);
		}
	}

}
