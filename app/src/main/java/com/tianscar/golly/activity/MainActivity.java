package com.tianscar.golly.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tianscar.golly.ui.GameView;
import com.tianscar.golly.R;

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
	private StringBuilder gameInfoBuilder;

	private boolean isGameRendering;

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
					gameView.getLand().clear();
					break;
				case R.id.btn_reset:
					gameView.getLand().init();
					break;
				case R.id.btn_add_cell:
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

        gameView = new GameView(this);
        container.addView(gameView);
        gameInfoBuilder = new StringBuilder();

        gameView.setOnInvalidateListener(new GameView.OnInvalidateListener() {
			@Override
			public void onInvalidate() {
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
		});
        gameView.startGame();
		isGameRendering = true;
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

}

