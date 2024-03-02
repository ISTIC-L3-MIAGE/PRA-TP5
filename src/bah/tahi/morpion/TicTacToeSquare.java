package bah.tahi.morpion;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

public class TicTacToeSquare extends TextField {

	private static final TicTacToeModel model = TicTacToeModel.getInstance();

	private final ObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>(Owner.NONE);

	private final BooleanProperty winnerProperty = new SimpleBooleanProperty(false);

	public ObjectProperty<Owner> ownerProperty() {
		return ownerProperty;
	}

	public BooleanProperty colorProperty() {
		return winnerProperty;
	}


	public TicTacToeSquare(final int row, final int column) {

		this.setEditable(false);
		this.setFocusTraversable(false);
		this.setStyle("-fx-font-size: 24; -fx-alignment: center;");

		// Définir la taille maximale et minimale pour occuper 100% de l'espace disponible
		this.setMaxWidth(Double.MAX_VALUE);
		this.setMaxHeight(Double.MAX_VALUE);

		// Les écouteurs
		this.ownerProperty.addListener((observable, oldValue, newValue) -> {
			setText(newValue.toString());
			setDisable(newValue != Owner.NONE);
		});

		this.winnerProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				this.setStyle("-fx-font-size: 34; -fx-alignment: center;");
			}
		});

		// Définir la couleur de fond en fonction de l'état de la partie et du survol de la souris
		this.setOnMouseEntered(event -> {
			if (!model.gameOver().get()) {
				this.setStyle("-fx-font-size: 24; -fx-alignment: center; -fx-background-color: lightgreen;");
			} else {
				this.setStyle("-fx-font-size: 24; -fx-alignment: center; -fx-background-color: red;");
			}
		});

		this.setOnMouseExited(event -> {
			this.setStyle("-fx-font-size: 24; -fx-alignment: center;");
		});

		this.setOnMouseClicked(event -> {
			if (model.legalMove(row, column).get()) {
				model.play(row, column);
				ownerProperty.set(model.getSquare(row, column).get());
				if (model.gameOver().get()) {
					model.winnerProperty().addListener((observable, oldValue, newValue) -> {
						if (!newValue.equals(Owner.NONE)) {
							winnerProperty.set(true);
						}
					});
				}
			}
		});
	}
}