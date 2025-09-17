package com.csse3200.game.components;

public enum ComponentPriority {
	
	HIGH(1),
	MEDIUM(25),
	LOW(100);
	
	private final int value;
	
	ComponentPriority(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
