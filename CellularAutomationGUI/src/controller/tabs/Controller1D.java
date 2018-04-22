package controller.tabs;

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
    private int cellNumber, ruleNumber, type;
    private Color background = Color.WHITE, square = Color.BLACK;

    //czyszczenie canvasa
    private void cleanCanvas() {
        gc.setFill(background);
        gc.fillRect(0, 0, canvas1D.getWidth(), canvas1D.getHeight());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cellNumber = 10;
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
            cellNumber = Integer.parseInt(gridField.getText());
            if (cellNumber > 600) //bo 600 to max siatki
                cellNumber = 600;
        }
        catch (Exception ignored){}
    }

    private void setRule() {
        try{
            ruleNumber = Integer.parseInt(ruleField.getText());
        }
        catch (Exception ignored) {}
    }


    public void draw(ActionEvent actionEvent) {
        setGridWidth();
        setRule();
        cleanCanvas();
        int rozmiar = (int) (canvas1D.getWidth() / cellNumber);

        if (group.getSelectedToggle() == radioNormal) type = Model1D.Type.NORMAL;
        else if (group.getSelectedToggle() == radioPeriodic) type = Model1D.Type.PERIODIC;

        rule = new Model1D(cellNumber, ruleNumber, type);
        int[] tab = rule.getTab();

        gc.setFill(square);
        for (int i = 0; i < cellNumber; i++) {
            for (int j = 0; j < cellNumber; j++)
                if (tab[j] == Model1D.Option.ALIVE)
                    gc.fillRect(j * rozmiar, i * rozmiar, rozmiar, rozmiar);

            tab = rule.getResult(rule.getTab());
        }
    }

}
