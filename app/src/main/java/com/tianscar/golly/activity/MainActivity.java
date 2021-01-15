package com.tianscar.golly.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.math.MathUtils;

import com.tianscar.golly.game.Cell;
import com.tianscar.golly.game.Land;
import com.tianscar.golly.ui.GameView;
import com.tianscar.golly.R;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

	// Widgets
	private FrameLayout container;
	private GameView gameView;
	private Button btnStop;
	private Button btnStart;
	private Button btnClear;
	private Button btnReset;
	public TextView tvGameInfo;
	private EditText etCoordinateX;
	private EditText etCoordinateY;
	private Button btnAddCell;
	private Button btnScaleDown;
	private Button btnScaleUp;
	private Button btnScaleReset;
	private TextView tvScale;
	private Button btnAliveProbability;
	private SeekBar barAliveProbability;
	private StringBuilder gameInfoBuilder;

	private boolean isGameRendering;
	private double aliveProbability;

	private final View.OnClickListener clickListener = new View.OnClickListener() {
		@SuppressLint("NonConstantResourceId")
		@Override
		public void onClick(@NonNull View v) {
			switch(v.getId()){
				case R.id.btn_stop:
					gameView.stopGame();
					break;
				case R.id.btn_start:
					gameView.startGame();
					break;
				case R.id.btn_clear:
					gameView.clear();
					if (!gameView.isGameRendering()) {
						updateGameInfo();
					}
					break;
				case R.id.btn_reset:
					gameView.reset(aliveProbability);
					if (!gameView.isGameRendering()) {
						updateGameInfo();
					}
					break;
				case R.id.btn_add_cell:
					break;
				case R.id.btn_scale_down:
					gameView.addDrawScale(-0.2f);
					break;
				case R.id.btn_scale_up:
					gameView.addDrawScale(0.2f);
					break;
				case R.id.btn_scale_reset:
					gameView.setDrawScale(1.0f);
					break;
				case R.id.btn_alive_probability:
					aliveProbability = Land.ALIVE_PROBABILITY_DEFAULT;
					updateAliveProbabilityBtn();
					updateAliveProbabilityBar();
					break;
				default:
					throw new IllegalStateException("Unexpected value: " + v.getId());
			}
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
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
        btnScaleDown = findViewById(R.id.btn_scale_down);
        btnScaleDown.setOnClickListener(clickListener);
        btnScaleUp = findViewById(R.id.btn_scale_up);
        btnScaleUp.setOnClickListener(clickListener);
        btnScaleReset = findViewById(R.id.btn_scale_reset);
        btnScaleReset.setOnClickListener(clickListener);
        tvScale = findViewById(R.id.tv_scale);
        btnAliveProbability = findViewById(R.id.btn_alive_probability);
        btnAliveProbability.setOnClickListener(clickListener);
        barAliveProbability = findViewById(R.id.bar_alive_probability);

        aliveProbability = Land.ALIVE_PROBABILITY_DEFAULT;
		updateAliveProbabilityBtn();
		updateAliveProbabilityBar();

        barAliveProbability.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				aliveProbability = MathUtils.clamp(progress * 0.01, 0, 1);
				updateAliveProbabilityBtn();
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

        gameView = new GameView(this);
        container.addView(gameView);
        gameInfoBuilder = new StringBuilder();

        gameView.setOnDrawScaleChangeListener(new GameView.OnDrawScaleChangeListener() {
			@Override
			public void onDrawScaleChange(float newScale) {
				tvScale.setText(new DecimalFormat("0.0").format(newScale * 100));
				tvScale.append("%");
			}
		});
        gameView.setOnInvalidateListener(new GameView.OnInvalidateListener() {
			@Override
			public void onInvalidate() {
				updateGameInfo();
			}
		});
        gameView.setDrawScale(1.0f);
        gameView.startGame();
		isGameRendering = true;
    }

    private void updateAliveProbabilityBtn() {
		btnAliveProbability.setText(new DecimalFormat("0").format(aliveProbability * 100));
		btnAliveProbability.append("%");
	}

	private void updateAliveProbabilityBar() {
		barAliveProbability.setProgress((int) (aliveProbability * 100));
	}

	@Override
	protected void onPause() {
    	isGameRendering = gameView.isGameRendering();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		gameView.setGameRendering(isGameRendering);
	}

	private void updateGameInfo() {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				gameInfoBuilder.setLength(0);
				gameInfoBuilder
						.append(getResources().getString(R.string.authors))
						.append(": ")
						.append(getResources().getString(R.string.dobando))
						.append(" & ")
						.append(getResources().getString(R.string.tianscar))
						.append("\n")
						.append(getResources().getString(R.string.day_count))
						.append(": ")
						.append(gameView.getLand().getDayCount())
						.append("\n")
						.append(getResources().getString(R.string.FPS))
						.append(": ")
						.append(gameView.getFPS())
						.append("\n")
						.append(getResources().getString(R.string.free_space))
						.append(": ")
						.append(gameView.getLand().getDeadCellCount())
						.append("\n")
						.append(getResources().getString(R.string.alive_cell_count))
						.append(": ")
						.append(gameView.getLand().getAliveCellCount());
				tvGameInfo.setText(gameInfoBuilder.toString());
			}
		});
	}

}

