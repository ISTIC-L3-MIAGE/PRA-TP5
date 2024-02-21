package bah.tahi.morpion;

import java.awt.TextField;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TicTacToeSquare extends TextField {

	private static TicTacToeModel model = TicTacToeModel.getInstance();

	private ObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>(Owner.NONE);

	private BooleanProperty winnerProperty = new SimpleBooleanProperty(false);

	public ObjectProperty<Owner> ownerProperty() {

	}

	public BooleanProperty colorProperty() {

	}

	public TicTacToeSquare(final int row, final int column) {
	}
}