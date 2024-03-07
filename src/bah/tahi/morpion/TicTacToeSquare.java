package bah.tahi.morpion;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.swing.*;

public class TicTacToeSquare extends TextField {

	private static final TicTacToeModel model = TicTacToeModel.getInstance();

	private final ObjectProperty<Owner> owner = new SimpleObjectProperty<>(Owner.NONE);

	private final BooleanProperty winner = new SimpleBooleanProperty(false);

	public ObjectProperty<Owner> ownerProperty() {
		return owner;
	}

	public BooleanProperty winnerProperty() {
		return winner;
	}


	public TicTacToeSquare(final int row, final int column) {
		// Styles
		Font normalFont = new Font(30); // Police normale
		Font bigFont = new Font(50); // Police en cas de victoire
		Background whiteBg = new Background(new BackgroundFill(Color.WHITE, null, null)); // Le fond des cases vides en cours de partie
		Background greenBg = new Background(new BackgroundFill(Color.LIGHTGREEN, null, null)); // Le fond des cases vides en cours de partie
		Background redBg = new Background(new BackgroundFill(Color.RED, null, null)); // Le fond des cases vides à la fin d'une partie
		Border border = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(0.5))); // Bordure par défaut des cases

		this.setEditable(false); // Les cases ne sont pas modifiables au clavier
		this.setFocusTraversable(false); // Inutile de changer le focus des cases
		this.setFont(normalFont); // On applique la police normale à l'initialisation
		this.setBorder(border); // On applique les bordures à l'initialisation
		this.setAlignment(Pos.CENTER); // Centrer le texte dans une case
		this.setBackground(whiteBg); // Couleur par défaut d'une case
		this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Taille pour que la case occupe tout l'espace disponible dans se partie de la grille

		// Les observateurs
		this.ownerProperty().addListener((observable, oldValue, newValue) -> {
			this.setText(newValue.toString()); // On affecte la lettre correspondante au nouveau propriétaire de la case
			this.setDisable(!newValue.equals(Owner.NONE)); // On désactive la case si elle n'est plus vide
		});

		this.winnerProperty().addListener((observable, oldValue, newValue) -> {
			this.setFont(newValue ? bigFont : normalFont); // On agrandit la police si this est une case gagnante
		});

		// Les évènements liés à la souris
		this.setOnMouseEntered(event -> {
			this.setBackground(model.gameOver().get() ? redBg : greenBg); // On change la couleur de la case au survol de la souris et fonction de l'état de la partie
		});

		this.setOnMouseExited(event -> {
			this.setBackground(whiteBg); // On remets la couleur par défaut lorsque la souris sors de la case
		});

		this.setOnMouseClicked(event -> {
			model.play(row, column);  // Jouer dans la case (row, column)
			this.ownerProperty().set(model.getSquare(row, column).get()); // Définir le joueur courant en tant que propriétaire de la case
		});
	}
}