package bah.tahi.morpion;

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

        // Lier d'autres éléments de l'interface utilisateur aux propriétés du modèle TicTacToeModel
        restartButton.setOnAction(event -> model.restart());
        gameOverLabel.textProperty().bind(model.getEndOfGameMessage());
        //freeCasesLabel.textProperty().bind(model.getFreeCasesMessage());
        firstCasesLabel.textProperty().bind(model.getScoreMessage(Owner.FIRST));
        secondCasesLabel.textProperty().bind(model.getScoreMessage(Owner.SECOND));
        freeCasesLabel.textProperty().bind(model.getScoreMessage(Owner.NONE));
    }
}
