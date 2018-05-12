package model;

import java.util.*;

import javafx.scene.paint.Color;
import model.cells.CellGrain;

import static model.cells.CellGrain.*;

public class ModelGrainGrowth {
    private CellGrain[][] grid;
    private int gridHeight, gridWidth;
    private NeighborhoodType neighborhoodType;
    private EdgeType edgeType;
    private List<GrainType> listOfGrains;

    public enum EdgeType {
        Closed, Periodic
    }

    public enum NeighborhoodType {
        vonNeuman, Moore, randomPentagonal, randomHexagonal
    }

    public class GrainType {
        Color grainColor;

        public GrainType(double r, double g, double b) {
            grainColor = Color.color(r, g, b);
        }

        public Color getGrainColor() {
            return grainColor;
        }
    }

    public ModelGrainGrowth(int height, int wight, NeighborhoodType type, int grainAmount, EdgeType edgeType) {
        neighborhoodType = type;
        this.edgeType = edgeType;
        gridHeight = height;
        gridWidth = wight;
        grid = new CellGrain[gridHeight][gridWidth];

        for (int i = 0; i < gridHeight; i++)
            for (int j = 0; j < gridWidth; j++)
                grid[i][j] = new CellGrain();

        fillRandomly(grainAmount);
    }

    private void fillMap(int id, Map<Integer, Integer> grainMap) {
        if (grainMap.containsKey(id)) grainMap.put(id, grainMap.get(id) + 1);
        else grainMap.put(id, 1);
    }

    private int getIDMaxNeighbour(Map<Integer, Integer> grainMap) {
        Map.Entry<Integer, Integer> maxEntry = null;

        for (Map.Entry<Integer, Integer> entry : grainMap.entrySet())
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
                maxEntry = entry;

        int id = 0;
        if (maxEntry != null) {
            int max = maxEntry.getValue();

            List<Integer> listMax = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : grainMap.entrySet())
                if (entry.getValue() == max)
                    listMax.add(entry.getKey());

            Random rand = new Random();
            int randWinner = rand.nextInt(listMax.size());
            id = listMax.get(randWinner);
        }

        return id;
    }

    private int vonNeuman(CellGrain[][] frame, int iG, int i, int iD, int jL, int j, int jR) {
        Map<Integer, Integer> grainMap = new HashMap<>();
        int newID = 0;
        int grainType;

        if (iG != -1)
            if (frame[iG][j].getState() == State.GRAIN) {
                grainType = frame[iG][j].getId();
                fillMap(grainType, grainMap);
            }

        if (jL != -1)
            if (frame[i][jL].getState() == State.GRAIN) {
                grainType = frame[i][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (jR != -1)
            if (frame[i][jR].getState() == State.GRAIN) {
                grainType = frame[i][jR].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1)
            if (frame[iD][j].getState() == State.GRAIN) {
                grainType = frame[iD][j].getId();
                fillMap(grainType, grainMap);
            }

        int max = getIDMaxNeighbour(grainMap);
        newID = max;

        return newID;
    }

    private int Moore(CellGrain[][] frame, int iG, int i, int iD, int jL, int j, int jR) {
        Map<Integer, Integer> grainMap = new HashMap<>();
        int newID = 0;
        int grainType;

        if (iG != -1 && jL != -1)
            if (frame[iG][jL].getState() == State.GRAIN) {
                grainType = frame[iG][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (iG != -1)
            if (frame[iG][j].getState() == State.GRAIN) {
                grainType = frame[iG][j].getId();
                fillMap(grainType, grainMap);
            }

        if (iG != -1 && jR != -1)
            if (frame[iG][jR].getState() == State.GRAIN) {
                grainType = frame[iG][jR].getId();
                fillMap(grainType, grainMap);
            }

        if (jL != -1)
            if (frame[i][jL].getState() == State.GRAIN) {
                grainType = frame[i][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (jR != -1)
            if (frame[i][jR].getState() == State.GRAIN) {
                grainType = frame[i][jR].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1 && jL != -1)
            if (frame[iD][jL].getState() == State.GRAIN) {
                grainType = frame[iD][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1)
            if (frame[iD][j].getState() == State.GRAIN) {
                grainType = frame[iD][j].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1 && jR != -1)
            if (frame[iD][jR].getState() == State.GRAIN) {
                grainType = frame[iD][jR].getId();
                fillMap(grainType, grainMap);
            }

        int max = getIDMaxNeighbour(grainMap);
        newID = max;

        return newID;
    }

    private int randomPentagonal(CellGrain[][] frame, int iG, int i, int iD, int jL, int j, int jR) {
        Map<Integer, Integer> grainMap = new HashMap<>();
        int newID = 0;
        int grainType;

        int U = 0, R = 1, D = 2, L = 3;
        Random rand = new Random();
        int randDirection = rand.nextInt(4);

        if ((iG != -1 && jL != -1) && (randDirection != U && randDirection != L))
            if (frame[iG][jL].getState() == State.GRAIN) {
                grainType = frame[iG][jL].getId();
                fillMap(grainType, grainMap);
            }

        if ((iG != -1) && randDirection != U)
            if (frame[iG][j].getState() == State.GRAIN) {
                grainType = frame[iG][j].getId();
                fillMap(grainType, grainMap);
            }

        if ((iG != -1 && jR != -1) && (randDirection != U && randDirection != R))
            if (frame[iG][jR].getState() == State.GRAIN) {
                grainType = frame[iG][jR].getId();
                fillMap(grainType, grainMap);
            }

        if ((jL != -1) && randDirection != L)
            if (frame[i][jL].getState() == State.GRAIN) {
                grainType = frame[i][jL].getId();
                fillMap(grainType, grainMap);
            }

        if ((jR != -1) && randDirection != R)
            if (frame[i][jR].getState() == State.GRAIN) {
                grainType = frame[i][jR].getId();
                fillMap(grainType, grainMap);
            }

        if ((iD != -1 && jL != -1) && (randDirection != D && randDirection != L))
            if (frame[iD][jL].getState() == State.GRAIN) {
                grainType = frame[iD][jL].getId();
                fillMap(grainType, grainMap);
            }

        if ((iD != -1) && randDirection != D)
            if (frame[iD][j].getState() == State.GRAIN) {
                grainType = frame[iD][j].getId();
                fillMap(grainType, grainMap);
            }

        if ((iD != -1 && jR != -1) && (randDirection != D && randDirection != R))
            if (frame[iD][jR].getState() == State.GRAIN) {
                grainType = frame[iD][jR].getId();
                fillMap(grainType, grainMap);
            }

        int max = getIDMaxNeighbour(grainMap);
        newID = max;

        return newID;
    }

    private int randomHexagonal(CellGrain[][] frame, int iG, int i, int iD, int jL, int j, int jR) {
        Map<Integer, Integer> grainMap = new HashMap<>();
        int newID = 0;
        int grainType;

        if (iG != -1)
            if (frame[iG][j].getState() == State.GRAIN) {
                grainType = frame[iG][j].getId();
                fillMap(grainType, grainMap);
            }

        if (jL != -1)
            if (frame[i][jL].getState() == State.GRAIN) {
                grainType = frame[i][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (jR != -1)
            if (frame[i][jR].getState() == State.GRAIN) {
                grainType = frame[i][jR].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1)
            if (frame[iD][j].getState() == State.GRAIN) {
                grainType = frame[iD][j].getId();
                fillMap(grainType, grainMap);
            }

        Random rand = new Random();

        if (rand.nextInt(2) == 0) {
            if (iG != -1 && jL != -1)
                if (frame[iG][jL].getState() == State.GRAIN) {
                    grainType = frame[iG][jL].getId();
                    fillMap(grainType, grainMap);
                }

            if (iD != -1 && jR != -1)
                if (frame[iD][jR].getState() == State.GRAIN) {
                    grainType = frame[iD][jR].getId();
                    fillMap(grainType, grainMap);
                }
        } else {
            if (iG != -1 && jR != -1)
                if (frame[iG][jR].getState() == State.GRAIN) {
                    grainType = frame[iG][jR].getId();
                    fillMap(grainType, grainMap);
                }

            if (iD != -1 && jL != -1)
                if (frame[iD][jL].getState() == State.GRAIN) {
                    grainType = frame[iD][jL].getId();
                    fillMap(grainType, grainMap);
                }
        }

        int max = getIDMaxNeighbour(grainMap);
        newID = max;

        return newID;
    }

    private int checkNeighbours(CellGrain[][] frame, int height, int width) {
        int result = 0;

        int iG, i, iD, jL, j, jR;
        i = height;
        j = width;

        if (height == 0)
            if (edgeType == EdgeType.Closed) iG = -1;
            else iG = gridHeight - 1;
        else iG = height - 1;

        if (height == gridHeight - 1)
            if (edgeType == EdgeType.Closed) iD = -1;
            else iD = 0;
        else iD = height + 1;

        if (width == 0)
            if (edgeType == EdgeType.Closed) jL = -1;
            else jL = gridWidth - 1;
        else jL = width - 1;

        if (width == gridWidth - 1)
            if (edgeType == EdgeType.Closed) jR = -1;
            else jR = 0;
        else jR = width + 1;

        switch (neighborhoodType) {
            case vonNeuman:
                result = vonNeuman(frame, iG, i, iD, jL, j, jR);
                break;
            case Moore:
                result = Moore(frame, iG, i, iD, jL, j, jR);
                break;
            case randomPentagonal:
                result = randomPentagonal(frame, iG, i, iD, jL, j, jR);
                break;
            case randomHexagonal:
                result = randomHexagonal(frame, iG, i, iD, jL, j, jR);
                break;
        }

        return result;
    }

    public CellGrain[][] getGrid() {
        return grid;
    }

    private CellGrain[][] getTmp() {
        CellGrain[][] tmp = new CellGrain[gridHeight][gridWidth];
        for (int i = 0; i < gridHeight; i++)
            for (int j = 0; j < gridWidth; j++)
                tmp[i][j] = new CellGrain();

        return tmp;
    }

    public CellGrain[][] getResult(CellGrain[][] frame) {
        CellGrain[][] tmp = getTmp();

        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                if (frame[i][j].getState() == State.EMPTY) {
                    tmp[i][j].setId(checkNeighbours(frame, i, j));
                    if (tmp[i][j].getId() != 0) tmp[i][j].setState(State.GRAIN);
                } else if (frame[i][j].getState() == State.GRAIN) {
                    tmp[i][j].setState(State.GRAIN);
                    tmp[i][j].setId(frame[i][j].getId());
                }
            }
        }

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

    public void fillRandomly(int grainAmount) {
        Random rand = new Random();
        listOfGrains = new ArrayList<>();

        for (int i = 0; i < grainAmount; i++) {
            listOfGrains.add(new GrainType(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
            int x = rand.nextInt(gridHeight);
            int y = rand.nextInt(gridWidth);
            grid[x][y].setState(State.GRAIN);
            grid[x][y].setId(i + 1);
        }
    }

    public List getListOfGrains() {
        return listOfGrains;
    }
}