package model.cells;

public interface Cell {
    class Type{}
    int state = 0;

    int getState();
    void setState(int state);
}
