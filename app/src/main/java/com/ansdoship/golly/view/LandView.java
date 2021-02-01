package com.ansdoship.golly.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import androidx.core.math.MathUtils;

import com.ansdoship.golly.common.Settings;
import com.ansdoship.golly.game.Land;
import com.ansdoship.golly.util.DensityUtils;
import com.ansdoship.golly.util.DrawUtils;
import com.ansdoship.golly.util.ScreenUtils;

public class LandView extends SurfaceView implements SurfaceHolder.Callback {

	public final int LAND_WIDTH = 256;
	public final int LAND_HEIGHT = 256;

	public final float DRAW_SCALE_MIN = 1.0f;
	public final float DRAW_SCALE_MAX = 20.0f;

	private int drawFps;
	private long drawTime;
	private int drawSum;
	private int iterationLandFps;
	private long iterationLandTime;
	private int iterationLandSum;

    private final SurfaceHolder mHolder;
    private volatile boolean isLandIteration;
    private volatile boolean threadActive;
    private volatile boolean scaleMode;

    private final int VIEW_WIDTH;
    private final int VIEW_HEIGHT;
	private Land mLand;
	private float landTranslationX;
	private float landTranslationY;
	private int cellStrokeSize;
	private int cellSize;
	private int landBackgroundColor;
	private final Paint cellPaint;
	private final Bitmap cacheBitmap;
	private final Canvas cacheCanvas;

	private float drawScale;

	private OnDrawLandListener onDrawLandListener;

	public interface OnDrawLandListener {
		void onDrawLand();
	}

	private OnDrawScaleChangeListener onDrawScaleChangeListener;

	public interface OnDrawScaleChangeListener {
		void onDrawScaleChange(float newScale);
	}

	public void setOnDrawLandListener(OnDrawLandListener onDrawLandListener) {
		this.onDrawLandListener = onDrawLandListener;
	}

	public OnDrawLandListener getOnDrawLandListener() {
		return onDrawLandListener;
	}

	public void setOnDrawScaleChangeListener(OnDrawScaleChangeListener onDrawScaleChangeListener) {
		this.onDrawScaleChangeListener = onDrawScaleChangeListener;
	}

	public OnDrawScaleChangeListener getOnDrawScaleChangeListener() {
		return onDrawScaleChangeListener;
	}

	public LandView(Context context) {
		this(context, null);
	}

	public LandView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LandView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setFormat(PixelFormat.TRANSLUCENT);
		setZOrderOnTop(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);
		isLandIteration = false;
		threadActive = false;
		scaleMode = false;
		cellPaint = new Paint();
		cellPaint.setAntiAlias(false);
		setLandSize(LAND_WIDTH, LAND_HEIGHT);
		cacheBitmap = Bitmap.createBitmap(mLand.getWidth(), mLand.getHeight(), Bitmap.Config.ARGB_8888);
		cacheCanvas = new Canvas(cacheBitmap);
		setCellStrokeSize(3);
		setCellSize(1);
		setLandBackgroundColor(Color.WHITE);
		VIEW_WIDTH = ScreenUtils.getScreenRealWidth();
		VIEW_HEIGHT = ScreenUtils.getScreenRealHeight() / 2;
		mHolder.setFixedSize(VIEW_WIDTH, VIEW_HEIGHT);
		setDrawScale(1.0f);
		resetLandTranslationX();
		resetLandTranslationY();
		drawFps = 0;
		iterationLandFps = 0;
		drawTime = 0;
		iterationLandTime = 0;
		drawSum = 0;
		iterationLandSum = 0;
	}

	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
		threadActive = true;
		new Thread(() -> {
			while (threadActive) {
				drawLand();
				if (System.currentTimeMillis() - drawTime >= 1000) {
					drawFps = drawSum + 1;
					drawSum = 0;
					drawTime = System.currentTimeMillis();
				}
				drawSum ++;
			}
		}).start();
		new Thread(() -> {
			while (threadActive) {
				if (isLandIteration) {
					mLand.iteration();
					if (System.currentTimeMillis() - iterationLandTime >= 1000) {
						iterationLandFps = iterationLandSum + 1;
						iterationLandSum = 0;
						iterationLandTime = System.currentTimeMillis();
					}
					iterationLandSum ++;
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
		if (mLand == null) {
			return;
		}
        Canvas mCanvas = mHolder.lockCanvas();
		Matrix matrix = new Matrix();
		matrix.setTranslate(getLandTranslationX() / drawScale, getLandTranslationY() / drawScale);
		matrix.postScale(drawScale, drawScale);
        if (mCanvas != null) {
        	synchronized (mHolder) {
				mCanvas.drawPaint(DrawUtils.getEraser());
				cacheCanvas.drawPaint(DrawUtils.getEraser());
				cacheCanvas.drawColor(landBackgroundColor);
				for(int x = 0; x < mLand.getWidth(); x ++) {
					for(int y = 0; y < mLand.getHeight(); y ++) {
						if (mLand.isCellAlive(x, y)) {
							cellPaint.setColor(mLand.getCellColor(x, y));
							cacheCanvas.drawPoint((x + 0.5f) * cellSize,(y + 0.5f) * cellSize, cellPaint);
						}
					}
				}
				mCanvas.drawBitmap(cacheBitmap, matrix, DrawUtils.getBitmapPaint());
			}
			mHolder.unlockCanvasAndPost(mCanvas);
			if (onDrawLandListener != null) {
				onDrawLandListener.onDrawLand();
			}
		}
    }

	private double scaleModeTouchDistRecord;
	private float scaleModeRecordX;
	private float scaleModeRecordY;
	private boolean pointer0Changed;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		if (scaleMode) {
			if (pointer0Changed) {
				scaleModeRecordX = event.getX(0);
				scaleModeRecordY = event.getY(0);
			}
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					pointer0Changed = true;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					if (event.getPointerCount() == 2) {
						pointer0Changed = true;
					}
					scaleModeTouchDistRecord = spacing(event);
					break;
				case MotionEvent.ACTION_POINTER_UP:
					if (event.getPointerCount() == 2) {
						pointer0Changed = true;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					double newTouchDist = spacing(event);
					if(newTouchDist != 0) {
						double touchDist = DensityUtils.px2dp(newTouchDist - scaleModeTouchDistRecord);
						if (touchDist > 1 || touchDist < -1) {
							addDrawScale((float) (0.005 * touchDist));
							scaleModeTouchDistRecord = newTouchDist;
						}
					}
					addLandTranslationX(event.getX(0) - scaleModeRecordX);
					addLandTranslationY(event.getY(0) - scaleModeRecordY);
					scaleModeRecordX = event.getX(0);
					scaleModeRecordY = event.getY(0);
					pointer0Changed = false;
					break;
			}
		}
		else {
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
				case MotionEvent.ACTION_MOVE:
					for (int i = 0; i < event.getPointerCount(); i ++) {
						int posX = (int)((event.getX(i) / cellSize - getLandTranslationX()) / drawScale);
						int posY = (int)((event.getY(i) / cellSize - getLandTranslationY()) / drawScale);
						addCellStroke(posX, posY);
					}
					break;
			}
		}
		return true;
	}

	private static double spacing(@NonNull MotionEvent event) {
		if(event.getPointerCount() >= 2) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return Math.pow(Math.pow(x, 2) + Math.pow(y, 2), 0.5);
		}
		return 0;
	}

	public synchronized void addCellStroke(int posX, int posY) {
		for (int x = posX - getCellStrokeSize(); x <= posX + getCellStrokeSize(); x ++) {
			for (int y = posY - getCellStrokeSize(); y <= posY + getCellStrokeSize(); y ++) {
				if (x >= 0 && x < mLand.getWidth() && y >= 0 && y < mLand.getHeight()) {
					mLand.setCellColor(x, y, Settings.getInstance().getPaletteColor());
					mLand.setCellAlive(x, y);
				}
			}
		}
	}

	public synchronized void clearCellStroke(int posX, int posY) {
		for (int x = posX - getCellStrokeSize(); x <= posX + getCellStrokeSize(); x ++) {
			for (int y = posY - getCellStrokeSize(); y <= posY + getCellStrokeSize(); y ++) {
				if (x >= 0 && x < mLand.getWidth() && y >= 0 && y < mLand.getHeight()) {
					mLand.setCellDie(x, y);
				}
			}
		}
	}
	
	public void stopGame() {
		isLandIteration = false;
	}

	public void startGame() {
		isLandIteration = true;
	}

	public boolean isLandIteration() {
		return isLandIteration;
	}

	public void setLandIteration(boolean landIteration) {
		isLandIteration = landIteration;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
		cellPaint.setStrokeWidth(cellSize);
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

	public int getDrawFps() {
		return drawFps;
	}

	public int getIterationLandFps() {
		return iterationLandFps;
	}

	public void clear() {
		mLand.clear();
	}

	public void reset() {
		reset(Land.ALIVE_PROBABILITY_DEFAULT);
	}

	public void reset(double aliveProbability) {
		mLand.reset(aliveProbability);
	}

	public void setDrawScale(float drawScale) {
		float newScale = MathUtils.clamp(drawScale, DRAW_SCALE_MIN, DRAW_SCALE_MAX);
		float offset = this.drawScale - newScale;
		this.drawScale = newScale;
		addLandTranslationX(offset * getLand().getWidth() * 0.5f);
		addLandTranslationY(offset * getLand().getHeight() * 0.5f);
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
		setDrawScale(drawScale + value);
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
		landTranslationX = (getHolderWidth() - getLand().getWidth()) * 0.5f;
		addLandTranslationX((1.0f - drawScale) * getLand().getWidth() * 0.5f);

	}

	public void resetLandTranslationY() {
		landTranslationY = (getHolderHeight() - getLand().getHeight()) * 0.5f;
		addLandTranslationY((1.0f - drawScale) * getLand().getHeight() * 0.5f);
	}

	public void addLandTranslationX(float value) {
		landTranslationX += value;
	}

	public void addLandTranslationY(float value) {
		landTranslationY += value;
	}

	public int getHolderWidth() {
		return VIEW_WIDTH;
	}

	public int getHolderHeight() {
		return VIEW_HEIGHT;
	}

	public void release() {
		if (cacheBitmap != null) {
			if (cacheBitmap.isRecycled()) {
				cacheBitmap.recycle();
			}
		}
	}

}
