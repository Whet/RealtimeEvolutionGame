package com.watoydo.evoGame.ai.rules;

public enum MovementRule {

	MOVE_FORWARD(1),
	STAND_STILL(0),
	MOVE_BACKWARD(-1);
	
	private int code;

	private MovementRule(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static MovementRule getRule(int i) {
		for(MovementRule rule:values()) {
			if(rule.getCode() == i)
				return rule;
		}
		return null;
	}
	
}
