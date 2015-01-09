package com.watoydo.evoGame.ai.rules;

import java.io.Serializable;
import java.util.Random;

import com.watoydo.evoGame.ai.Ai;
import com.watoydo.evoGame.world.WorldMap;
import com.watoydo.evoGame.world.WorldStates;
import com.watoydo.utils.Maths;

public class Rule implements Serializable {
	
	private static final double MOVEMENT_MUTATION = 0.1;
	private static final double ROTATION_MUTATION = 0.1;
	private static final double ROTATION_AMOUNT_MUTATION = 0.1;
	private static final int ROTATION_AMOUNT_MUTATION_QUANTITY = 5;
	private static final double WORLD_VIEW_MUTATION_RATE = 0.1;
	
	private int[][][] worldViewRequirement;
	
	private MovementRule movement;
	private RotationRule rotation;
	double phi;
	
	public Rule() {
		this.worldViewRequirement = new int[WorldMap.VIEW_SIZE][WorldMap.VIEW_SIZE][WorldStates.getDimensions()];
		
		for (int i = 0; i < worldViewRequirement.length; i++) {
			for (int j = 0; j < worldViewRequirement[i].length; j++) {
				for(int k = 0; k < worldViewRequirement[i][j].length; k++) {
					worldViewRequirement[i][j][k] = WorldStates.EMPTY.getCode();
				}
			}
		}
		
		this.movement = MovementRule.STAND_STILL;
		this.rotation = RotationRule.NO_TURN;
		this.phi = 0;
	}
	
	public void initialMutation(Random random) {
		
		this.movement = MovementRule.getRule(random.nextInt(3) - 1);
		this.rotation = RotationRule.getRule(random.nextInt(3) - 1);
		
		this.phi += Math.toRadians(random.nextInt(90)) * Maths.POM();
		
		for(int i = 0; i < this.worldViewRequirement.length; i++) {
			for(int j = 0; j < this.worldViewRequirement.length; j++) {
				for(int k = 0; k < WorldStates.getDimensions(); k++) {
					this.worldViewRequirement[i][j][k] = WorldStates.random(k, random).getCode();
				}
			}
		}
	}
	
	public void mutate(Random random) {
		if(random.nextDouble() < MOVEMENT_MUTATION) {
			this.movement = MovementRule.getRule(random.nextInt(3) - 1);
		}
		if(random.nextDouble() < ROTATION_MUTATION) {
			this.rotation = RotationRule.getRule(random.nextInt(3) - 1);
		}
		if(random.nextDouble() < ROTATION_AMOUNT_MUTATION) {
			this.phi += Math.toRadians(random.nextInt(ROTATION_AMOUNT_MUTATION_QUANTITY)) * Maths.POM();
		}
		
		for(int i = 0; i < this.worldViewRequirement.length; i++) {
			for(int j = 0; j < this.worldViewRequirement.length; j++) {
				if(Math.random() < WORLD_VIEW_MUTATION_RATE) {
					WorldStates currentState = WorldStates.getState(this.worldViewRequirement[i][j][0]);
					
					if(currentState == WorldStates.EMPTY)
						this.worldViewRequirement[i][j][0] = WorldStates.OCCUPIED.getCode();
					else if(currentState == WorldStates.OCCUPIED)
						this.worldViewRequirement[i][j][0] = WorldStates.EMPTY.getCode();
				}
			}
		}
	}
	
	public double difference(int[][][] otherWorld) {
		
		double dist = 0;
		
		for(int i = 0; i < otherWorld.length; i++) {
			for(int j = 0; j < otherWorld[i].length; j++) {
				for(int k = 0; k < otherWorld[i][j].length; k++) {
					dist += Math.abs(worldViewRequirement[i][j][k] - otherWorld[i][j][k]);
				}
			}
		}
		
		return dist;
	}

	public void apply(Ai ai) {
		ai.rotate(rotation, phi);
		ai.moveDirection(movement);
	}
	
	public Rule copy() {
		
		Rule rule = new Rule();
		
		for(int i = 0; i < this.worldViewRequirement.length; i++) {
			for(int j = 0; j < this.worldViewRequirement[i].length; j++) {
				rule.worldViewRequirement[i][j] = this.worldViewRequirement[i][j];
			}
		}
		
		rule.movement = this.movement;
		rule.rotation = this.rotation;
		rule.phi = this.phi;
		
		return rule; 
	}

}
