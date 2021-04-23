package com.mysteria.sanity.sanity.enums;

public enum SanityPhase {

	PHASE_0(0, '\uE777'),
	PHASE_1(1, '\uE778'),
	PHASE_2(2, '\uE779'),
	PHASE_3(3, '\uE780'),
	PHASE_4(4, '\uE781'),
	PHASE_5(5, '\uE782'),
	PHASE_6(6, '\uE783'),
	PHASE_7(7, '\uE784'),
	PHASE_8(8, '\uE785');

	private final int weight;
	private final char character;

	SanityPhase(int weight, char character) {
		this.weight = weight;
		this.character = character;
	}

	public int getWeight() {
		return weight;
	}

	public char getChar() {
		return character;
	}

}
