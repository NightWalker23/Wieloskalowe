package model;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.cells.CellGameOfLife;

import java.util.concurrent.TimeUnit;

public class Painter implements Runnable {
    private Canvas canvas2D;
    private ModelGameOfLife model;
    private GraphicsContext gc;
    private volatile boolean running;
    private volatile boolean paused;
    private final Object pauseLock = new Object();
    private int speed;

    public Painter(Canvas canvas2D, ModelGameOfLife model, GraphicsContext gc, int speed) {
        this.canvas2D = canvas2D;
        this.model = model;
        this.gc = gc;
        running = true;
        paused = false;
        this.speed = speed;
    }

    private void cleanCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas2D.getWidth(), canvas2D.getHeight());
    }

    @Override
    public void run() {

        int height = (int) canvas2D.getHeight() / model.getGridHeight();
        int width = (int) canvas2D.getWidth() / model.getGridWidth();

        while (running) {
            synchronized (pauseLock) {
                if (!running) {
                    break;
                }
                if (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) {
                        break;
                    }
                }
            }
            try {
                Platform.runLater(() -> {
                    CellGameOfLife[][] tab = model.getResult(model.getGrid());
                    cleanCanvas();
                    gc.setFill(Color.BLACK);
                    for (int i = 0; i < model.getGridHeight(); i++) {
                        for (int j = 0; j < model.getGridWidth(); j++)
                            if (tab[i][j].getState() == Model1D.Option.ALIVE)
                                gc.fillRect(j * width, i * height, height, width);
                    }
                });

                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
            }
        }
    }

    public void stop() {
        running = false;
        resume();
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public boolean isPaused() {
        return paused;
    }
}
