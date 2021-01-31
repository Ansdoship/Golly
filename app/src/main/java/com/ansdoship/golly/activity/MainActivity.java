package com.ansdoship.golly.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.math.MathUtils;

import com.ansdoship.golly.game.Land;
import com.ansdoship.golly.util.ActivityUtils;
import com.ansdoship.golly.util.DialogUtils;
import com.ansdoship.golly.util.SoftKeyBoardStateChangeObserver;
import com.ansdoship.golly.view.LandView;
import com.ansdoship.golly.R;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

	private FrameLayout flContainer;
	private LandView landView;
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
	private StringBuilder gameInfoBuilder;

	private boolean isLandIteration;
	private double aliveProbability;

	private final View.OnClickListener clickListener = new View.OnClickListener() {
		@SuppressLint("NonConstantResourceId")
		@Override
		public void onClick(@NonNull View v) {
			switch(v.getId()){
				case R.id.btn_stop:
					landView.stopGame();
					break;
				case R.id.btn_start:
					landView.startGame();
					break;
				case R.id.btn_clear:
					landView.clear();
					break;
				case R.id.btn_reset:
					landView.reset(aliveProbability);
					break;
				case R.id.btn_add_cell:
					String xStr = etCoordinateX.getText().toString();
					int xInt = xStr.equals("") ? 0 : Integer.parseInt(xStr);
					String yStr = etCoordinateY.getText().toString();
					int yInt = yStr.equals("") ? 0 : Integer.parseInt(yStr);
					landView.addCellStroke(xInt, yInt);
					break;
				case R.id.btn_draw_scale:
					landView.setDrawScale(1.0f);
					landView.resetLandTranslationX();
					landView.resetLandTranslationY();
					break;
				case R.id.btn_alive_probability:
					aliveProbability = Land.ALIVE_PROBABILITY_DEFAULT;
					updateAliveProbabilityBtn();
					updateAliveProbabilityBar();
					break;
				case R.id.tbtn_scale_mode:
					landView.setScaleMode(tBtnScaleMode.isChecked());
					break;
				case R.id.btn_recenter_map:
					landView.resetLandTranslationX();
					landView.resetLandTranslationY();
					break;
				case R.id.tv_source_code_url:
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.source_code_url))));
					break;
			}
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
						landView.setDrawScale((progress + 100) * 0.01f);
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
		ActivityUtils.hideStatusBar(this);
		new SoftKeyBoardStateChangeObserver(this).setOnSoftKeyBoardStateChangeListener(
				isShow -> {
					if (!isShow) {
						clearFocus();
					}
				}
		);
        setContentView(R.layout.activity_main);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        flContainer = findViewById(R.id.fl_container);
        btnStop = findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(clickListener);
        btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(clickListener);
        btnClear = findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(clickListener);
        btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(clickListener);
        tvGameInfo = findViewById(R.id.tv_game_info);
        etCoordinateX = findViewById(R.id.et_coordinate_x);
        etCoordinateY = findViewById(R.id.et_coordinate_y);
        btnAddCell = findViewById(R.id.btn_add_cell);
        btnAddCell.setOnClickListener(clickListener);
        btnDrawScale = findViewById(R.id.btn_draw_scale);
        btnDrawScale.setOnClickListener(clickListener);
        barDrawScale = findViewById(R.id.bar_draw_scale);
		barDrawScale.setMax(900);
        barDrawScale.setOnSeekBarChangeListener(seekBarChangeListener);
        btnAliveProbability = findViewById(R.id.btn_alive_probability);
        btnAliveProbability.setOnClickListener(clickListener);
        barAliveProbability = findViewById(R.id.bar_alive_probability);
		barAliveProbability.setOnSeekBarChangeListener(seekBarChangeListener);
        aliveProbability = Land.ALIVE_PROBABILITY_DEFAULT;
        tBtnScaleMode = findViewById(R.id.tbtn_scale_mode);
        tBtnScaleMode.setOnClickListener(clickListener);
        btnRecenterMap = findViewById(R.id.btn_recenter_map);
        btnRecenterMap.setOnClickListener(clickListener);

        landView = new LandView(this);
        flContainer.addView(landView);
        gameInfoBuilder = new StringBuilder();
        landView.setOnDrawLandListener(this::updateGameInfo);
        landView.setOnDrawScaleChangeListener(newScale -> {
			updateDrawScaleBtn();
			updateDrawScaleBar();
		});
        landView.setDrawScale(1.0f);
		isLandIteration = true;
		updateAliveProbabilityBtn();
		updateAliveProbabilityBar();
    }

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override
	protected void onDestroy() {
		landView.release();
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
		btnDrawScale.setText(new DecimalFormat("0").format(landView.getDrawScale() * 100) + "%");
	}

	private void updateDrawScaleBar() {
		barDrawScale.setProgress((int) ((landView.getDrawScale() - 1.0f) * 100));
	}

	@Override
	protected void onPause() {
    	isLandIteration = landView.isLandIteration();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		landView.setLandIteration(isLandIteration);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mi_description:
				AlertDialog descriptionDialog = new AlertDialog.Builder(this).setMessage(R.string.description_content).create();
				descriptionDialog.show();
				TextView tvMessage = DialogUtils.getMessageTextView(descriptionDialog);
				if (tvMessage != null) {
					tvMessage.setTextSize(18);
				}
				return true;
			case R.id.mi_donate:
				//new AlertDialog.Builder(this).setView().create().show();
				return true;
			case R.id.mi_info:
				AlertDialog infoDialog = new AlertDialog.Builder(this).setView(R.layout.dialog_info).create();
				infoDialog.show();
				infoDialog.findViewById(R.id.tv_source_code_url).setOnClickListener(clickListener);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			if (!(getCurrentFocus() instanceof EditText)) {
				clearFocus();
			}
		}
	}

	private void clearFocus() {
		View focus = getCurrentFocus();
		if (focus != null) {
			focus.clearFocus();
		}
		ActivityUtils.hideNavigationBar(this);
	}

	private void updateGameInfo() {
    	runOnUiThread(() -> {
			gameInfoBuilder.setLength(0);
			gameInfoBuilder
					.append(getResources().getString(R.string.map_width))
					.append(": ")
					.append(landView.getLand().getWidth())
					.append("\n")
					.append(getResources().getString(R.string.map_height))
					.append(": ")
					.append(landView.getLand().getHeight())
					.append("\n")
					.append(getResources().getString(R.string.day_count))
					.append(": ")
					.append(landView.getLand().getDayCount())
					.append("\n")
					.append(getResources().getString(R.string.draw_fps))
					.append(": ")
					.append(landView.getDrawFps())
					.append("\n")
					.append(getResources().getString(R.string.iteration_map_fps))
					.append(": ")
					.append(landView.getIterationLandFps())
					.append("\n")
					.append(getResources().getString(R.string.free_space))
					.append(": ")
					.append(landView.getLand().getDeadCellCount())
					.append("\n")
					.append(getResources().getString(R.string.alive_cell_count))
					.append(": ")
					.append(landView.getLand().getAliveCellCount());
			tvGameInfo.setText(gameInfoBuilder.toString());
		});
	}

}

