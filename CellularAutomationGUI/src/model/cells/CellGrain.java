package model.cells;

public class CellGrain extends Cell {
    public static class State {
        public static byte EMPTY = 0;
        public static byte GRAIN = 1;
    }

    private int id;

    public CellGrain() {
        state = State.EMPTY;
        id = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
