package com.dude.meme;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView bottle;
    private boolean isSpinning = false;
    private float lastRotation = 0f;
    private MediaPlayer mediaPlayer;
    private Spinner imageSelector;

    private float startX;

    private final int[] bottleImages = {
            R.drawable.bottle,
            R.drawable.tralalero,
            R.drawable.bombardino,
            R.drawable.bombini,
            R.drawable.lil,
            R.drawable.bonekaambalabu,
            R.drawable.brrpatapim,
            R.drawable.cappuccino,
            R.drawable.frigocamelo
    };

    private final int[] soundFiles = {
            R.raw.tung,
            R.raw.tralalero,
            R.raw.bombardino,
            R.raw.bombini,
            R.raw.lil,
            R.raw.bonekaambalabu,
            R.raw.brrpatapim,
            R.raw.cappuccino,
            R.raw.frigocamelo
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottle = findViewById(R.id.bottle);
        mediaPlayer = new MediaPlayer();
        imageSelector = findViewById(R.id.imageSelector);
        ImageButton btnRandom = findViewById(R.id.btnRandom);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bottle_options, R.layout.spinner_item_white);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageSelector.setAdapter(adapter);

        imageSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bottle.setImageResource(bottleImages[position]);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                mediaPlayer = MediaPlayer.create(MainActivity.this, soundFiles[position]);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRandom.setOnClickListener(v -> {
            int randomIndex = new Random().nextInt(bottleImages.length);
            imageSelector.setSelection(randomIndex);
        });

        // 觸控偵測滑動方向
        bottle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    return true;
                case MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    if (!isSpinning) {
                        if (endX > startX) {
                            spinBottle(1);  // 向右（順時針）
                        } else if (endX < startX) {
                            spinBottle(-1); // 向左（逆時針）
                        }
                    }
                    return true;
            }
            return false;
        });
    }

    private void spinBottle(int direction) {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

        int minSpins = 5;
        int maxSpins = 10;
        int spins = new Random().nextInt((maxSpins - minSpins) + 1) + minSpins;
        int angle = new Random().nextInt(360);
        float totalRotation = (360f * spins + angle) * direction;
        float newRotation = lastRotation + totalRotation;

        ObjectAnimator animator = ObjectAnimator.ofFloat(bottle, "rotation", lastRotation, newRotation);
        animator.setDuration(3000);
        animator.setInterpolator(new DecelerateInterpolator());

        animator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(@NonNull Animator animation) { isSpinning = true; }
            @Override public void onAnimationEnd(@NonNull Animator animation) {
                isSpinning = false;
                lastRotation = newRotation % 360;
            }
            @Override public void onAnimationCancel(@NonNull Animator animation) {}
            @Override public void onAnimationRepeat(@NonNull Animator animation) {}
        });

        animator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
