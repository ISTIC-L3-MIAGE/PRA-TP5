package bah.tahi.morpion;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class MorpionController {

    /**
     * Instance du modèle de jeu
     */
    private final TicTacToeModel model = TicTacToeModel.getInstance();
    private final int BOARD_HEIGHT = model.getBoardHeight();
    private final int BOARD_WIDTH = model.getBoardWidth();

    /**
     * Conteneur du plateau de jeu sur la vue
     */
    @FXML
    private AnchorPane gridContainer;

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

        // Initialisation de la grille et de son conteneur
        int i, j;
        double gridContainerHeight = gridContainer.getHeight();
        double gridContainerWidth = gridContainer.getWidth();
        double gridRowHeight = gridContainer.getPrefHeight() / BOARD_HEIGHT;
        double gridRowWidth = gridContainer.getPrefWidth() / BOARD_WIDTH;

        GridPane grid = new GridPane();
        grid.setMaxSize(gridContainerWidth, gridContainerHeight);

        for (i = 0; i < BOARD_HEIGHT; i++) {
            grid.getRowConstraints().add(new RowConstraints(gridRowHeight));
        }

        for (j = 0; j < BOARD_WIDTH; j++) {
            grid.getColumnConstraints().add(new ColumnConstraints(gridRowWidth));
        }

        // Ajout des cases à la grille
        for (i = 0; i < model.getBoardHeight(); i++) {
            for (j = 0; j < model.getBoardWidth(); j++) {
                TicTacToeSquare square = new TicTacToeSquare(i, j);
                grid.add(square, j, i);
            }
        }

        // Ajout de la grille à son conteneur
        gridContainer.getChildren().add(grid);

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
