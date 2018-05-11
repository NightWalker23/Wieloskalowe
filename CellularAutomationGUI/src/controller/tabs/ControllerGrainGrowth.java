package controller.tabs;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import model.Global;
import model.ModelGrainGrowth;

import static model.ModelGrainGrowth.*;

import model.painters.PainterGrainGrowth;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerGrainGrowth implements Initializable {
    @FXML
    RadioButton radio10, radio20, radio50, radio100, radio200, radio500, radio1000;
    @FXML
    TextField randomCellsField;
    @FXML
    ChoiceBox<String> choiceBoxGridSize;
    @FXML
    ChoiceBox<String> choiceBoxNeighborhoodType;
    @FXML
    ChoiceBox<String> choiceBoxEdgeType;
    @FXML
    Button startButton, pauseButton, stopButton;
    @FXML
    Canvas canvas2D;

    private ToggleGroup group = new ToggleGroup();
    private GraphicsContext gc;
    private ModelGrainGrowth model;
    private int gridHeight, gridWidth;
    private PainterGrainGrowth painter;
    private Thread thread;
    private NeighborhoodType nType;
    private EdgeType eType;

    private void cleanCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas2D.getWidth(), canvas2D.getHeight());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String[] options = new String[]{"10x10", "20x20", "40x40", "50x50", "100x100", "200x200", "300x300", "600x600"};
        choiceBoxGridSize.setItems(FXCollections.observableArrayList(options));
        choiceBoxGridSize.setValue("10x10");
        gridHeight = 10;
        gridWidth = 10;

        choiceBoxGridSize.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            final int value = newValue.intValue();

            switch (value) {
                case 0:
                    gridHeight = 10;
                    gridWidth = 10;
                    break;
                case 1:
                    gridHeight = 20;
                    gridWidth = 20;
                    break;
                case 2:
                    gridHeight = 40;
                    gridWidth = 40;
                    break;
                case 3:
                    gridHeight = 50;
                    gridWidth = 50;
                    break;
                case 4:
                    gridHeight = 100;
                    gridWidth = 100;
                    break;
                case 5:
                    gridHeight = 200;
                    gridWidth = 200;
                    break;
                case 6:
                    gridHeight = 300;
                    gridWidth = 300;
                    break;
                case 7:
                    gridHeight = 600;
                    gridWidth = 600;
                    break;
            }
        });


        String[] neighboursOptions = new String[]{"von Neuman", "Moore", "Pentagonal random", "Hexagonal random"};
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
                    nType = NeighborhoodType.randomPentagonal;
                    break;
                case 3:
                    nType = NeighborhoodType.randomHexagonal;
                    break;
            }
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

        //dołączenie radioButtonów do grupy
        radio10.setToggleGroup(group);
        radio10.setSelected(true);
        radio20.setToggleGroup(group);
        radio50.setToggleGroup(group);
        radio100.setToggleGroup(group);
        radio200.setToggleGroup(group);
        radio500.setToggleGroup(group);
        radio1000.setToggleGroup(group);

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> Global.animationSpeedGrainGrowth = setSpeed());

        //blokowanie wpisywanie wartości innych niż liczbowych
        randomCellsField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.matches("\\d*"))
                    randomCellsField.setText(oldValue);
            } catch (Exception ignored) {
            }
        });
    }

    private int setSpeed() {
        int speed = 0;
        if (group.getSelectedToggle() == radio10) speed = 10;
        else if (group.getSelectedToggle() == radio20) speed = 20;
        else if (group.getSelectedToggle() == radio50) speed = 50;
        else if (group.getSelectedToggle() == radio100) speed = 100;
        else if (group.getSelectedToggle() == radio200) speed = 200;
        else if (group.getSelectedToggle() == radio500) speed = 500;
        else if (group.getSelectedToggle() == radio1000) speed = 1000;

        return speed;
    }

    public void start(ActionEvent actionEvent) {
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
        choiceBoxGridSize.setDisable(true);
        choiceBoxNeighborhoodType.setDisable(true);
        choiceBoxEdgeType.setDisable(true);

        cleanCanvas();

        //uruchomienie Garbage Collectora
        System.gc();

        int grainAmount = Integer.parseInt(randomCellsField.getText());
        model = new ModelGrainGrowth(gridHeight, gridWidth, nType, grainAmount, eType);

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
        choiceBoxGridSize.setDisable(false);
        choiceBoxNeighborhoodType.setDisable(false);
        choiceBoxEdgeType.setDisable(false);

        painter.stop();
    }

    public void resetButtons(){
        stop(new ActionEvent());
    }
}
