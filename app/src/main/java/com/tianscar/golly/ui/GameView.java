package com.tianscar.golly.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
    private volatile boolean scaleMode;

    private final int VIEW_WIDTH;
    private final int VIEW_HEIGHT;
	private Land mLand;
	private float landTranslationX;
	private float landTranslationY;
	private int cellStrokeSize;
	private int cellSize;
	private int cellColor;
	private int landBackgroundColor;
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
		scaleMode = false;
		cellPaint = new Paint();
		cellPaint.setAntiAlias(false);
		setLandSize(ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenRealHeight() / 4);
		setCellStrokeSize(3);
		setCellSize(1);
		setCellColor(Color.BLACK);
		setLandBackgroundColor(Color.WHITE);
		fpsCounter = new FPSCounter();
		setDrawScale(1.0f);
		VIEW_WIDTH = ScreenUtils.getScreenWidth();
		VIEW_HEIGHT = ScreenUtils.getScreenRealHeight() / 2;
		mHolder.setFixedSize(VIEW_WIDTH, VIEW_HEIGHT);
		resetLandTranslationX();
		resetLandTranslationY();
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
		matrix.setTranslate(getLandTranslationX(), getLandTranslationY());
		matrix.postScale(drawScale, drawScale);
        if (mCanvas != null) {
        	mCanvas.drawPaint(eraser);
        	cacheCanvas.drawPaint(eraser);
			cacheCanvas.drawColor(landBackgroundColor);
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

	private double scaleModeTouchDistRecord;
	private float scaleModeRecordX;
	private float scaleModeRecordY;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		if (scaleMode) {
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					scaleModeRecordX = event.getX(0);
					scaleModeRecordY = event.getY(0);
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					scaleModeTouchDistRecord = spacing(event);
					break;
				case MotionEvent.ACTION_MOVE:
					double newTouchDist = spacing(event);
					if(newTouchDist != 0) {
						double touchDist = newTouchDist - scaleModeTouchDistRecord;
						addDrawScale((float) (0.005 * touchDist));
						scaleModeTouchDistRecord = newTouchDist;
					}
					addLandTranslationX(event.getX(0) - scaleModeRecordX);
					addLandTranslationY(event.getY(0) - scaleModeRecordY);
					scaleModeRecordX = event.getX(0);
					scaleModeRecordY = event.getY(0);
					break;
			}
		}
		else {
			int posX = (int)(event.getX() / cellSize / drawScale - getLandTranslationX());
			int posY = (int)(event.getY() / cellSize / drawScale - getLandTranslationY());
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					addCellStroke(posX, posY);
					break;
			}
		}
		if (!isDraw) {
			drawLand();
		}
		return true;
	}

	private static double spacing(@NonNull MotionEvent event) {
		if(event.getPointerCount() >= 2) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return Math.pow(x * x + y * y, 0.5);
		}
		return 0;
	}

	private void addCellStroke(int posX, int posY) {
		for (int x = posX - getCellStrokeSize(); x <= posX + getCellStrokeSize(); x ++) {
			for (int y = posY - getCellStrokeSize(); y <= posY + getCellStrokeSize(); y ++) {
				if (x >= 0 && x < mLand.getWidth() && y >= 0 && y < mLand.getHeight()) {
					mLand.getCell(x, y).alive();
				}
			}
		}
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

	public void setLandBackgroundColor(int landBackgroundColor) {
		this.landBackgroundColor = landBackgroundColor;
	}

	public int getLandBackgroundColor() {
		return landBackgroundColor;
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
		reset(Land.ALIVE_PROBABILITY_DEFAULT);
	}

	public void reset(double aliveProbability) {
		mLand.reset(aliveProbability);
		if (!isDraw) {
			drawLand();
		}
	}

	public void setDrawScale(float drawScale) {
		addDrawScale(drawScale - this.drawScale);
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
		if (!isDraw) {
			drawLand();
		}
		if (onDrawScaleChangeListener != null) {
			onDrawScaleChangeListener.onDrawScaleChange(drawScale);
		}
	}

	public void setScaleMode(boolean scaleMode) {
		this.scaleMode = scaleMode;
	}

	public boolean isScaleMode() {
		return scaleMode;
	}

	public void setCellStrokeSize(int cellStrokeSize) {
		this.cellStrokeSize = cellStrokeSize;
	}

	public int getCellStrokeSize() {
		return cellStrokeSize;
	}

	public void setLandTranslationX(float landTranslationX) {
		this.landTranslationX = landTranslationX;
	}

	public void setLandTranslationY(float landTranslationY) {
		this.landTranslationY = landTranslationY;
	}

	public float getLandTranslationX() {
		return landTranslationX;
	}

	public float getLandTranslationY() {
		return landTranslationY;
	}

	public void resetLandTranslationX() {
		landTranslationX = (getViewWidth() - getLand().getWidth()) * 0.5f;
	}

	public void resetLandTranslationY() {
		landTranslationY = (getViewHeight() - getLand().getHeight()) * 0.5f;
	}

	public void addLandTranslationX(float value) {
		landTranslationX += value;
	}

	public void addLandTranslationY(float value) {
		landTranslationY += value;
	}

	public int getViewWidth() {
		return VIEW_WIDTH;
	}

	public int getViewHeight() {
		return VIEW_HEIGHT;
	}

}
