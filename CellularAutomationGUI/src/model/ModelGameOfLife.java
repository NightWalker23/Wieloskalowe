package model;



import model.cells.Cell;
import model.cells.CellGameOfLife;

import java.util.Random;

public class ModelGameOfLife {
    private Cell[][] grid;
    private int gridHeight, gridWidth;

    public ModelGameOfLife(int height, int wight) {
        gridHeight = height;
        gridWidth = wight;
        grid = new CellGameOfLife[gridHeight][gridWidth];

        for (int i = 0; i < gridHeight; i++)
            for (int j = 0; j < gridWidth; j++)
                grid[i][j] = new CellGameOfLife();

        //fillRandomly(100);
    }

    private int checkNeighbours(CellGameOfLife[][] frame, int height, int width) {
        int result;

        int iG, i, iD, jL, j, jR;
        i = height;
        j = width;

        if (height == 0) iG = gridHeight - 1;
        else iG = height - 1;

        if (height == gridHeight - 1) iD = 0;
        else iD = height + 1;

        if (width == 0) jL = gridWidth - 1;
        else jL = width - 1;

        if (width == gridWidth - 1) jR = 0;
        else jR = width + 1;

        int aliveNeighbours = 0;
        if (frame[iG][jL].getState() == CellGameOfLife.Type.ALIVE) aliveNeighbours++;
        if (frame[iG][j].getState() == CellGameOfLife.Type.ALIVE) aliveNeighbours++;
        if (frame[iG][jR].getState() == CellGameOfLife.Type.ALIVE) aliveNeighbours++;

        if (frame[i][jL].getState() == CellGameOfLife.Type.ALIVE) aliveNeighbours++;//
        if (frame[i][jR].getState() == CellGameOfLife.Type.ALIVE) aliveNeighbours++;

        if (frame[iD][jL].getState() == CellGameOfLife.Type.ALIVE) aliveNeighbours++;
        if (frame[iD][j].getState() == CellGameOfLife.Type.ALIVE) aliveNeighbours++;
        if (frame[iD][jR].getState() == CellGameOfLife.Type.ALIVE) aliveNeighbours++;

        if (frame[height][width].getState() == CellGameOfLife.Type.DEAD && aliveNeighbours == 3)
            result = CellGameOfLife.Type.ALIVE;
        else if (frame[height][width].getState() == CellGameOfLife.Type.ALIVE && (aliveNeighbours == 3 || aliveNeighbours == 2))
            result = CellGameOfLife.Type.ALIVE;
        else
            result = CellGameOfLife.Type.DEAD;

        return result;
    }

    public CellGameOfLife[][] getGrid() {
        return (CellGameOfLife[][]) grid;
    }

    public CellGameOfLife[][] getResult(CellGameOfLife[][] frame) {
        CellGameOfLife[][] tmp = new CellGameOfLife[gridHeight][gridWidth];
        for (int i = 0; i < gridHeight; i++)
            for (int j = 0; j < gridWidth; j++)
                tmp[i][j] = new CellGameOfLife();

        for (int i = 0; i < gridHeight; i++)
            for (int j = 0; j < gridWidth; j++)
                tmp[i][j].setState(checkNeighbours(frame, i, j));

        for (int i = 0; i < gridHeight; i++)
            System.arraycopy(tmp[i], 0, grid[i], 0, gridWidth);

        return getGrid();
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void clearGrid(){
        for (int i = 0; i < gridHeight; i++)
            for (int j = 0; j < gridWidth; j++)
                grid[i][j].setState(CellGameOfLife.Type.DEAD);
    }

    public void fillRandomly(int amount){
        Random rand = new Random();
        for (int i = 0; i < amount; i++)
            grid[rand.nextInt(gridHeight - 1)][rand.nextInt(gridWidth - 1)].setState(CellGameOfLife.Type.ALIVE);
    }
}
