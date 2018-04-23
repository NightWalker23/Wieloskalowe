package controller.tabs;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.Model1D;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller1D implements Initializable {
    @FXML
    Slider sliderCellSize, sliderRuleNumber,
            sliderBackgroundR, sliderBackgroundG, sliderBackgroundB,
            sliderCellColorR, sliderCellColorG, sliderCellColorB;
    @FXML
    TextField gridField, ruleField;
    @FXML
    Button drawButton;
    @FXML
    Canvas canvas1D;
    @FXML
    RadioButton radioNormal, radioPeriodic;

    private ToggleGroup group = new ToggleGroup(); //grupuje radio buttony
    private GraphicsContext gc;
    private Model1D rule;
    private int cellSize, ruleNumber, type;
    private Color background, square;
    private double bR, bG, bB, cR, cG, cB;

    //czyszczenie canvasa
    private void cleanCanvas() {
        background = Color.color(bR, bG, bB);
        gc.setFill(background);
        gc.fillRect(0, 0, canvas1D.getWidth(), canvas1D.getHeight());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cellSize = 1;
        ruleNumber = 0;
        bR = 1;
        bG = 1;
        bB = 1;
        cR = 0;
        cG = 0;
        cB = 0;
        type = Model1D.Type.NORMAL;

        sliderCellSize.setMin(1);
        sliderCellSize.setMax(300);

        sliderRuleNumber.setMin(0);
        sliderRuleNumber.setMax(255);

        sliderBackgroundR.setMin(0);
        sliderBackgroundR.setMax(1);
        sliderBackgroundR.setMajorTickUnit(0.004);
        sliderBackgroundR.setValue(1);
        sliderBackgroundG.setMin(0);
        sliderBackgroundG.setMax(1);
        sliderBackgroundG.setMajorTickUnit(0.004);
        sliderBackgroundG.setValue(1);
        sliderBackgroundB.setMin(0);
        sliderBackgroundB.setMax(1);
        sliderBackgroundB.setMajorTickUnit(0.004);
        sliderBackgroundB.setValue(1);

        sliderCellColorR.setMin(0);
        sliderCellColorR.setMax(1);
        sliderCellColorR.setMajorTickUnit(0.004);
        sliderCellColorG.setMin(0);
        sliderCellColorG.setMax(1);
        sliderCellColorG.setMajorTickUnit(0.004);
        sliderCellColorB.setMin(0);
        sliderCellColorB.setMax(1);
        sliderCellColorB.setMajorTickUnit(0.004);

        radioNormal.setToggleGroup(group);
        radioNormal.setSelected(true);
        radioPeriodic.setToggleGroup(group);

        gc = canvas1D.getGraphicsContext2D();
        cleanCanvas();

        //blokowanie wpisywanie wartości innych niż liczbowych
        gridField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.matches("\\d*"))
                    gridField.setText(oldValue);
            } catch (Exception ignored) {
            }
        });

        ruleField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.matches("\\d*"))
                    ruleField.setText(oldValue);
            } catch (Exception ignored) {
            }
        });
    }

    private void setGridWidth() {
        try {
            cellSize = Integer.parseInt(gridField.getText());
            if (cellSize > 300) //bo 600 to max siatki
                cellSize = 300;
            if (cellSize < 1)
                cellSize = 1;
        } catch (Exception ignored) {
        }
    }

    private void setRule() {
        try {
            int tmp = Integer.parseInt(ruleField.getText());
            if (tmp >= 0 && tmp <= 255)
                ruleNumber = tmp;
        } catch (Exception ignored) {
        }
    }


    public void draw(ActionEvent actionEvent) {
        setGridWidth();
        setRule();
        cleanCanvas();

        square = Color.color(cR, cG, cB);
        gc.setFill(square);

        int amount = (int) (canvas1D.getWidth() / cellSize);

        if (group.getSelectedToggle() == radioNormal) type = Model1D.Type.NORMAL;
        else if (group.getSelectedToggle() == radioPeriodic) type = Model1D.Type.PERIODIC;

        rule = new Model1D(amount, ruleNumber, type);

        final int[][] tab = {rule.getTab()};
        Platform.runLater(() -> {
            for (int i = 0; i < amount; i++) {
                for (int j = 0; j < amount; j++)
                    if (tab[0][j] == Model1D.Option.ALIVE)
                        gc.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                tab[0] = rule.getResult(rule.getTab());
            }
        });
    }

    public void sliderSetCell(MouseEvent mouseEvent) {
        cellSize = (int) sliderCellSize.getValue();
        if (((int) canvas1D.getWidth()) % cellSize == 0)
            gridField.setText(Integer.toString(cellSize));
    }

    public void sliderSetRule(MouseEvent mouseEvent) {
        ruleNumber = (int) sliderRuleNumber.getValue();
        ruleField.setText(Integer.toString(ruleNumber));
    }

    public void setBackgroundR(MouseEvent mouseEvent) {
        bR = sliderBackgroundR.getValue();
    }

    public void setBackgroundG(MouseEvent mouseEvent) {
        bG = sliderBackgroundG.getValue();
    }

    public void setBackgroundB(MouseEvent mouseEvent) {
        bB = sliderBackgroundB.getValue();
    }

    public void setCellR(MouseEvent mouseEvent) {
        cR = sliderCellColorR.getValue();
    }

    public void setCellG(MouseEvent mouseEvent) {
        cG = sliderCellColorG.getValue();
    }

    public void setCellB(MouseEvent mouseEvent) {
        cB = sliderCellColorB.getValue();
    }
}
