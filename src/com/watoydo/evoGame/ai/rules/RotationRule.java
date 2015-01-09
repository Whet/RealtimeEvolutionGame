package com.watoydo.evoGame.ai.rules;

public enum RotationRule {

	TURN_RIGHT(1),
	NO_TURN(0),
	TURN_LEFT(-1);
	
	private int code;

	private RotationRule(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	public static RotationRule getRule(int i) {
		for(RotationRule rule:values()) {
			if(rule.getCode() == i)
				return rule;
		}
		return null;
	}
	
}
