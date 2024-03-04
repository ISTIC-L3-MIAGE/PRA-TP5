package bah.tahi.morpion;

import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

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

                // Ajouter les square à la grille
                //this.grid.add(square, i, j);
                this.grid.add(square, j, i);
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
                if (model.gameOver().get() || !model.turnProperty().get().equals(Owner.FIRST)) {
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
                if (model.gameOver().get() || !model.turnProperty().get().equals(Owner.SECOND)) {
                    return "-fx-background-color: red; -fx-text-fill: white;";
                } else {
                    return "-fx-background-color: cyan; -fx-text-fill: black;";
                }
            }
        });


        // Lier d'autres éléments de l'interface utilisateur aux propriétés du modèle TicTacToeModel
        restartButton.setOnAction(event -> model.restart());
        gameOverLabel.textProperty().bind(model.getEndOfGameMessage());

        freeCasesLabel.visibleProperty().bind(model.gameOver().not());

        firstCasesLabel.textProperty().bind(model.getScore(Owner.FIRST).asString().concat(" case(s) pour X"));
        secondCasesLabel.textProperty().bind(model.getScore(Owner.SECOND).asString().concat(" case(s) pour O"));
        freeCasesLabel.textProperty().bind(model.getScore(Owner.NONE).asString().concat(" case(s) libre(s)"));
    }
}
