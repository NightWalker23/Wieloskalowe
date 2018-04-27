package controller.tabs;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import model.cells.CellGameOfLife;
import model.Model1D;
import model.ModelGameOfLife;
import model.Painter;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerGameOfLife implements Initializable {
    @FXML
    RadioButton radio10, radio20, radio50, radio100, radio200, radio500, radio1000;
    @FXML
    TextField randomCellsField;
    @FXML
    ChoiceBox<String> choiceBoxGridSize;
    @FXML
    Button startButton, pauseButton, stopButton, randomFillButton, resetButton;
    @FXML
    Canvas canvas2D;

    private ToggleGroup group = new ToggleGroup();
    private GraphicsContext gc;
    private ModelGameOfLife model, modelRandom;
    private int gridHeight, gridWidth;
    private Painter painter;
    private Thread thread;
    private boolean isNewRandom, isNewSize; //zmienne pomocnicze podczas sterowania GUI

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

        //listener do choice boxa
        choiceBoxGridSize.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            final int value = newValue.intValue();
            isNewSize = true;

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

        //blokowanie wpisywanie wartości innych niż liczbowych
        randomCellsField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.matches("\\d*"))
                    randomCellsField.setText(oldValue);
            } catch (Exception ignored) {
            }
        });

        isNewRandom = false;
        isNewSize = false;
        model = new ModelGameOfLife(gridHeight, gridWidth);
    }

    public void start(ActionEvent actionEvent) {
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
        randomFillButton.setDisable(true);
        resetButton.setDisable(true);
        choiceBoxGridSize.setDisable(true);

        cleanCanvas();

        int speed = 10;
        if (group.getSelectedToggle() == radio10) speed = 10;
        else if (group.getSelectedToggle() == radio20) speed = 20;
        else if (group.getSelectedToggle() == radio50) speed = 50;
        else if (group.getSelectedToggle() == radio100) speed = 100;
        else if (group.getSelectedToggle() == radio200) speed = 200;
        else if (group.getSelectedToggle() == radio500) speed = 500;
        else if (group.getSelectedToggle() == radio1000) speed = 1000;

        //jeśli wypełniliśmy losowo, to bierzemy ten losowy
        if (isNewRandom)
            model = modelRandom;

        //jeśli od ostatniego startu zmienił się rozmiar Canvasa to zerujemy
        //bo jeśli się nie zmienił, to będzie działać jak pause/resume
        if (isNewSize)
            model = new ModelGameOfLife(gridHeight, gridWidth);

        painter = new Painter(canvas2D, model, gc, speed);
        thread = new Thread(painter);
        thread.setDaemon(true);
        thread.start();

        isNewRandom = false;
        isNewSize = false;
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
        randomFillButton.setDisable(false);
        resetButton.setDisable(false);
        choiceBoxGridSize.setDisable(false);

        painter.stop();
    }


    public void randomFill(ActionEvent actionEvent) {
        isNewRandom = true;
        isNewSize = false;


        int amount;
        try {
            amount = Integer.parseInt(randomCellsField.getText());
            if (amount > 0) {
                modelRandom = new ModelGameOfLife(gridHeight, gridWidth);
                modelRandom.clearGrid();
                modelRandom.fillRandomly(amount);

                int height = (int) canvas2D.getHeight() / modelRandom.getGridHeight();
                int width = (int) canvas2D.getWidth() / modelRandom.getGridWidth();
                Platform.runLater(() -> {
                    CellGameOfLife[][] tab = modelRandom.getGrid();
                    cleanCanvas();
                    gc.setFill(Color.BLACK);
                    for (int i = 0; i < modelRandom.getGridHeight(); i++) {
                        for (int j = 0; j < modelRandom.getGridWidth(); j++)
                            if (tab[i][j].getState() == Model1D.Option.ALIVE)
                                gc.fillRect(j * width, i * height, height, width);
                    }
                });
            }
        }
        catch (Exception ignored){}
    }

    public void reset(ActionEvent actionEvent) {
        cleanCanvas();
        model.clearGrid();
    }
}
