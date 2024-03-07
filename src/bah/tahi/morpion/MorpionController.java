package bah.tahi.morpion;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class MorpionController {

    /**
     * Instance du modèle de jeu
     */
    private final TicTacToeModel model = TicTacToeModel.getInstance();

    /**
     * Plateau de jeu sur la vue
     */
    @FXML
    private GridPane grid;

    /**
     * Bouton RESTART
     */
    @FXML
    private Button restartButton;

    /**
     * Message de fin de partie
     */
    @FXML
    private Label gameOverLabel;

    /**
     * Nombre de cases libres
     */
    @FXML
    private Label freeCasesLabel;

    /**
     * Score du premier joueur
     */
    @FXML
    private Label firstCasesLabel;

    /**
     * Score du second joueur
     */
    @FXML
    private Label secondCasesLabel;

    @FXML
    public void initialize() {

        // Création des cases et liaison au modèle de jeu
        for (int i = 0; i < model.getBoardHeight(); i++) {
            for (int j = 0; j < model.getBoardWidth(); j++) {
                TicTacToeSquare square = new TicTacToeSquare(i, j);
                square.ownerProperty().bindBidirectional(model.getSquare(i, j));
                square.winnerProperty().bindBidirectional(model.getWinningSquare(i, j));
                grid.add(square, j, i);
            }
        }

        // Évènement du bouton RESTART
        restartButton.setOnAction(event -> model.restart());

        // Binding des labels
        gameOverLabel.textProperty().bind(model.getEndOfGameMessage());

        freeCasesLabel.visibleProperty().bind(model.gameOver().not());
        freeCasesLabel.textProperty().bind(model.getScore(Owner.NONE).asString().concat(" case(s) libre(s)"));

        firstCasesLabel.styleProperty().bind(model.getOwnerLabelStyle(Owner.FIRST));
        firstCasesLabel.textProperty().bind(model.getScore(Owner.FIRST).asString().concat(" case(s) pour X"));

        secondCasesLabel.styleProperty().bind(model.getOwnerLabelStyle(Owner.SECOND));
        secondCasesLabel.textProperty().bind(model.getScore(Owner.SECOND).asString().concat(" case(s) pour O"));
    }
}
