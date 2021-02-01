package com.ansdoship.golly.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.math.MathUtils;

import com.ansdoship.golly.common.Settings;
import com.ansdoship.golly.game.Land;
import com.ansdoship.golly.util.ActivityUtils;
import com.ansdoship.golly.util.SoftKeyBoardStateChangeObserver;
import com.ansdoship.golly.view.LandView;
import com.ansdoship.golly.R;

import java.text.DecimalFormat;

public class MainActivity extends BaseActivity {
	
	private float DEFAULT_DRAW_SCALE;

	private FrameLayout flContainer;
	private LandView mGameView;
	private Button btnStop;
	private Button btnStart;
	private Button btnClear;
	private Button btnReset;
	public TextView tvGameInfo;
	private EditText etCoordinateX;
	private EditText etCoordinateY;
	private Button btnAddCell;
	private Button btnDrawScale;
	private SeekBar barDrawScale;
	private Button btnAliveProbability;
	private SeekBar barAliveProbability;
	private ToggleButton tBtnScaleMode;
	private Button btnRecenterMap;
	private Button btnPalette;
	private RadioGroup rgPalette;

	private StringBuilder gameInfoBuilder;
	private boolean isLandIteration;
	private double aliveProbability;

	@SuppressLint("NonConstantResourceId")
	private final View.OnClickListener mOnClickListener = v -> {
		switch(v.getId()){
			case R.id.btn_stop:
				mGameView.stopGame();
				break;
			case R.id.btn_start:
				mGameView.startGame();
				break;
			case R.id.btn_clear:
				mGameView.clear();
				break;
			case R.id.btn_reset:
				mGameView.reset(aliveProbability);
				break;
			case R.id.btn_add_cell:
				String xStr = etCoordinateX.getText().toString();
				int xInt = xStr.equals("") ? 0 : Integer.parseInt(xStr);
				String yStr = etCoordinateY.getText().toString();
				int yInt = yStr.equals("") ? 0 : Integer.parseInt(yStr);
				mGameView.addCellStroke(xInt, yInt);
				break;
			case R.id.btn_draw_scale:
				mGameView.setDrawScale(DEFAULT_DRAW_SCALE);
				mGameView.resetLandTranslationX();
				mGameView.resetLandTranslationY();
				break;
			case R.id.btn_alive_probability:
				aliveProbability = Land.ALIVE_PROBABILITY_DEFAULT;
				updateAliveProbabilityBtn();
				updateAliveProbabilityBar();
				break;
			case R.id.tbtn_scale_mode:
				mGameView.setScaleMode(tBtnScaleMode.isChecked());
				break;
			case R.id.btn_recenter_map:
				mGameView.resetLandTranslationX();
				mGameView.resetLandTranslationY();
				break;
			case R.id.btn_palette:
				Settings.getInstance().setPaletteColor(Settings.PALETTE_COLOR_DEFAULT);
				rgPalette.check(R.id.rbtn_palette_black);
				break;
		}
	};

	private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		@SuppressLint("NonConstantResourceId")
		@Override
		public void onProgressChanged(@NonNull SeekBar seekBar, int progress, boolean fromUser) {
			switch (seekBar.getId()) {
				case R.id.bar_alive_probability:
					aliveProbability = MathUtils.clamp(progress * 0.01, 0, 1);
					updateAliveProbabilityBtn();
					break;
				case R.id.bar_draw_scale:
					if (fromUser) {
						mGameView.setDrawScale((progress + 100) * 0.01f);
					}
					break;
			}
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	};

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		new SoftKeyBoardStateChangeObserver(this).setOnSoftKeyBoardStateChangeListener(
				isShow -> {
					if (!isShow) {
						clearFocus();
						ActivityUtils.setImmersiveMode(this);
					}
				}
		);
		Intent intent = getIntent();
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(intent.getStringExtra("title"));
		}
        setContentView(R.layout.activity_main);

        flContainer = findViewById(R.id.fl_container);
        btnStop = findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(mOnClickListener);
        btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(mOnClickListener);
        btnClear = findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(mOnClickListener);
        btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(mOnClickListener);
        tvGameInfo = findViewById(R.id.tv_game_info);
        etCoordinateX = findViewById(R.id.et_coordinate_x);
        etCoordinateY = findViewById(R.id.et_coordinate_y);
        btnAddCell = findViewById(R.id.btn_add_cell);
        btnAddCell.setOnClickListener(mOnClickListener);
        btnDrawScale = findViewById(R.id.btn_draw_scale);
        btnDrawScale.setOnClickListener(mOnClickListener);
        barDrawScale = findViewById(R.id.bar_draw_scale);
		barDrawScale.setMax(1900);
        barDrawScale.setOnSeekBarChangeListener(seekBarChangeListener);
        btnAliveProbability = findViewById(R.id.btn_alive_probability);
        btnAliveProbability.setOnClickListener(mOnClickListener);
        barAliveProbability = findViewById(R.id.bar_alive_probability);
		barAliveProbability.setOnSeekBarChangeListener(seekBarChangeListener);
        aliveProbability = Land.ALIVE_PROBABILITY_DEFAULT;
        tBtnScaleMode = findViewById(R.id.tbtn_scale_mode);
        tBtnScaleMode.setOnClickListener(mOnClickListener);
        btnRecenterMap = findViewById(R.id.btn_recenter_map);
        btnRecenterMap.setOnClickListener(mOnClickListener);
        btnPalette = findViewById(R.id.btn_palette);
        btnPalette.setOnClickListener(mOnClickListener);

        rgPalette = findViewById(R.id.rg_palette);
        rgPalette.check(R.id.rbtn_palette_black);
        rgPalette.setOnCheckedChangeListener((group, checkedId) ->
				Settings.getInstance().setPaletteColor(((RadioButton)findViewById(checkedId)).getCurrentTextColor()));

		mGameView = new LandView(this, LandView.LAND_SIZE_LARGE, LandView.LAND_SIZE_LARGE);
        flContainer.addView(mGameView);
        gameInfoBuilder = new StringBuilder();
        mGameView.setOnDrawLandListener(this::updateGameInfo);
        mGameView.setOnDrawScaleChangeListener(newScale -> {
			updateDrawScaleBtn();
			updateDrawScaleBar();
		});
        DEFAULT_DRAW_SCALE = (float) (
				Math.min(mGameView.getHolderWidth(), mGameView.getHolderHeight()) * 1.0 /
				Math.max(mGameView.getLand().getWidth(), mGameView.getLand().getHeight()) * 1.0);
        mGameView.setDrawScale(DEFAULT_DRAW_SCALE);
		isLandIteration = true;
		updateAliveProbabilityBtn();
		updateAliveProbabilityBar();
    }

	@Override
	protected void onDestroy() {
		mGameView.release();
		super.onDestroy();
	}

	@SuppressLint("SetTextI18n")
	private void updateAliveProbabilityBtn() {
		btnAliveProbability.setText(new DecimalFormat("0").format(aliveProbability * 100) + "%");
	}

	private void updateAliveProbabilityBar() {
		barAliveProbability.setProgress((int) (aliveProbability * 100));
	}

	@SuppressLint("SetTextI18n")
	private void updateDrawScaleBtn() {
		btnDrawScale.setText(new DecimalFormat("0").format(mGameView.getDrawScale() * 100) + "%");
	}

	private void updateDrawScaleBar() {
		barDrawScale.setProgress((int) ((mGameView.getDrawScale() - 1.0f) * 100));
	}

	@Override
	protected void onPause() {
    	isLandIteration = mGameView.isLandIteration();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGameView.setLandIteration(isLandIteration);
	}

	private void updateGameInfo() {
    	runOnUiThread(() -> {
			gameInfoBuilder.setLength(0);
			gameInfoBuilder
					.append(getResources().getString(R.string.map_width))
					.append(": ")
					.append(mGameView.getLand().getWidth())
					.append("\n")
					.append(getResources().getString(R.string.map_height))
					.append(": ")
					.append(mGameView.getLand().getHeight())
					.append("\n")
					.append(getResources().getString(R.string.day_count))
					.append(": ")
					.append(mGameView.getLand().getDayCount())
					.append("\n")
					.append(getResources().getString(R.string.draw_fps))
					.append(": ")
					.append(mGameView.getDrawFps())
					.append("\n")
					.append(getResources().getString(R.string.iteration_map_fps))
					.append(": ")
					.append(mGameView.getIterationLandFps())
					.append("\n")
					.append(getResources().getString(R.string.free_space))
					.append(": ")
					.append(mGameView.getLand().getDeadCellCount())
					.append("\n")
					.append(getResources().getString(R.string.alive_cell_count))
					.append(": ")
					.append(mGameView.getLand().getAliveCellCount());
			tvGameInfo.setText(gameInfoBuilder.toString());
		});
	}

}

