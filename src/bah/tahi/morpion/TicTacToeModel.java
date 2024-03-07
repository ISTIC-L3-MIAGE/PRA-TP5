package bah.tahi.morpion;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.collections.ObservableMap;

import static javafx.collections.FXCollections.observableHashMap;

public class TicTacToeModel {
    /**
     * Taille du plateau de jeu (pour être extensible).
     */
    private final static int BOARD_WIDTH = 3;
    private final static int BOARD_HEIGHT = 3;

    /**
     * Nombre de pièces alignées pour gagner (idem).
     */
    private final static int WINNING_COUNT = 3;

    /**
     * Scores des joueurs.
     */
    private final ObservableMap<Owner, IntegerProperty> scores = observableHashMap();

    /**
     * Joueur courant.
     */
    private final ObjectProperty<Owner> turn = new SimpleObjectProperty<>(Owner.FIRST);

    /**
     * Vainqueur du jeu, NONE si pas de vainqueur.
     */
    private final ObjectProperty<Owner> winner = new SimpleObjectProperty<>(Owner.NONE);

    /**
     * Plateau de jeu.
     */
    private final ObjectProperty<Owner>[][] board;

    /**
     * Positions gagnantes.
     */
    private final BooleanProperty[][] winningBoard;

    /**
     * Constructeur privé.
     */
    private TicTacToeModel() {
        // Initialisation du plateau de jeu et des positions gagnantes
        this.board = new SimpleObjectProperty[BOARD_HEIGHT][BOARD_WIDTH];
        this.winningBoard = new SimpleBooleanProperty[BOARD_HEIGHT][BOARD_WIDTH];

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                this.board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
                this.winningBoard[i][j] = new SimpleBooleanProperty(false);
            }
        }

        // Initialisation des scores
        this.scores.put(Owner.FIRST, new SimpleIntegerProperty(0));
        this.scores.put(Owner.SECOND, new SimpleIntegerProperty(0));
        this.scores.put(Owner.NONE, new SimpleIntegerProperty(this.getNbCases()));

        // Début de la partie
        this.restart();
    }

    /**
     * @return la seule instance possible du jeu.
     */
    public static TicTacToeModel getInstance() {
        return TicTacToeModelHolder.INSTANCE;
    }

    /**
     * @return la largeur du plateau.
     */
    public final int getBoardWidth() {
        return BOARD_WIDTH;
    }

    /**
     * @return la longueur du plateau.
     */
    public final int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    /**
     * @return le nombre de cases du plateau.
     */
    public final int getNbCases() {
        return BOARD_HEIGHT * BOARD_WIDTH;
    }

    /**
     * Permet de recommencer la partie.
     */
    public final void restart() {
        // Vider les cases du plateau
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                this.getSquare(i, j).set(Owner.NONE);
                this.getWinningSquare(i, j).set(false);
            }
        }

        // Réinitialisation des scores
        this.scores.get(Owner.FIRST).set(0);
        this.scores.get(Owner.SECOND).set(0);
        this.scores.get(Owner.NONE).set(this.getNbCases());

        // Réinitialisation des joueurs
        this.turnProperty().set(Owner.FIRST);
        this.winnerProperty().set(Owner.NONE);
    }

    /**
     * @return l'observateur du joueur courant
     */
    public final ObjectProperty<Owner> turnProperty() {
        return this.turn;
    }

    /**
     * @return l'observateur du vainqueur
     */
    public final ObjectProperty<Owner> winnerProperty() {
        return this.winner;
    }

    /**
     * @return l'observateur du propriétaire de la case (row, column)
     */
    public final ObjectProperty<Owner> getSquare(int row, int column) {
        return this.board[row][column];
    }

    public final BooleanProperty getWinningSquare(int row, int column) {
        return this.winningBoard[row][column];
    }

    /**
     * Cette fonction ne doit donner le bon résultat que si le jeu est terminé.
     * L’affichage peut être caché avant la fin du jeu.
     *
     * @return résultat du jeu sous forme de texte
     */
    public final StringExpression getEndOfGameMessage() {
        return new StringBinding() {
            {
                bind(winnerProperty(), getScore(Owner.NONE));
            }

            @Override
            protected String computeValue() {
                if (gameOver().get()) {
                    return switch (winnerProperty().get()) {
                        case FIRST -> "Game Over. Le gagnant est le premier joueur.";
                        case SECOND -> "Game Over. Le gagnant est le deuxième joueur.";
                        default -> "Game Over. Match nul.";
                    };
                } else {
                    return "Partie en cours";
                }
            }
        };
    }

    /**
     * Cette fonction permet de mettre en surbrillance le label du joueur courant
     *
     * @param owner le joueur concerné par le label
     * @return le style à affecter au label de score du joueur owner
     */
    public final StringExpression getOwnerLabelStyle(Owner owner) {
        return new StringBinding() {
            {
                bind(turnProperty(), getScore(Owner.NONE));
            }

            @Override
            protected String computeValue() {
                if (gameOver().get() || !turnProperty().get().equals(owner)) {
                    return "-fx-background-color: red; -fx-text-fill: white;";
                } else {
                    return "-fx-background-color: cyan; -fx-text-fill: black;";
                }
            }
        };
    }

    /**
     * @param winner le vainqueur de la partie
     */
    public void setWinner(Owner winner) {
        this.winnerProperty().set(winner);
    }

    /**
     * @param row    numéro de ligne
     * @param column numéro de colonne
     * @return true si les paramètres pointent sur une case du plateau de jeu
     */
    public boolean validSquare(int row, int column) {
        return 0 <= row && row < BOARD_HEIGHT && 0 <= column && column < BOARD_WIDTH;
    }

    /**
     * Changer le joueur courant.
     */
    public void nextPlayer() { // OK
        this.turnProperty().set(this.turnProperty().get().opposite());
    }

    /**
     * Met à jour le score d'un joueur.
     *
     * @param owner le joueur dont on doit mettre à jour le score
     */
    public void updateScore(Owner owner) {
        IntegerProperty ownerScore = this.scores.get(owner);
        IntegerProperty freeCases = this.scores.get(Owner.NONE);

        ownerScore.set(ownerScore.get() + 1);
        freeCases.set(freeCases.get() - 1);
    }

    /**
     * Jouer dans la case (row, column) quand c’est possible.
     *
     * @param row    numéro de ligne
     * @param column numéro de colonne
     */
    public void play(int row, int column) {
        if (this.validSquare(row, column) && this.legalMove(row, column).get()) {
            Owner currentPlayer = this.turnProperty().get(); // On récupère le joueur courant
            this.getSquare(row, column).set(currentPlayer); // On le définit comme propriétaire de la case (row, column)
            this.updateScore(currentPlayer); // On met à jour son score
            this.checkWinningCases(); // On vérifie ensuite si le coup joué engendre une victoire
            this.nextPlayer(); // Enfin, on passe la main au joueur suivant
        }
    }

    /**
     * Cette fonction parcours la matrice en ligne, colonne et diagonale pour détecter d'éventuels cas de victoire
     */
    public void checkWinningCases() {
        int i;

        // Parcours des lignes
        for (i = BOARD_HEIGHT - 1; i >= 0; i--) {
            if (checkRow(i)) {
                return;
            }
        }

        // Parcours des colonnes
        for (i = BOARD_WIDTH - 1; i >= 0; i--) {
            if (checkColumn(i)) {
                return;
            }
        }

        // Parcours des diagonales
        checkDiags();

        // Match nul si aucune victoire et plus de case libre
    }

    private boolean checkRow(int row) {
        Owner firstCell = getSquare(row, 0).get();
        if (firstCell == Owner.NONE) return false;

        for (int j = 1; j < BOARD_WIDTH; j++) {
            if (!getSquare(row, j).get().equals(firstCell)) {
                return false; // Pas de victoire dans cette ligne
            }
        }

        // Marquer la ligne comme gagnante
        markWinningRow(row);
        setWinner(firstCell);
        return true;
    }

    private boolean checkColumn(int column) {
        Owner firstCell = getSquare(0, column).get();
        if (firstCell == Owner.NONE) return false;

        for (int i = 1; i < BOARD_HEIGHT; i++) {
            if (!getSquare(i, column).get().equals(firstCell)) {
                return false; // Pas de victoire dans cette colonne
            }
        }

        // Marquer la colonne comme gagnante
        markWinningColumn(column);
        setWinner(firstCell);
        return true;
    }

    private boolean checkDiags() {
        // Vérifier la diagonale principale
        Owner topLeft = getSquare(0, 0).get();
        if (topLeft != Owner.NONE && topLeft == getSquare(1, 1).get() && topLeft == getSquare(2, 2).get()) {
            markWinningDiagonal(false);
            setWinner(topLeft);
            return true;
        }

        // Vérifier la diagonale inverse
        Owner topRight = getSquare(0, 2).get();
        if (topRight != Owner.NONE && topRight == getSquare(1, 1).get() && topRight == getSquare(2, 0).get()) {
            markWinningDiagonal(true);
            setWinner(topRight);
            return true;
        }

        return false;
    }

    private boolean checkDiags2() {
        int i, winningCounter = 0;
        Owner currentPlayer = this.turnProperty().get();

        // Vérification de la diagonale principale
        for (i = 0; i < BOARD_HEIGHT; i++) {
            if (currentPlayer.equals(this.getSquare(i, i).get())) {
                winningCounter++;
            }
        }

        if (winningCounter >= WINNING_COUNT) {
            markWinningDiagonal(false);
            setWinner(currentPlayer);
            return true;
        }

        // Vérification de la diagonale inverse
        winningCounter = 0;
        for (i = BOARD_HEIGHT - 1; i >= 0; i--) {
            if (currentPlayer.equals(this.getSquare(i, i).get())) {
                winningCounter++;
            }
        }

        if (winningCounter >= WINNING_COUNT) {
            markWinningDiagonal(true);
            setWinner(currentPlayer);
            return true;
        }

        // Pas de vistoire diagonale détectée
        return false;
    }

    /**
     * @return true si le plateau de jeu est totalement rempli (s'il n'y a plus de case libre), false sinon
     */
    private boolean fullBoard() {
        return this.getScore(Owner.NONE).intValue() == 0;
    }

    private void markWinningRow(int row) {
        for (int j = 0; j < BOARD_WIDTH; j++) {
            getWinningSquare(row, j).set(true);
        }
    }

    private void markWinningDiagonal(boolean reverse) {
        if (!reverse) {
            // Marquer la diagonale principale
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                getWinningSquare(i, i).set(true);
            }
        } else {
            // Marquer la diagonale secondaire
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                getWinningSquare(i, BOARD_WIDTH - 1 - i).set(true);
            }
        }
    }

    private void markWinningColumn(int column) {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            getWinningSquare(i, column).set(true);
        }
    }

    /**
     * @param row    numéro de ligne
     * @param column numéro de colonne
     * @return true s’il est possible de jouer dans la case, c’est-à-dire la case est libre et le jeu n’est pas terminé
     */
    public BooleanBinding legalMove(int row, int column) {
        return this.getSquare(row, column).isEqualTo(Owner.NONE).and(this.gameOver().not());
    }

    /**
     * @param owner le joueur concerné
     * @return Le nombre de coups joué par owner
     */
    public NumberExpression getScore(Owner owner) {
        return this.scores.get(owner);
    }

    /**
     * @return true si le jeu est terminé (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
     */
    public BooleanBinding gameOver() {
        return new BooleanBinding() {
            {
                bind(winnerProperty(), getScore(Owner.NONE));
            }

            @Override
            protected boolean computeValue() {
                return !winnerProperty().get().equals(Owner.NONE) || fullBoard();
            }
        };
    }

    /**
     * Classe interne selon le pattern singleton.
     */
    private static class TicTacToeModelHolder {
        private static final TicTacToeModel INSTANCE = new TicTacToeModel();
    }
}