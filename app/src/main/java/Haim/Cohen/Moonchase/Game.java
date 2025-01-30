package Haim.Cohen.Moonchase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Haim.Cohen.Moonchase.Object.Circle;
import Haim.Cohen.Moonchase.Object.Enemy;
import Haim.Cohen.Moonchase.Object.Player;
import Haim.Cohen.Moonchase.Object.Spell;

/**
 * Game manages all objects in the game and is responsible for  updating and rendering all objects
 * to the screen
 */
class Game extends SurfaceView implements SurfaceHolder.Callback {
    private final Player player;

    private final Joystick joystick;
    private GameLoop gameLoop;
    private List<Enemy> enemyList = new ArrayList<Enemy>();
    private List<Spell> spellList = new ArrayList<Spell>();


    public Game(Context context) {
        super(context);

        // Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder);

        // Initialize game object
        joystick = new Joystick(275, 700,70, 40);

        //initialize player
        player = new Player(getContext(), joystick, 2*500, 500, 30);

        setFocusable(true);
    }

    // CLEAN THIS CODE UP LATER, FOR THE LOVE OF GOD THIS LOOKS LIKE SPAGHETTI
    @Override
    public boolean onTouchEvent(MotionEvent event){
        // Handle touch event actions
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (joystick.getIsPressed()) {
                    // Joystick was pressed before this event -> cast spell
                    spellList.add(new Spell(getContext(), player));
                } else if (joystick.isPressed((double) event.getX(), (double) event.getY())) {
                    joystick.setIsPressed(true);
                } else {
                    // Joystick is pressed in this event -> setIsPressed(true)
                    spellList.add(new Spell(getContext(), player));
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                // Joystick was pressed previously and is now moved.
                if(joystick.getIsPressed()) {
                    joystick.setActuator((double)event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
                // Joystick was letgo of -> setIsPressed(False) and resetActuator
                joystick.setIsPressed(false);
                joystick.resetActuator();
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawUPS(canvas);
        drawFPS(canvas);

        joystick.draw(canvas);
        player.draw(canvas);
        for (Enemy enemy : enemyList) {
            enemy.draw(canvas);
        }
        for (Spell spell : spellList) {
            spell.draw(canvas);
        }
    }

    public void drawUPS(Canvas canvas) {
      String averageUPS = Double.toString(gameLoop.getAverageUPS());
      Paint paint = new Paint();
      int color = ContextCompat.getColor(getContext(), R.color.magenta);
      paint.setColor(color);
      paint.setTextSize(50);
      canvas.drawText("UPS: " + averageUPS, 100, 140, paint);
    }

    public void drawFPS(Canvas canvas){
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("FPS: " + averageFPS, 100, 190, paint);
    }

    public void update() {
        //update game state
        joystick.update();
        player.update();

        //Spawn enemy if it is time to spawn new enemies
        if (Enemy.readyToSpawn()) {
            enemyList.add(new Enemy(getContext(), player));
        }

        // Update state of each enemy
        for (Enemy enemy : enemyList) {
            enemy.update();
        }

        // Update state of each spell
        for (Spell spell : spellList) {
            spell.update();
        }

        // Iterate through enemyList and check for collision between each enemy and the player
        Iterator<Enemy> iteratorEnemy = enemyList.iterator();
        while (iteratorEnemy.hasNext()) {
            if (Circle.isColliding(iteratorEnemy.next(), player)) {
                // Remove enemy if it collides with the player
                iteratorEnemy.remove();
            }
        }
    }
}

