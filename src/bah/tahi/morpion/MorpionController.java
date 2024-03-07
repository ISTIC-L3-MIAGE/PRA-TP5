package bah.tahi.morpion;

import javafx.beans.binding.StringBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class MorpionController {
    @FXML
    private GridPane grid; // Plateau de jeu sur la vue

    @FXML
    private Button restartButton; // Bouton RESTART

    @FXML
    private Label gameOverLabel; // Message de fin de partie

    @FXML
    private Label freeCasesLabel; // Nombre de cases libres

    @FXML
    private Label firstCasesLabel; // Score du premier joueur

    @FXML
    private Label secondCasesLabel; // Score du second joueur

    private final TicTacToeModel model = TicTacToeModel.getInstance(); // Instance du modèle de jeu

    @FXML
    public void initialize() {

        // Création des cases et liaison au modèle de jeu
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                TicTacToeSquare square = new TicTacToeSquare(i, j);
                square.ownerProperty().bindBidirectional(model.getSquare(i, j));
                square.winnerProperty().bindBidirectional(model.getWinningSquare(i, j));
                grid.add(square, j, i);
            }
        }

        // Textes de base
        firstCasesLabel.setText("0 case pour X");
        secondCasesLabel.setText("0 case pour O");
        freeCasesLabel.setText(model.getNbCases() + " cases libres");

        // Lier d'autres éléments de l'interface utilisateur aux propriétés du modèle TicTacToeModel
        restartButton.setOnAction(event -> model.restart());
        gameOverLabel.textProperty().bind(model.getEndOfGameMessage());

        freeCasesLabel.visibleProperty().bind(model.gameOver().not());

        firstCasesLabel.textProperty().bind(model.getScore(Owner.FIRST).asString().concat(" case(s) pour X"));
        firstCasesLabel.styleProperty().bind(model.getOwnerLabelStyle(Owner.FIRST));

        secondCasesLabel.textProperty().bind(model.getScore(Owner.SECOND).asString().concat(" case(s) pour O"));
        secondCasesLabel.styleProperty().bind(model.getOwnerLabelStyle(Owner.SECOND));

        freeCasesLabel.textProperty().bind(model.getScore(Owner.NONE).asString().concat(" case(s) libre(s)"));
    }
}
