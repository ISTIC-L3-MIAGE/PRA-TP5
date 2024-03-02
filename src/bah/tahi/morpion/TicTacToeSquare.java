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

	private final int row;
	private final int column;

	public TicTacToeSquare(final int row, final int column) {
		this.row = row;
		this.column = column;

		this.setEditable(false);
		this.setStyle("-fx-font-size: 24; -fx-alignment: center;");

		this.ownerProperty.addListener((observable, oldValue, newValue) -> {
			this.setText(newValue.toString());
			if (newValue != Owner.NONE) {
				setDisable(true);
			}
		});

		this.winnerProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				this.setStyle("-fx-font-size: 24; -fx-alignment: center; -fx-background-color: lightgreen;");
			}
		});

		this.setOnAction(event -> {
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