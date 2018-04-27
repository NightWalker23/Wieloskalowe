package app;

import model.cells.CellGameOfLife;
import model.ModelGameOfLife;


public class MainGameOfLife {

    public static void show(CellGameOfLife[][] tab, int height, int width) {
        int state;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                state = tab[i][j].getState();
                if (state == CellGameOfLife.Type.ALIVE)
                    System.out.print(tab[i][j].getState());
                else System.out.print(" ");
            }

            System.out.println();
        }
        System.out.println("\n\n\n");
    }

    public static void main(String[] args) {
        int height = 10, width = 10;

        ModelGameOfLife mgol = new ModelGameOfLife(height, width);
        show(mgol.getGrid(), mgol.getGridHeight(), mgol.getGridWidth());
        show(mgol.getResult(mgol.getGrid()), mgol.getGridHeight(), mgol.getGridWidth());
        show(mgol.getResult(mgol.getGrid()), mgol.getGridHeight(), mgol.getGridWidth());
        show(mgol.getResult(mgol.getGrid()), mgol.getGridHeight(), mgol.getGridWidth());
        show(mgol.getResult(mgol.getGrid()), mgol.getGridHeight(), mgol.getGridWidth());
        show(mgol.getResult(mgol.getGrid()), mgol.getGridHeight(), mgol.getGridWidth());
        show(mgol.getResult(mgol.getGrid()), mgol.getGridHeight(), mgol.getGridWidth());
    }
}
