package Haim.Cohen.Moonchase;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
public class GameLoop extends Thread {
    public static final double MAX_UPS = 30.0;
    private static final double UPS_PERIOD = (1E+3)/MAX_UPS;
    private boolean isRunning = false;
    private Game game;
    private SurfaceHolder surfaceHolder;
    private double averageUPS;
    private double averageFPS;

    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.game = game;
        this.surfaceHolder = surfaceHolder;
    }


    public double getAverageUPS() {
        return averageUPS;
    }

    public double getAverageFPS() {
        return averageFPS;
    }

    public void startLoop() {
        isRunning = true;
        start();
    }

    @Override
    public void run() {
        super.run();


        //Declare time and Cycle count variables
        int updateCount = 0;
        int frameCount = 0;

        long startTime;
        long elapsedTime;
        long sleepTime;

        while (isRunning) {
            // Game Loop
            Canvas canvas = null;
            startTime = System.currentTimeMillis();
            while (isRunning) {
                //Try to update and render the game
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        game.update();
                        updateCount++;

                        game.draw(canvas);
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } finally{
                    if (canvas != null)
                    {try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        frameCount++;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }}
                }

                //Pause game Loop to not exceed target UPS
                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime = (long) (updateCount*UPS_PERIOD - elapsedTime);
                if(sleepTime > 0) {
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                //Skip frames to keep up with target UPS
                while(sleepTime < 0 && updateCount < MAX_UPS - 1){
                    game.update();
                    updateCount++;
                    elapsedTime = System.currentTimeMillis() - startTime;
                    sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
                }

                //Calculate UPS and FPS
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime >= 1000) {
                    averageUPS = updateCount / (1E-3 * elapsedTime);
                    averageFPS = frameCount / (1E-3 * elapsedTime);
                    updateCount = 0;
                    frameCount = 0;
                    startTime = System.currentTimeMillis();
                }
            }
        }
    }
}