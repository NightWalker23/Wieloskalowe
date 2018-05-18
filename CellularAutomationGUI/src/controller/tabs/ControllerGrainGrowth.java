package controller.tabs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import model.Global;
import model.ModelGrainGrowth;

import static model.ModelGrainGrowth.*;

import model.cells.CellGrain;
import model.painters.PainterGrainGrowth;

import java.net.URL;
import java.util.ResourceBundle;

import static model.cells.CellGrain.State.*;

public class ControllerGrainGrowth implements Initializable {
    @FXML
    AnchorPane pane;
    @FXML
    Label labelGridHeight, labelGridWidth, labelAnimationSpeed, labelGridSize;
    @FXML
    Slider sliderGridHeight, sliderGridWidth, sliderAnimationSpeed;
    @FXML
    TextField randomCellsField;
    @FXML
    ChoiceBox<String> choiceBoxNeighborhoodType;
    @FXML
    ChoiceBox<String> choiceBoxEdgeType;
    @FXML
    Button startButton, pauseButton, stopButton, fillRandomlyButton, resetButton;
    @FXML
    Canvas canvas2D;

    private GraphicsContext gc;
    private ModelGrainGrowth model;
    private int gridHeight, gridWidth;
    private PainterGrainGrowth painter;
    private Thread thread;
    private NeighborhoodType nType;
    private EdgeType eType;
    private int grainHeight, grainWidth;

    private void cleanCanvas()
    {
        gc.setFill(Color.WHITE);
        gc.clearRect(0,0,canvas2D.getHeight(),canvas2D.getWidth());
        if (model != null) gc.fillRect(0, 0, model.getGridWidth()*grainHeight, model.getGridHeight()*grainWidth);
        else gc.fillRect(0, 0,canvas2D.getHeight()*grainHeight,canvas2D.getWidth()*grainWidth);
    }

    private void displayAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas2D.setHeight(600);
        canvas2D.setWidth(600);

        String[] neighboursOptions = new String[]{"von Neuman", "Moore", "Pentagonal left", "Pentagonal right", "Pentagonal up", "Pentagonal down", "Pentagonal random", "Hexagonal left", "Hexagonal right", "Hexagonal random"};
        choiceBoxNeighborhoodType.setItems(FXCollections.observableArrayList(neighboursOptions));
        choiceBoxNeighborhoodType.setValue("von Neuman");
        nType = NeighborhoodType.vonNeuman;
        choiceBoxNeighborhoodType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            final int value = newValue.intValue();

            switch (value) {
                case 0:
                    nType = NeighborhoodType.vonNeuman;
                    break;
                case 1:
                    nType = NeighborhoodType.Moore;
                    break;
                case 2:
                    nType = NeighborhoodType.leftPentagonal;
                    break;
                case 3:
                    nType = NeighborhoodType.rightPentagonal;
                    break;
                case 4:
                    nType = NeighborhoodType.upPentagonal;
                    break;
                case 5:
                    nType = NeighborhoodType.downPentagonal;
                    break;
                case 6:
                    nType = NeighborhoodType.randomPentagonal;
                    break;
                case 7:
                    nType = NeighborhoodType.leftHexagonal;
                    break;
                case 8:
                    nType = NeighborhoodType.rightHexagonal;
                    break;
                case 9:
                    nType = NeighborhoodType.randomHexagonal;
                    break;
            }

            if (model != null)
                model.setNeighborhoodType(nType);
        });

        String[] edgeOptions = new String[]{"Closed", "Periodic"};
        choiceBoxEdgeType.setItems(FXCollections.observableArrayList(edgeOptions));
        choiceBoxEdgeType.setValue("Closed");
        eType = EdgeType.Closed;
        choiceBoxEdgeType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            final int value = newValue.intValue();

            switch (value) {
                case 0:
                    eType = EdgeType.Closed;
                    break;
                case 1:
                    eType = EdgeType.Periodic;
                    break;
            }
        });

        pauseButton.setDisable(true);
        stopButton.setDisable(true);

        gc = canvas2D.getGraphicsContext2D();
        cleanCanvas();

        //blokowanie wpisywanie wartości innych niż liczbowych
        randomCellsField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.matches("\\d*"))
                    randomCellsField.setText(oldValue);
            } catch (Exception ignored) {
            }
        });

        sliderGridHeight.setMin(1);
        sliderGridHeight.setMax(canvas2D.getHeight());

        sliderGridWidth.setMin(1);
        sliderGridWidth.setMax(canvas2D.getWidth());

        grainHeight = 1;
        grainWidth = 1;

        sliderAnimationSpeed.setMin(10);
        sliderAnimationSpeed.setMax(1000);
        sliderAnimationSpeed.setValue(10);

        gridWidth = (int) canvas2D.getHeight() / grainHeight;
        gridHeight = (int) canvas2D.getWidth() / grainWidth;
        model = new ModelGrainGrowth(gridHeight, gridWidth, nType, eType);
    }

    private void setLabelGridSize() {
        labelGridSize.setText("Grid size: " + gridHeight + "x" + gridWidth);
    }

    public void setGridHeight(MouseEvent mouseEvent) {
        grainWidth = (int) sliderGridHeight.getValue();
        Global.grainWidth = grainWidth;
        labelGridHeight.setText(Integer.toString(grainWidth));
        setLabelGridSize();
    }

    public void gridHeightPressed(KeyEvent keyEvent) {
        grainWidth = (int) sliderGridHeight.getValue();
        gridHeight = (int) canvas2D.getWidth() / grainWidth;
        Global.grainWidth = grainWidth;
        labelGridHeight.setText(Integer.toString(grainWidth));
        setLabelGridSize();
        if (model != null) {
            model.setGridHeight(gridHeight);
            model.createGrid();
        }
    }

    public void setGridWidth(MouseEvent mouseEvent) {
        grainHeight = (int) sliderGridWidth.getValue();
        Global.grainHeight = grainHeight;
        labelGridWidth.setText(Integer.toString(grainHeight));
        setLabelGridSize();
    }

    public void gridWidthPressed(KeyEvent keyEvent) {
        grainHeight = (int) sliderGridWidth.getValue();
        gridWidth = (int) canvas2D.getHeight() / grainHeight;
        Global.grainHeight = grainHeight;
        labelGridWidth.setText(Integer.toString(grainHeight));
        setLabelGridSize();
        if (model != null) {
            model.setGridWidth(gridWidth);
            model.createGrid();
        }
    }

    public void heightDone(MouseEvent dragEvent) {
        gridHeight = (int) canvas2D.getWidth() / grainWidth;
        if (model != null) {
            model.setGridHeight(gridHeight);
            model.createGrid();
        }
    }

    public void widthtDone(MouseEvent dragEvent) {
        gridWidth = (int) canvas2D.getHeight() / grainHeight;
        if (model != null) {
            model.setGridWidth(gridWidth);
            model.createGrid();
        }
    }

    public void setAnimationSpeed(MouseEvent mouseEvent) {
        Global.animationSpeedGrainGrowth = (int) sliderAnimationSpeed.getValue();
        labelAnimationSpeed.setText(Integer.toString((int) sliderAnimationSpeed.getValue()));
    }

    public void animationSpeedPressed(KeyEvent keyEvent) {
        Global.animationSpeedGrainGrowth = (int) sliderAnimationSpeed.getValue();
        labelAnimationSpeed.setText(Integer.toString((int) sliderAnimationSpeed.getValue()));
    }

    private void refreshCanvas() {
        Platform.runLater(() -> {
            CellGrain[][] tab;

            tab = model.getGrid();
            cleanCanvas();

            int height = Global.grainHeight;
            int width = Global.grainWidth;
            for (int i = 0; i < model.getGridHeight(); i++) {
                for (int j = 0; j < model.getGridWidth(); j++) {
                    if (tab[i][j].getState() == CellGrain.State.GRAIN) {
                        gc.setFill(((ModelGrainGrowth.GrainType) model.getListOfGrains().get(tab[i][j].getId() - 1)).getGrainColor());
                        gc.fillRect(j * height, i * width, height, width);
                    }
                }
            }
        });
    }

    public void fillRandomly(ActionEvent actionEvent) {
        int grainAmount;
        try {
            grainAmount = Integer.parseInt(randomCellsField.getText());
        } catch (NumberFormatException e) {
            grainAmount = 1;
        }

        if (model != null) {
            if (model.fillRandomly(grainAmount)) refreshCanvas();
            else displayAlert("Nie ma wystarczająco dużo miejsca aby dodać " + grainAmount + " dodatkowych ziaren.");
        }
    }

    @FXML
    private void addGrainOnCanvas(MouseEvent mouseEvent) {
        int x0 = 10, y0 = 39; //współrzędne początka canvasa w okienku
        int x = (int) mouseEvent.getSceneX() - x0, y = (int) mouseEvent.getSceneY() - y0; //współrzędne w okienku

        //rozmiary komórki
        int height = grainHeight;
        int width = grainWidth;

        //pozycja komórki w oknie
        int canvasX = (x / height) * height;
        int canvasY = (y / width) * width;

        //pozycja komórki w siatce
        int gridX = canvasX / height;
        int gridY = canvasY / width;

        if (!(gridX > model.getGridWidth() - 1 || gridX < 0 || gridY > model.getGridHeight() - 1 || gridY < 0)){
            model.addSingleGrain(gridY, gridX);
            refreshCanvas();
        }
    }

    public void reset(ActionEvent actionEvent) {
        model.reset();
        cleanCanvas();
    }

    public void start(ActionEvent actionEvent) {
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
        choiceBoxEdgeType.setDisable(true);
        sliderGridHeight.setDisable(true);
        sliderGridWidth.setDisable(true);

        //uruchomienie Garbage Collectora
        System.gc();

        painter = new PainterGrainGrowth(canvas2D, model, gc, this);
        thread = new Thread(painter);
        thread.setDaemon(true);
        thread.start();
    }

    public void pause(ActionEvent actionEvent) {
        startButton.setDisable(true);

        if (painter.isPaused())
            painter.resume();
        else
            painter.pause();
    }

    public void stop(ActionEvent actionEvent) {
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        choiceBoxEdgeType.setDisable(false);
        sliderGridHeight.setDisable(false);
        sliderGridWidth.setDisable(false);

        painter.stop();
    }

    public void resetButtons() {
        stop(new ActionEvent());
    }
}
