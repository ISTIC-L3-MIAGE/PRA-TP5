package bah.tahi.morpion;

import javafx.beans.property.IntegerProperty;

public enum Owner {
	NONE, FIRST, SECOND;

	public Owner opposite() {
        return switch (this) {
			case FIRST -> SECOND;
			case SECOND -> FIRST;
            default -> NONE;
        };
	}

	@Override
	public String toString() {
		return switch (this) {
			case FIRST -> "X";
			case SECOND -> "O";
			default -> "";
		};
	}
}