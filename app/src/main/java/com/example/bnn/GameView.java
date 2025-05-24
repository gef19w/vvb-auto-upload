package com.example.bnn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    private Paint carPaint, obstaclePaint, textPaint;
    private float carX, carY;
    private final float carWidth = 100, carHeight = 150;
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private int score = 0;
    private int lives = 3;
    private boolean gameOver = false;
    private Handler handler = new Handler();
    private Random random = new Random();
    private Context context;

    public GameView(Context context) {
        super(context);
        this.context = context;
        initPaints();
        post(() -> {
            resetCarPosition();
            startGameLoop();
        });
    }

    private void initPaints() {
        carPaint = new Paint();
        carPaint.setColor(Color.RED);

        obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.BLUE);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
    }

    private void resetCarPosition() {
        carX = getWidth() / 2f - carWidth / 2;
        carY = getHeight() - carHeight - 50;
    }

    private void startGameLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!gameOver) {
                    update();
                    invalidate();
                    handler.postDelayed(this, 16);
                }
            }
        }, 0);
    }

    private void update() {
        for (Obstacle obstacle : obstacles) {
            obstacle.y += 10;
        }

        if (random.nextInt(60) == 0) {
            obstacles.add(new Obstacle(random.nextFloat() * (getWidth() - 100), -100));
        }

        obstacles.removeIf(obstacle -> obstacle.y > getHeight());

        if (checkCollision()) {
            lives--;
            if (lives > 0) {
                resetAfterHit();
            } else {
                gameOver = true;
            }
        }
    }

    private void resetAfterHit() {
        obstacles.clear();
        resetCarPosition();
        Toast.makeText(context, "اصطدمت! تبقى " + lives + " قلوب", Toast.LENGTH_SHORT).show();
    }

    private boolean checkCollision() {
        for (Obstacle obstacle : obstacles) {
            if (carX < obstacle.x + 100 &&
                carX + carWidth > obstacle.x &&
                carY < obstacle.y + 100 &&
                carY + carHeight > obstacle.y) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        canvas.drawRect(carX, carY, carX + carWidth, carY + carHeight, carPaint);

        for (Obstacle obstacle : obstacles) {
            canvas.drawRect(obstacle.x, obstacle.y, obstacle.x + 100, obstacle.y + 100, obstaclePaint);
        }

        canvas.drawText("النقاط: " + score, 50, 80, textPaint);
        canvas.drawText("القلوب: " + lives, getWidth() - 250, 80, textPaint);

        if (gameOver) {
            canvas.drawText("انتهت القلوب!", getWidth() / 2f - 150, getHeight() / 2f, textPaint);
            canvas.drawText("شاهد إعلان للاستمرار", getWidth() / 2f - 220, getHeight() / 2f + 100, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameOver) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() < getWidth() / 2) {
                carX -= 50;
            } else {
                carX += 50;
            }
            carX = Math.max(0, Math.min(getWidth() - carWidth, carX));
            invalidate();
        }
        return true;
    }
}