package bah.tahi.morpion;

import javafx.beans.binding.*;
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
	 * Scores des joueurs.
	 */
	private final ObservableMap<Owner, IntegerProperty> scores = observableHashMap();

	/**
	 * Nombre de pièces alignés pour gagner (idem).
	 */
	private final static int WINNING_COUNT = 3;
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
		// Board init
		this.board = new SimpleObjectProperty[BOARD_HEIGHT][BOARD_WIDTH];
		this.winningBoard = new SimpleBooleanProperty[BOARD_HEIGHT][BOARD_WIDTH];

		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				this.board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
				this.winningBoard[i][j] = new SimpleBooleanProperty(false);
			}
		}

		// Score init
		scores.put(Owner.FIRST, new SimpleIntegerProperty(0));
		scores.put(Owner.SECOND, new SimpleIntegerProperty(0));
		scores.put(Owner.NONE, new SimpleIntegerProperty(this.getNbCases()));

		// Square init
		this.restart();
	}

	/**
	 * @return la seule instance possible du jeu.
	 */
	public static TicTacToeModel getInstance() {
		return TicTacToeModelHolder.INSTANCE;
	}

	/**
	 * Classe interne selon le pattern singleton.
	 */
	private static class TicTacToeModelHolder {
		private static final TicTacToeModel INSTANCE = new TicTacToeModel();
	}

	public int getNbCases() {
		return BOARD_HEIGHT * BOARD_WIDTH;
	}

	public void restart() {
		// Vider les cases
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				this.getSquare(i, j).set(Owner.NONE);
				this.getWinningSquare(i, j).set(false);
			}
		}

		// Réinitialiser les scores
		this.scores.get(Owner.FIRST).set(0);
		this.scores.get(Owner.SECOND).set(0);
		this.scores.get(Owner.NONE).set(this.getNbCases());

		// Réinitialiser des joueurs
		this.turnProperty().set(Owner.FIRST);
		this.winnerProperty().set(Owner.NONE);
	}

	public final ObjectProperty<Owner> turnProperty() {
		return this.turn;
	}

	public final ObjectProperty<Owner> winnerProperty() {
		return this.winner;
	}

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
	 * Changer le joueur courant
	 */
	public void nextPlayer() { // OK
		this.turnProperty().set(this.turnProperty().get().opposite());
	}

	/**
	 * Met à jour les scores après chaque coup.
	 */
	public void updateScores(Owner owner) {
		IntegerProperty score = this.scores.get(owner);
		IntegerProperty freeCases = this.scores.get(Owner.NONE);

		score.set(score.get() + 1);
		freeCases.set(freeCases.get() - 1);
	}

	/**
	 * Jouer dans la case (row, column) quand c’est possible.
	 */
	public void play(int row, int column) {
		if (this.validSquare(row, column) && this.legalMove(row, column).get()) {
			this.getSquare(row, column).set(this.turnProperty().get());
			this.updateScores(this.turnProperty().get());
			this.checkWin();
			this.nextPlayer();
		}
	}

	public void checkWin() {
		// Parcourir les lignes
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			if (checkRow(i)) return;
		}

		// Parcourir les colonnes
		for (int j = 0; j < BOARD_WIDTH; j++) {
			if (checkColumn(j)) return;
		}

		// Vérifier les diagonales
		if (checkDiagonals()) return;

		// S'il n'y a pas de gagnant et que toutes les cases sont remplies, déclarer un match nul
		if (isBoardFull()) {
			setWinner(Owner.NONE); // Match nul
		}
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

	private boolean checkDiagonals() {
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

	private boolean isBoardFull() {
		return this.scores.get(Owner.FIRST).add(this.scores.get(Owner.SECOND)).isEqualTo(this.getNbCases()).get();
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
	 * @return true s’il est possible de jouer dans la case c’est-à-dire la case est
	 *         libre et le jeu n’est pas terminé
	 */
	public BooleanBinding legalMove(int row, int column) {
		return this.board[row][column].isEqualTo(Owner.NONE).and(this.gameOver().not());
	}

	/**
	 * @return Le nombre de coups joué par owner
	 */
	public NumberExpression getScore(Owner owner) { // OK
		return this.scores.get(owner);
	}

	/**
	 * @return true si le jeu est terminé (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
	 */
	public BooleanBinding gameOver() {
		return this.winner.isNotEqualTo(Owner.NONE).or(new BooleanBinding() {
			{
				bind(winnerProperty(), getScore(Owner.NONE));
			}
			@Override
			protected boolean computeValue() {
				return winnerProperty().get().equals(Owner.NONE) && getScore(Owner.NONE).getValue().equals(0);
			}
		});
	}
}