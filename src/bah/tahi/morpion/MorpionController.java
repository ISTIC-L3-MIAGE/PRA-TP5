package bah.tahi.morpion;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MorpionController {
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
                TicTacToeSquare square = new TicTacToeSquare(i, j);
            }
        }
        // Initialisez tous les objets TicTacToeSquare de manière similaire

        // Lier les propriétés des objets TicTacToeSquare aux propriétés du modèle TicTacToeModel
        square00.ownerProperty().bindBidirectional(model.getSquare(0, 0));
        square00.winnerProperty().bind(model.getWinningSquare(0, 0));

        square01.ownerProperty().bindBidirectional(model.getSquare(0, 1));
        square01.winnerProperty().bind(model.getWinningSquare(0, 1));

        // Lier d'autres éléments de l'interface utilisateur aux propriétés du modèle TicTacToeModel
        restartButton.setOnAction(event -> model.restart());
        gameOverLabel.textProperty().bind(model.getEndOfGameMessage());
        freeCasesLabel.textProperty().bind(model.getFreeCasesMessage());
    }
}
