package model;

import java.util.*;

import javafx.scene.paint.Color;
import model.cells.CellGrain;

import static model.ModelGrainGrowth.NeighborhoodType.*;
import static model.ModelGrainGrowth.EdgeType.*;
import static model.cells.CellGrain.State.*;
import static model.cells.CellGrain.Availability.*;

public class ModelGrainGrowth {
    private CellGrain[][] grid;
    private int gridHeight, gridWidth;
    private NeighborhoodType neighborhoodType;
    private EdgeType edgeType;
    private TypeOfPlacement placementType;
    private List<GrainType> listOfGrains;

    public enum EdgeType {
        Closed, Periodic
    }

    public enum NeighborhoodType {
        vonNeuman, Moore, leftPentagonal, rightPentagonal, upPentagonal, downPentagonal, randomPentagonal, leftHexagonal, rightHexagonal, randomHexagonal
    }

    public enum TypeOfPlacement {
        Random, EvenlyPlacement, RandomWithRadius
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

    private class Coordinates {
        public int x, y;

        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public ModelGrainGrowth(int height, int wight, NeighborhoodType type, EdgeType edgeType) {
        neighborhoodType = type;
        this.edgeType = edgeType;
        gridHeight = height;
        gridWidth = wight;
        createGrid();
        listOfGrains = new ArrayList<>();
    }

    public void setEdgeType(EdgeType edgeType) {
        this.edgeType = edgeType;
    }

    public void setPlacementType(TypeOfPlacement placementType) {
        this.placementType = placementType;
    }

    public void createGrid() {
        grid = new CellGrain[gridHeight][gridWidth];

        for (int i = 0; i < gridHeight; i++)
            for (int j = 0; j < gridWidth; j++)
                grid[i][j] = new CellGrain();
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
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
        int grainType;

        if (iG != -1)
            if (frame[iG][j].getState() == GRAIN) {
                grainType = frame[iG][j].getId();
                fillMap(grainType, grainMap);
            }

        if (jL != -1)
            if (frame[i][jL].getState() == GRAIN) {
                grainType = frame[i][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (jR != -1)
            if (frame[i][jR].getState() == GRAIN) {
                grainType = frame[i][jR].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1)
            if (frame[iD][j].getState() == GRAIN) {
                grainType = frame[iD][j].getId();
                fillMap(grainType, grainMap);
            }

        return getIDMaxNeighbour(grainMap);
    }

    private int moore(CellGrain[][] frame, int iG, int i, int iD, int jL, int j, int jR) {
        Map<Integer, Integer> grainMap = new HashMap<>();
        int grainType;

        if (iG != -1 && jL != -1)
            if (frame[iG][jL].getState() == GRAIN) {
                grainType = frame[iG][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (iG != -1)
            if (frame[iG][j].getState() == GRAIN) {
                grainType = frame[iG][j].getId();
                fillMap(grainType, grainMap);
            }

        if (iG != -1 && jR != -1)
            if (frame[iG][jR].getState() == GRAIN) {
                grainType = frame[iG][jR].getId();
                fillMap(grainType, grainMap);
            }

        if (jL != -1)
            if (frame[i][jL].getState() == GRAIN) {
                grainType = frame[i][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (jR != -1)
            if (frame[i][jR].getState() == GRAIN) {
                grainType = frame[i][jR].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1 && jL != -1)
            if (frame[iD][jL].getState() == GRAIN) {
                grainType = frame[iD][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1)
            if (frame[iD][j].getState() == GRAIN) {
                grainType = frame[iD][j].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1 && jR != -1)
            if (frame[iD][jR].getState() == GRAIN) {
                grainType = frame[iD][jR].getId();
                fillMap(grainType, grainMap);
            }

        return getIDMaxNeighbour(grainMap);
    }

    private int pentagonal(CellGrain[][] frame, int iG, int i, int iD, int jL, int j, int jR, NeighborhoodType type) {
        Map<Integer, Integer> grainMap = new HashMap<>();
        int grainType;

        int U = 0, R = 1, D = 2, L = 3;
        Random rand = new Random();
        int randDirection = rand.nextInt(4);

        if ((iG != -1 && jL != -1) && ((type == randomPentagonal && (randDirection != U && randDirection != L)) || (type != randomPentagonal && type != leftPentagonal && type != upPentagonal)))
            if (frame[iG][jL].getState() == GRAIN) {
                grainType = frame[iG][jL].getId();
                fillMap(grainType, grainMap);
            }

        if ((iG != -1) && ((type == randomPentagonal && randDirection != U) || (type != randomPentagonal && type != upPentagonal)))
            if (frame[iG][j].getState() == GRAIN) {
                grainType = frame[iG][j].getId();
                fillMap(grainType, grainMap);
            }

        if ((iG != -1 && jR != -1) && ((type == randomPentagonal && (randDirection != U && randDirection != R)) || (type != randomPentagonal && type != upPentagonal && type != rightPentagonal)))
            if (frame[iG][jR].getState() == GRAIN) {
                grainType = frame[iG][jR].getId();
                fillMap(grainType, grainMap);
            }

        if ((jL != -1) && ((type == randomPentagonal && randDirection != L) || (type != randomPentagonal && type != leftPentagonal)))
            if (frame[i][jL].getState() == GRAIN) {
                grainType = frame[i][jL].getId();
                fillMap(grainType, grainMap);
            }

        if ((jR != -1) && ((type == randomPentagonal && randDirection != R) || (type != randomPentagonal && type != rightPentagonal)))
            if (frame[i][jR].getState() == GRAIN) {
                grainType = frame[i][jR].getId();
                fillMap(grainType, grainMap);
            }

        if ((iD != -1 && jL != -1) && ((type == randomPentagonal && (randDirection != D && randDirection != L)) || (type != randomPentagonal && type != downPentagonal && type != leftPentagonal)))
            if (frame[iD][jL].getState() == GRAIN) {
                grainType = frame[iD][jL].getId();
                fillMap(grainType, grainMap);
            }

        if ((iD != -1) && ((type == randomPentagonal && randDirection != D) || (type != randomPentagonal && type != downPentagonal)))
            if (frame[iD][j].getState() == GRAIN) {
                grainType = frame[iD][j].getId();
                fillMap(grainType, grainMap);
            }

        if ((iD != -1 && jR != -1) && ((type == randomPentagonal && (randDirection != D && randDirection != R)) || (type != randomPentagonal && type != downPentagonal && type != rightPentagonal)))
            if (frame[iD][jR].getState() == GRAIN) {
                grainType = frame[iD][jR].getId();
                fillMap(grainType, grainMap);
            }

        return getIDMaxNeighbour(grainMap);
    }

    private int hexagonal(CellGrain[][] frame, int iG, int i, int iD, int jL, int j, int jR, NeighborhoodType type) {
        Map<Integer, Integer> grainMap = new HashMap<>();
        int grainType;

        if (iG != -1)
            if (frame[iG][j].getState() == GRAIN) {
                grainType = frame[iG][j].getId();
                fillMap(grainType, grainMap);
            }

        if (jL != -1)
            if (frame[i][jL].getState() == GRAIN) {
                grainType = frame[i][jL].getId();
                fillMap(grainType, grainMap);
            }

        if (jR != -1)
            if (frame[i][jR].getState() == GRAIN) {
                grainType = frame[i][jR].getId();
                fillMap(grainType, grainMap);
            }

        if (iD != -1)
            if (frame[iD][j].getState() == GRAIN) {
                grainType = frame[iD][j].getId();
                fillMap(grainType, grainMap);
            }

        Random rand = new Random();
        int los = rand.nextInt(2);
        if ((type == randomHexagonal && los == 0) || (type == leftHexagonal)) {
            if (iG != -1 && jL != -1)
                if (frame[iG][jL].getState() == GRAIN) {
                    grainType = frame[iG][jL].getId();
                    fillMap(grainType, grainMap);
                }

            if (iD != -1 && jR != -1)
                if (frame[iD][jR].getState() == GRAIN) {
                    grainType = frame[iD][jR].getId();
                    fillMap(grainType, grainMap);
                }
        } else if ((type == randomHexagonal && los == 1) || (type == rightHexagonal)) {
            if (iG != -1 && jR != -1)
                if (frame[iG][jR].getState() == GRAIN) {
                    grainType = frame[iG][jR].getId();
                    fillMap(grainType, grainMap);
                }

            if (iD != -1 && jL != -1)
                if (frame[iD][jL].getState() == GRAIN) {
                    grainType = frame[iD][jL].getId();
                    fillMap(grainType, grainMap);
                }
        }

        return getIDMaxNeighbour(grainMap);
    }

    private int checkNeighbours(CellGrain[][] frame, int height, int width) {
        int result = 0;

        int iG, i, iD, jL, j, jR;
        i = height;
        j = width;

        if (height == 0)
            if (edgeType == Closed) iG = -1;
            else iG = gridHeight - 1;
        else iG = height - 1;

        if (height == gridHeight - 1)
            if (edgeType == Closed) iD = -1;
            else iD = 0;
        else iD = height + 1;

        if (width == 0)
            if (edgeType == Closed) jL = -1;
            else jL = gridWidth - 1;
        else jL = width - 1;

        if (width == gridWidth - 1)
            if (edgeType == Closed) jR = -1;
            else jR = 0;
        else jR = width + 1;

        switch (neighborhoodType) {
            case vonNeuman:
                result = vonNeuman(frame, iG, i, iD, jL, j, jR);
                break;
            case Moore:
                result = moore(frame, iG, i, iD, jL, j, jR);
                break;
            case leftPentagonal:
                result = pentagonal(frame, iG, i, iD, jL, j, jR, leftPentagonal);
                break;
            case rightPentagonal:
                result = pentagonal(frame, iG, i, iD, jL, j, jR, rightPentagonal);
                break;
            case upPentagonal:
                result = pentagonal(frame, iG, i, iD, jL, j, jR, upPentagonal);
                break;
            case downPentagonal:
                result = pentagonal(frame, iG, i, iD, jL, j, jR, downPentagonal);
                break;
            case randomPentagonal:
                result = pentagonal(frame, iG, i, iD, jL, j, jR, randomPentagonal);
                break;
            case leftHexagonal:
                result = hexagonal(frame, iG, i, iD, jL, j, jR, leftHexagonal);
                break;
            case rightHexagonal:
                result = hexagonal(frame, iG, i, iD, jL, j, jR, rightHexagonal);
                break;
            case randomHexagonal:
                result = hexagonal(frame, iG, i, iD, jL, j, jR, randomHexagonal);
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
                if (frame[i][j].getState() == EMPTY) {
                    tmp[i][j].setId(checkNeighbours(frame, i, j));
                    if (tmp[i][j].getId() != 0) tmp[i][j].setState(GRAIN);
                } else if (frame[i][j].getState() == GRAIN) {
                    tmp[i][j].setState(GRAIN);
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

    private int getNumberOfEmptyGrains() {
        int amount = 0;

        for (int i = 0; i < gridHeight; i++)
            for (int j = 0; j < gridWidth; j++)
                if (grid[i][j].getState() == EMPTY) amount++;

        return amount;
    }

    private int getNumberOfAvailableGrains() {
        int amount = 0;

        for (int i = 0; i < gridHeight; i++)
            for (int j = 0; j < gridWidth; j++)
                if (grid[i][j].getAvailability() == AVAILABLE) amount++;

        return amount;
    }

    public int fillRandomly(int grainAmount) {
        Random rand = new Random();

        int counter = 0;

        int i = 0, limiter = grainAmount;
        if (listOfGrains.size() > 0) {
            limiter = listOfGrains.size() + grainAmount;
            i = listOfGrains.size();
        }

        for (; i < limiter; i++) {
            if (getNumberOfEmptyGrains() == 0)
                break;

            listOfGrains.add(new GrainType(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
            int x = rand.nextInt(gridHeight);
            int y = rand.nextInt(gridWidth);
            if (grid[x][y].getState() != GRAIN) {
                counter++;
                grid[x][y].setState(GRAIN);
                grid[x][y].setId(i + 1);
            } else i--;
        }

        return counter;
    }

    public int fillEvenlyPlacement(int grainAmount, int distance) {
        reset();
        Random rand = new Random();
        int maxOnHeight = gridHeight / (distance + 1);// - 1;
        int maxOnWidth = gridWidth / (distance + 1);// - 1;
        int maxGrainNumber = maxOnHeight * maxOnWidth;

        int x = distance, y = distance;
        int counter = 1, i = 0;

        for (; i < grainAmount; i++) {
            if (getNumberOfEmptyGrains() == 0 || i == maxGrainNumber)
                break;

            listOfGrains.add(new GrainType(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));

            grid[x][y].setState(GRAIN);
            grid[x][y].setId(i + 1);

            x += distance + 1;
            counter++;

            if (counter > maxOnHeight) {
                counter = 1;
                x = distance;
                y += distance + 1;
            }
        }

        return i;
    }

    private Coordinates getUp(int x, int y, int range) {
        int cx = x, cy = y;

        for (int i = 0; i < range; i++) {
            if (cx == 0)
                if (edgeType == Closed) break;
                else cx = gridHeight - 1;
            else cx--;
        }

        return new Coordinates(cx, cy);
    }

    private Coordinates getDown(int x, int y, int range) {
        int cx = x, cy = y;

        for (int i = 0; i < range; i++) {
            if (cx == gridHeight - 1)
                if (edgeType == Closed) break;
                else cx = 0;
            else cx++;
        }

        return new Coordinates(cx, cy);
    }

    private Coordinates getLeft(int x, int y, int range) {
        int cx = x, cy = y;

        for (int i = 0; i < range; i++) {
            if (cy == 0)
                if (edgeType == Closed) break;
                else cy = gridWidth - 1;
            else cy--;
        }

        return new Coordinates(cx, cy);
    }

    private Coordinates getRight(int x, int y, int range) {
        int cx = x, cy = y;

        for (int i = 0; i < range; i++) {
            if (cy == gridWidth - 1)
                if (edgeType == Closed) break;
                else cy = 0;
            else cy++;
        }

        return new Coordinates(cx, cy);
    }

    private void setRadius(int x, int y, int radius) {
        Coordinates startCell = new Coordinates(x, y);
        startCell = getLeft(startCell.x, startCell.y, radius);
        startCell = getUp(startCell.x, startCell.y, radius);

        Coordinates tmpCell = new Coordinates(startCell.x, startCell.y);
        grid[tmpCell.x][tmpCell.y].setAvailability(UNAVAILABLE);

        for (int i = 0; i < 2 * radius + 1; i++) {
            for (int j = 0; j < 2 * radius + 1; j++) {
                tmpCell = getRight(tmpCell.x, tmpCell.y, 1);
                grid[tmpCell.x][tmpCell.y].setAvailability(UNAVAILABLE);
            }
            tmpCell = getLeft(tmpCell.x, tmpCell.y, 2 * radius + 1);
            tmpCell = getDown(tmpCell.x, tmpCell.y, 1);
            grid[tmpCell.x][tmpCell.y].setAvailability(UNAVAILABLE);
        }
    }

    public int fillRandomlyWithRadius(int grainAmount, int radius) {
        reset();
        Random rand = new Random();

        int i = 0, counter = 0, limit = 1000;

        for (; i < grainAmount; i++) {
            if (getNumberOfAvailableGrains() == 0)
                break;

            int x = rand.nextInt(gridHeight);
            int y = rand.nextInt(gridWidth);

            if (grid[x][y].getAvailability() == AVAILABLE) {
                listOfGrains.add(new GrainType(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
                grid[x][y].setState(GRAIN);
                grid[x][y].setAvailability(UNAVAILABLE);
                grid[x][y].setId(i + 1);
                setRadius(x, y, radius);
            } else {
                i--;

                //zabezpieczenie - jeśli będą dostępne ziarna, ale nie będzie w stanie w nie trafić w sposób losowy
                if (++counter == limit)
                    break;
            }
        }

        return i;
    }

    public void addSingleGrain(int x, int y) {
        Random rand = new Random();

        if (grid[x][y].getState() == EMPTY) {
            int i = 0;
            if (listOfGrains.size() > 0)
                i = listOfGrains.size();

            listOfGrains.add(new GrainType(rand.nextDouble(), rand.nextDouble(), rand.nextDouble()));
            grid[x][y].setState(GRAIN);
            grid[x][y].setId(i + 1);
        } else if (grid[x][y].getState() == GRAIN) {
            grid[x][y].setState(EMPTY);
            grid[x][y].setId(0);
        }
    }

    public List getListOfGrains() {
        return listOfGrains;
    }

    public void setNeighborhoodType(NeighborhoodType neighborhoodType) {
        this.neighborhoodType = neighborhoodType;
    }

    public void reset() {
        listOfGrains = new ArrayList<>();
        createGrid();
    }
}