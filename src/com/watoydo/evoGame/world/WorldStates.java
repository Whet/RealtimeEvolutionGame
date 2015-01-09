package com.watoydo.evoGame.world;

import java.util.Random;

public enum WorldStates {

	OCCUPIED(1), EMPTY(0), IMMORTAL(20), DEFAULT(0), AI(20);

	private int code;

	private WorldStates(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public static WorldStates random(int dimension, Random random) {
		if(dimension == 0) {
			if(random.nextBoolean())
				return EMPTY;
			return OCCUPIED;
		}
		else if(dimension == 1) {
			if(random.nextBoolean())
				return IMMORTAL;
			return DEFAULT;
		}
		else if(dimension == 2) {
			if(random.nextBoolean())
				return AI;
			return DEFAULT;
		}
		
		return DEFAULT;
	}

	public static WorldStates getState(int i) {

		for(WorldStates worldState:values()) {
			if(worldState.getCode() == i)
				return worldState;
		}
		
		return null;
	}

	public static int getDimensions() {
		// Physical, Immortal, Ai
		return 3;
	}

	public static WorldStates parseState(String string) {
		if(string.equals("WorldStates.OCCUPIED"))
			return WorldStates.OCCUPIED;
		else if(string.equals("WorldStates.IMMORTAL"))
			return WorldStates.IMMORTAL;
		return WorldStates.DEFAULT;
	}

}
