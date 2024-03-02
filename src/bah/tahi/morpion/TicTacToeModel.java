package bah.tahi.morpion;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.NumberExpression;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;

public class TicTacToeModel {
	/**
	 * Taille du plateau de jeu (pour être extensible).
	 */
	private final static int BOARD_WIDTH = 3;
	private final static int BOARD_HEIGHT = 3;
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
		this.board = new SimpleObjectProperty[BOARD_HEIGHT][BOARD_WIDTH];
		this.winningBoard = new SimpleBooleanProperty[BOARD_HEIGHT][BOARD_WIDTH];
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

	public void restart() {
		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				this.board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
			}
		}

		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				this.winningBoard[i][j] = new SimpleBooleanProperty(false);
			}
		}

		System.out.println("Restart");
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
		return null;
	}

	/**
	 * Cette fonction ne doit donner le bon résultat que si le jeu est terminé.
	 * L’affichage peut être caché avant la fin du jeu.
	 *
	 * @return résultat du jeu sous forme de texte
	 */
	public final StringExpression getEndOfGameMessage() {
		String msgString = "Partie en cours";

		if (this.gameOver().get()) {
			msgString = switch (this.winnerProperty().get()) {
				case Owner.FIRST -> "Game Over. Le gagnant est le premier joueur.";
				case Owner.SECOND -> "Game Over. Le gagnant est le deuxième joueur.";
				default -> "Game Over. Match nul.";
			};
		}

		return new SimpleStringProperty(msgString);
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
	 * Jouer dans la case (row, column) quand c’est possible.
	 */
	public void play(int row, int column) {
		this.board[row][column].set(this.turnProperty().get());
		this.nextPlayer();
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
		IntegerExpression score = new SimpleIntegerProperty(0);

		for (int i = 0; i < BOARD_HEIGHT; i++) {
			for (int j = 0; j < BOARD_WIDTH; j++) {
				if (this.board[i][j].get().equals(owner)) {
					score.add(1);
				}
			}
		}

		return score;
	}

	public final StringExpression getScoreMessage(Owner owner) {
		String msgString = this.getScore(owner).intValue() + " cases pour " + owner.toString();
		return new SimpleStringProperty(msgString);
	}

	/**
	 * @return true si le jeu est terminé (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
	 */
	public BooleanBinding gameOver() {
		int nbCases = BOARD_HEIGHT * BOARD_WIDTH;
		IntegerExpression scoreTotal = new SimpleIntegerProperty(0);

		return this.winner.isNotEqualTo(Owner.NONE).or(scoreTotal.isEqualTo(nbCases));
	}

}