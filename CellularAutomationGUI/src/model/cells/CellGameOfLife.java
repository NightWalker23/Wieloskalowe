package model.cells;

public class CellGameOfLife implements Cell {
    public static class Type {
        public static int DEAD = 0;
        public static int ALIVE = 1;
    }
    private int state;

    public CellGameOfLife() {
        state = Type.DEAD;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
