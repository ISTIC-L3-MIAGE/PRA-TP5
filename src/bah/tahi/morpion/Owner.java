package bah.tahi.morpion;

public enum Owner {
	NONE, FIRST, SECOND;

	public Owner opposite() {
		if (this == NONE) {
			return NONE;
		}
		return this == FIRST ? SECOND : FIRST;
	}
}