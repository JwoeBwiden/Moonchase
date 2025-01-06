package Haim.Cohen.Moonchase;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
public class GameLoop extends Thread {
    private boolean isRunning = false;
    private Game game;
    private SurfaceHolder surfaceHolder;
    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.game = game;
        this.surfaceHolder = surfaceHolder;
    }


    public static double getAverageUPS() {
        return 0;
    }

    public static double getAverageFPS() {
        return 0;
    }

    public void startLoop() {
        isRunning = true;
        start();
    }

    @Override
    public void run() {
        super.run();
        // Game Loop

        while (isRunning) {

            Canvas canvas;
            while (isRunning) {
                //Try to update and render the game
                try {
                    canvas = surfaceHolder.lockCanvas();
                    game.update();
                    game.draw(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                //Pause game Loop to not exceed target UPS

                //Skip frames to keep up with target UPS

                //Calculate UPS and FPS
            }
        }
    }
}