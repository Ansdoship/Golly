package com.ansdoship.golly.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ansdoship.golly.R;

public class MenuActivity extends BaseActivity {

    private Button btnStoryMode;
    private Button btnSandboxMode;
    private Button btnSurviveMode;
    private Button btnVersusMode;
    private Button btnExitGame;

    @SuppressLint("NonConstantResourceId")
    private final View.OnClickListener mOnClickListener = v -> {
        switch (v.getId()) {
            case R.id.btn_story_mode:
                createComingSoonDialog();
                break;
            case R.id.btn_sandbox_mode:
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("title", getString(R.string.sandbox_mode));
                startActivity(intent);
                break;
            case R.id.btn_survive_mode:
                createComingSoonDialog();
                break;
            case R.id.btn_versus_mode:
                createComingSoonDialog();
                break;
            case R.id.btn_exit_game:
                finish();
                System.exit(0);
                break;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnStoryMode = findViewById(R.id.btn_story_mode);
        btnStoryMode.setOnClickListener(mOnClickListener);
        btnSandboxMode = findViewById(R.id.btn_sandbox_mode);
        btnSandboxMode.setOnClickListener(mOnClickListener);
        btnSurviveMode = findViewById(R.id.btn_survive_mode);
        btnSurviveMode.setOnClickListener(mOnClickListener);
        btnVersusMode = findViewById(R.id.btn_versus_mode);
        btnVersusMode.setOnClickListener(mOnClickListener);
        btnExitGame = findViewById(R.id.btn_exit_game);
        btnExitGame.setOnClickListener(mOnClickListener);

    }

}