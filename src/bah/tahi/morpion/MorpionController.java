package bah.tahi.morpion;

import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MorpionController {
    @FXML
    private GridPane grid;

    @FXML
    private Button restartButton;

    @FXML
    private Label gameOverLabel;

    @FXML
    private Label freeCasesLabel;

    @FXML
    private Label firstCasesLabel;

    @FXML
    private Label secondCasesLabel;

    private final TicTacToeModel model = TicTacToeModel.getInstance();

    //@Override
    public void initialize() {

        // Initialiser les objets TicTacToeSquare
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // Initialisez tous les objets TicTacToeSquare de manière similaire
                TicTacToeSquare square = new TicTacToeSquare(i, j);

                // Lier les propriétés des objets TicTacToeSquare aux propriétés du modèle TicTacToeModel
                square.ownerProperty().bindBidirectional(model.getSquare(i, j));
                //square.colorProperty().bind(model.getWinningSquare(0, 0)); // Bug

                // Ajouter les square à la grille
                this.grid.add(square, i, j);
            }
        }

        // Textes de base
        firstCasesLabel.setText("0 case pour X");
        secondCasesLabel.setText("0 case pour O");
        freeCasesLabel.setText(model.getNbCases() + " cases libres");

        // Binding pour le style CSS du label du premier joueur
        firstCasesLabel.styleProperty().bind(new StringBinding() {
            {
                bind(model.turnProperty(), model.gameOver());
            }

            @Override
            protected String computeValue() {
                if ( model.gameOver().get() || !model.turnProperty().get().equals(Owner.FIRST)) {
                    return "-fx-background-color: red; -fx-text-fill: white;";
                } else {
                    return "-fx-background-color: cyan; -fx-text-fill: black;";
                }
            }
        });

        // Binding pour le style CSS du label du deuxième joueur
        secondCasesLabel.styleProperty().bind(new StringBinding() {
            {
                bind(model.turnProperty(), model.gameOver());
            }

            @Override
            protected String computeValue() {
                if ( model.gameOver().get() || !model.turnProperty().get().equals(Owner.SECOND)) {
                    return "-fx-background-color: red; -fx-text-fill: white;";
                } else {
                    return "-fx-background-color: cyan; -fx-text-fill: black;";
                }
            }
        });

        // Lier d'autres éléments de l'interface utilisateur aux propriétés du modèle TicTacToeModel
        restartButton.setOnAction(event -> model.restart());
        gameOverLabel.textProperty().bind(model.getEndOfGameMessage());

        model.gameOver().addListener((observable, oldValue, newValue) -> {
            freeCasesLabel.setVisible(!newValue);
        });

        model.getScore(Owner.FIRST).addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > 1) {
                firstCasesLabel.setText(newValue.toString() + " cases pour X");
            } else {
                firstCasesLabel.setText(newValue.toString() + " case pour X");
            }
        });

        model.getScore(Owner.SECOND).addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > 1) {
                secondCasesLabel.setText(newValue.toString() + " cases pour O");
            } else {
                secondCasesLabel.setText(newValue.toString() + " case pour O");
            }
        });

        model.getScore(Owner.NONE).addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() > 1) {
                freeCasesLabel.setText(newValue.toString() + " cases libres");
            } else {
                freeCasesLabel.setText(newValue.toString() + " case libre");
            }
        });
    }
}
