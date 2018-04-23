package controller.tabs;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import model.Model1D;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller1D implements Initializable {
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
    private Color background = Color.WHITE, square = Color.BLACK;

    //czyszczenie canvasa
    private void cleanCanvas() {
        gc.setFill(background);
        gc.fillRect(0, 0, canvas1D.getWidth(), canvas1D.getHeight());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cellSize = 10;
        ruleNumber = 90;
        type = Model1D.Type.NORMAL;

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
        try{
            cellSize = Integer.parseInt(gridField.getText());
            if (cellSize > 300) //bo 600 to max siatki
                cellSize = 300;
            if (cellSize < 1)
                cellSize = 1;
        }
        catch (Exception ignored){}
    }

    private void setRule() {
        try{
            int tmp = Integer.parseInt(ruleField.getText());
            if (tmp >= 0 && tmp <= 255)
                ruleNumber = tmp;
        }
        catch (Exception ignored) {}
    }


    public void draw(ActionEvent actionEvent) {
        setGridWidth();
        setRule();
        cleanCanvas();

        Random rand = new Random();
        double R, G, B;
        R = rand.nextDouble();
        G = rand.nextDouble();
        B = rand.nextDouble();
        square = Color.color(R, G,B);
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

}
